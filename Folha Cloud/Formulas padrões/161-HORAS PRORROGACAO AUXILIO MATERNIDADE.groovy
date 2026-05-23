Funcoes.somenteFuncionarios()
if (!servidor.sexo.equals(Sexo.FEMININO)) {
    suspender "Este cálculo é realizado apenas para funcionários do sexo feminino"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasprorroglicmat = Funcoes.afasprorroglicmat()
    if (afasprorroglicmat <= 0) {
        suspender "Não há afastamento com a classificação 'Licença maternidade - Antecipação ou prorrogação' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasprorroglicmat)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    Bases.compor(valorReferencia, Bases.PAGAPROP, Bases.MEDAUXMATPR)
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

