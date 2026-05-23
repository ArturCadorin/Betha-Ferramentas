vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    valorReferencia = Funcoes.getValorBaseMultiplosVinculos(Bases.IRRF13, calculo.tipoProcessamento, calculo.subTipoProcessamento)
}
valorCalculado = valorReferencia

