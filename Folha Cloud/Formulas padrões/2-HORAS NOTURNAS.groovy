Funcoes.somenteFuncionarios()
valorReferencia = Lancamentos.valor(evento)
if (valorReferencia > 0) {
    valorCalculado = Funcoes.calcprop(funcionario.salario, valorReferencia)
    Bases.compor(valorReferencia, Bases.PAGAPROP)
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.PERIC,
            Bases.SIND,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.COMPHORAMES,
            Bases.FUNDFIN)
}

