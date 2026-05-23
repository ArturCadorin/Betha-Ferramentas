Funcoes.somenteFuncionarios()
if (!servidor.sexo.equals(Sexo.FEMININO)) {
    suspender "Este cálculo é realizado apenas para funcionários do sexo feminino"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasprorroglicmatlei11770 = Funcoes.afasprorroglicmatlei11770()
    if (afasprorroglicmatlei11770 <= 0) {
        suspender "Não há afastamento com a classificação 'Licença maternidade - Prorrogação 60 dias, Lei 11.770/2008' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasprorroglicmatlei11770)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    Bases.compor(valorReferencia, Bases.PAGAPROP, Bases.MEDAUXMATPR)
    if (Sexo.MASCULINO.equals(servidor.sexo)) {
        Bases.compor(valorCalculado, Bases.SALBASE, Bases.PERIC, Bases.SIND, Bases.FGTS, Bases.IRRF, Bases.PREVEST, Bases.FUNDASS, Bases.FUNDOPREV, Bases.COMPHORAMES, Bases.FUNDFIN)
    } else {
        Bases.compor(valorCalculado, Bases.SALBASE, Bases.PERIC, Bases.SIND, Bases.FGTS, Bases.IRRF, Bases.INSS, Bases.PREVEST, Bases.FUNDASS, Bases.FUNDOPREV, Bases.COMPHORAMES, Bases.FUNDFIN)
    }
}

