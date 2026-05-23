def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (folha.folhaPagamento) {
        def diasAbono = periodoConcessao.diasAbono
        if (diasAbono <= 0) {
            suspender "Não há dias de abono pecuniário lançados em férias"
        }
        def vaux = Lancamentos.valor(evento)
        if (vaux > 0) {
            valorReferencia = vaux
            valorCalculado = vaux
        } else {
            valorReferencia = diasAbono
            def vint = mediaVantagem.calcular()
            if (vint <= 0) {
                suspender "Não há média valores de abono pecuniário a serem pagas ao funcionário"
            }
            valorCalculado = vint
        }
    }
}

