Funcoes.somenteFuncionarios()
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
if (valorFerias.valor > 0) {
    Bases.compor(valorFerias.valor,
            Bases.INSSFER,
            Bases.FUNDOPREVFER,
            Bases.FUNDASSFER,
            Bases.PREVESTFER,
            Bases.FUNDFINFER,
            Bases.FGTS)
}
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    def diasGozo = periodoConcessao.diasGozo
    if (folha.folhaPagamento) {
        valorReferencia = diasGozo
        valorCalculado = 0
        folhas.buscaFolhas().each { f ->
            f.eventos.each { e ->
                if (e.codigo == evento.codigo) {
                    valorCalculado += e.valor
                }
            }
        }
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado,
                    Bases.FGTS,
                    Bases.IRRFFER,
                    Bases.INSSFER,
                    Bases.FUNDOPREVFER,
                    Bases.FUNDASSFER,
                    Bases.PREVESTFER,
                    Bases.FUNDFINFER,
                    Bases.MEDIAUXMAT,
                    Bases.SALAFAM)
        }
    } else {
        def vaux = Lancamentos.valor(evento)
        def diasferias = folha.diasGozo
        valorReferencia = diasferias
        if (vaux > 0) {
            valorCalculado = vaux / diasGozo * valorReferencia
        } else {
            def vprop = mediaVantagem.calcular(periodoAquisitivo)
            if (vprop <= 0) {
                suspender "Não há média valores de férias a serem pagas ao funcionário"
            }
            valorCalculado = vprop
        }
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado, Bases.MARGECONSIG)
        }
    }
}

