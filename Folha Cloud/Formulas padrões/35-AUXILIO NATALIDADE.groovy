def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorReferencia = vvar
    valorCalculado = vvar
} else {
    valorReferencia = evento.taxa
    valorCalculado = (EncargosSociais.salarioMinimo * valorReferencia) / 100
}

