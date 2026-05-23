Funcoes.somenteFuncionarios()
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (folha.folhaPagamento) {
        def diasAbono = folha.diasAbono
        if (diasAbono <= 0) {
            suspender "Não há dias de abono pecuniário lançados em férias"
        }
        def vaux = Lancamentos.valor(evento)
        if (vaux >= 0) {
            valorReferencia = vaux
        } else {
            vaux = Funcoes.cnvdpbase(diasAbono)
            valorReferencia = vaux
        }
        if (vaux > 0) {
            valorCalculado = Funcoes.calcprop(funcionario.salario, vaux)
        }
    }
}

