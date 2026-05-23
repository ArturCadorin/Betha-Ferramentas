Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    valorReferencia = Funcoes.getValorBaseMultiplosVinculos(Bases.INSS13, calculo.tipoProcessamento, calculo.subTipoProcessamento)
}
valorCalculado = valorReferencia

