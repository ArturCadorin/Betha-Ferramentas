Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasadocao = Funcoes.afasadocao()
    if (afasadocao <= 0) {
        suspender "Não há afastamento com a classificação 'Licença maternidade - Adoção/guarda judicial de criança' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasadocao)
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

