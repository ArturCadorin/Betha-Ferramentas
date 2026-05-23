if (!TipoMatricula.FUNCIONARIO.equals(matricula.tipo) && !TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para funcionários e estagiários"
}
vaux = Lancamentos.valor(evento)
if (vaux <= 0) {
    vaux = Funcoes.buscaFaltas(TipoFalta.NAO_JUSTIFICADA, calculo.competencia)
    if (vaux <= 0) {
        suspender "Não foram encontradas faltas para a matrícula na competência com os dados buscados"
    }
}
valorReferencia = vaux
if (valorReferencia > 0) {
    double remuneracao = Funcoes.remuneracao(matricula.tipo).valor
    valorCalculado = Funcoes.calcprop(remuneracao, valorReferencia)
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

