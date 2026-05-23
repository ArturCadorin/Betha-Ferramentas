Funcoes.somenteFuncionarios()
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento não deve ser calculado em processamentos de férias"
}
if (Bases.valor(Bases.PAGAPROP) == 0) {
    suspender "O vale transporte não é calculado para funcionários sem valor de base 'Paga proporcional'"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    vaux = ValesTransporte.busca(TipoRetornoValeTransporte.VALOR_ENTIDADE)
    if (vaux <= 0) {
        suspender "Não há valor de vale transporte lançado como valor gasto pelo empregador nesta competência"
    }
    valorReferencia = ValesTransporte.busca(TipoRetornoValeTransporte.QUANTIDADE)
    valorCalculado = vaux
}

