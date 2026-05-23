def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorCalculado = vvar
    valorReferencia = vvar
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.HORAEXTRA,
            Bases.PERIC,
            Bases.SIND,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.COMPHORAMES,
            Bases.FUNDFIN
    )
}

