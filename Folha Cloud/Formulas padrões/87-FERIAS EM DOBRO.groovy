Funcoes.somenteFuncionarios()
categorias = [
        'AGENTE_PUBLICO',
        'AGENTE_POLITICO',
        'SERVIDOR_PUBLICO_COMISSAO',
        'SERVIDOR_PUBLICO_EFETIVO'
]
categoria = funcionario.categoriaSefipVinculo.toString()
if (categorias.contains(categoria)) {
    suspender "Este evento não é calculado para funcionários com categoria SEFIP igual ou superior a 12"
}
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (folha.folhaPagamento) {
        folhas.buscaFolhas().each { f ->
            f.eventos.each { e ->
                if (e.codigo == evento.codigo) {
                    valorReferencia += e.referencia
                    valorCalculado += e.valor
                }
            }
        }
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado, Bases.IRRFFER)
        }
    } else {
        def vaux = Lancamentos.valor(evento)
        if (vaux >= 0) {
            valorCalculado = vaux
        } else {
            def dataFinalPeriodoAquisitivo = Datas.adicionaDias(periodoAquisitivo.dataFinal, 1)
            int mesesConcessao = periodoAquisitivo.configuracaoFerias.mesesParaConcessao
            dataFinalPeriodoAquisitivo = Datas.removeDias(Datas.adicionaMeses(dataFinalPeriodoAquisitivo, mesesConcessao), 1)
            int diasFerias
            if (periodoConcessao.dataInicioGozo > dataFinalPeriodoAquisitivo) {
                diasFerias = Funcoes.concessaoFeriasCompetencia(periodoConcessao.dataInicioGozo, periodoConcessao.dataFimGozo)
            } else {
                if (periodoConcessao.dataFimGozo > dataFinalPeriodoAquisitivo) {
                    diasFerias = Funcoes.concessaoFeriasCompetencia(dataFinalPeriodoAquisitivo, periodoConcessao.dataFimGozo) - 1
                }
            }
            int diasDireitoFerias = periodoAquisitivo.configuracaoFerias.diasParaAdquirirNoPeriodo
            def valorReferenciaAux = diasFerias / diasDireitoFerias
            if (valorReferenciaAux > 2) {
                valorReferenciaAux = 2
            }
            valorReferencia = valorReferenciaAux
            double valorCalculadoAux = Funcoes.remuneracao(matricula.tipo).valor * diasDireitoFerias / 30
            valorCalculado = valorCalculadoAux * valorReferenciaAux
        }
    }
}

