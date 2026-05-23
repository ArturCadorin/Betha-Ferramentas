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
    valorReferencia = evento.taxa
    if (folha.folhaPagamento) {
        folhas.buscaFolhas().each { f ->
            f.eventos.each { e ->
                if (e.codigo == evento.codigo) {
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
            valorCalculado = Eventos.valor(87) * evento.taxa / 100
        }
    }
}

