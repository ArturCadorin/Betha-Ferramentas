if (!TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para estagiários"
}
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
double remuneracao = estagiario.bolsaEstudos
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (folha.folhaPagamento) {
        valorReferencia = 0
        valorCalculado = 0
        folhas.buscaFolhas().each { f ->
            f.eventos.each { e ->
                if (e.codigo == evento.codigo) {
                    valorReferencia += e.referencia
                    valorCalculado += e.valor
                }
            }
        }
        if (valorCalculado > 0) {
            Bases.compor(valorReferencia, Bases.PAGAPROP)
            Bases.compor(valorCalculado, Bases.COMPHORAMES, Bases.IRRFFER)
        }
    } else {
        def diasferias = folha.diasGozo
        def vaux = Lancamentos.valor(evento)
        if (vaux >= 0) {
            valorReferencia = vaux
        } else {
            if (diasferias > 0) {
                vaux = Funcoes.cnvdpbase(diasferias)
                valorReferencia = vaux
            }
        }
        if (vaux > 0) {
            valorCalculado = Funcoes.calcprop(remuneracao, vaux)
        }
    }
}

