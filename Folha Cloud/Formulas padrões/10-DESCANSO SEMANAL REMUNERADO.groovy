def vvar = Lancamentos.valor(evento)
def vaux = calculo.quantidadeDiasCompetencia - vvar
if (vvar > 0) {
    vaux = (Eventos.valor(22) + Eventos.valor(23) + Eventos.valor(24)) / vaux * vvar
    valorReferencia = vvar
    valorCalculado = vaux
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
}

