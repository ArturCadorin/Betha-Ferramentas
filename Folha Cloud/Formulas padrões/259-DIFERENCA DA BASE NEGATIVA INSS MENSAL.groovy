def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorReferencia = vvar
    valorCalculado = vvar
    Bases.compor(valorCalculado, Bases.INSS)
}

