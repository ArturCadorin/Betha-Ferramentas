if (!TipoMatricula.APOSENTADO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para aposentados"
}
if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento não é calculado em processamentos de décimo terceiro"
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
} else {
    valorReferencia = Funcoes.diasaposent()
}
def remuneracao = Funcoes.remuneracao(matricula.tipo).valor
if (valorReferencia > 0 && remuneracao > 0) {
    valorCalculado = Funcoes.calcprop(remuneracao, valorReferencia)
    Bases.compor(valorReferencia, Bases.PAGAPROP)
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.PERIC,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.COMPHORAMES,
            Bases.FUNDFIN,
            Bases.PARCISENIRRF)
}

