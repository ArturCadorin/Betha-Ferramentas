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
double remuneracao = funcionario.salario
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (folha.folhaPagamento) {
        valorCalculado = 0
        valorReferencia = 0
        folhas.buscaFolhas().each { f ->
            f.eventos.each { e ->
                if (e.codigo == evento.codigo) {
                    valorCalculado += e.valor
                    valorReferencia += e.referencia
                }
            }
        }
        if (valorCalculado > 0) {
            Bases.compor(remuneracao, Bases.SIND)
            Bases.compor(valorReferencia,
                    Bases.PAGAPROP)
            Bases.compor(valorCalculado,
                    Bases.FGTS,
                    Bases.IRRFFER,
                    Bases.INSSFER,
                    Bases.FUNDOPREVFER,
                    Bases.FUNDASSFER,
                    Bases.PREVESTFER,
                    Bases.FUNDFINFER,
                    Bases.COMPHORAMES)
        }
    } else {
        def vaux = Lancamentos.valor(evento)
        if (vaux >= 0) {
            valorReferencia = vaux
        } else {
            def diasferias = folha.diasGozo
            if (diasferias > 0) {
                vaux = Funcoes.cnvdpbase(diasferias)
                valorReferencia = vaux
            }
        }
        if (vaux > 0) {
            valorCalculado = Funcoes.calcprop(remuneracao, vaux)
        }
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado, Bases.MARGECONSIG)
        }
    }
}
