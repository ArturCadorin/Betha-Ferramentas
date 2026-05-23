Funcoes.somenteFuncionarios()
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo, [], [], [], [], true)
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
            if (Eventos.valor(75) == 0) {
                Bases.compor(funcionario.salario, Bases.SIND)
            }
            Bases.compor(valorReferencia,
                    Bases.PAGAPROP)
            Bases.compor(valorCalculado,
                    Bases.FGTS,
                    Bases.IRRFFER,
                    Bases.INSSFER,
                    Bases.PREVESTFER,
                    Bases.FUNDASSFER,
                    Bases.FUNDOPREVFER,
                    Bases.COMPHORAMES,
                    Bases.FUNDFINFER)
        }
    } else {
        def diasferias = folha.diasFeriasColetivas
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
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado, Bases.MARGECONSIG)
        }
    }
}
