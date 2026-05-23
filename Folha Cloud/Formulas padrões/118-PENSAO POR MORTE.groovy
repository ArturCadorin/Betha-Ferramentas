if (!TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para pensionistas"
}
if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento não é calculado em processamentos de décimo terceiro"
}
valorCalculado = 0
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorCalculado = vaux
} else {
    vaux = Funcoes.diaspensionista()
    def remuneracao = Funcoes.remuneracao(matricula.tipo).valor
    if (vaux > 0 && remuneracao > 0) {
        valorCalculado = Funcoes.calcprop(remuneracao, vaux)
    }
}
if (valorCalculado > 0) {
    Bases.compor(valorCalculado,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.FUNDFIN,
            Bases.SALAFAM,
            Bases.PARCISENIRRF)
}

