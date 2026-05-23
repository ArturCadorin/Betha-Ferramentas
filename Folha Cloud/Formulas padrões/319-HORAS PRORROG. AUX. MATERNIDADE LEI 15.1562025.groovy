Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasprorroglicmatlei15156 = Funcoes.afasprorroglicmatlei15156()
    if (afasprorroglicmatlei15156 <= 0) {
        suspender "Não há afastamento com a classificação 'Licença maternidade - Prorrogação 60 dias, Lei 15.156/2025 - deficiência associada ao Zica vírus' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasprorroglicmatlei15156)
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

