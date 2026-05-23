Funcoes.somenteFuncionarios()
valorReferencia = Lancamentos.valor(evento)
if (valorReferencia > 0) {
    valorCalculado = Funcoes.calcprop(funcionario.salario, valorReferencia)
    Bases.compor(valorCalculado,
            Bases.SALBASE,
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

