if (TipoMatricula.FUNCIONARIO.equals(matricula.tipo)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorReferencia = vvar
        valorCalculado = Funcoes.calcprop(valorReferencia, Bases.valor(Bases.PAGAPROP))
        Bases.compor(valorReferencia, Bases.SALAFAM)
        Bases.compor(valorCalculado,
                Bases.INSS,
                Bases.PREVEST,
                Bases.FUNDASS,
                Bases.FUNDOPREV,
                Bases.COMPHORAMES,
                Bases.FUNDFIN,
                Bases.SALBASE,
                Bases.PERIC,
                Bases.SIND,
                Bases.FGTS,
                Bases.IRRF)
    }
}

