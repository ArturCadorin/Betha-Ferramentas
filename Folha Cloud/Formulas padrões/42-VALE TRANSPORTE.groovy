Funcoes.somenteFuncionarios()
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento não deve ser calculado em processamentos de férias"
}
if (Bases.valor(Bases.PAGAPROP) == 0) {
    suspender "O vale transporte não é calculado para funcionários sem valor de base 'Paga proporcional'"
}
valorReferencia = evento.taxa
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    vaux = ValesTransporte.busca(TipoRetornoValeTransporte.VALOR)
    if (vaux <= 0) {
        suspender "Não há valor de vale transporte a ser descontado do funcionário na competência"
    }
    valorCalculado = vaux
}

