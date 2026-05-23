vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorReferencia = vvar
    valorCalculado = vvar
} else {
    valorReferencia = evento.taxa
    valorCalculado = Eventos.valor(2) * evento.taxa / 100
}
if (valorCalculado < 0) {
    valorCalculado = 0
}
Bases.compor(valorCalculado,
        Bases.SALBASE,
        Bases.FGTS,
        Bases.IRRF,
        Bases.INSS,
        Bases.PREVEST,
        Bases.FUNDASS,
        Bases.FUNDOPREV,
        Bases.SALAFAM,
        Bases.MEDIAUXMAT,
        Bases.FUNDFIN)

