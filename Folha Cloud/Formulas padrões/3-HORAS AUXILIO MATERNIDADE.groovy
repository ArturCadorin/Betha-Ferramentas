Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afaslicmat = Funcoes.afaslicmat()
    if (afaslicmat <= 0) {
        suspender "Não há afastamento com a classificação 'Licença maternidade' na competência"
    }
    vaux = Funcoes.cnvdpbase(afaslicmat)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    Bases.compor(valorReferencia, Bases.PAGAPROP, Bases.MEDAUXMATPR)
    if (servidor.sexo == Sexo.MASCULINO) {
        Bases.compor(valorCalculado, Bases.SALBASE, Bases.PERIC, Bases.SIND, Bases.FGTS, Bases.IRRF, Bases.PREVEST, Bases.FUNDASS, Bases.FUNDOPREV, Bases.COMPHORAMES, Bases.FUNDFIN)
    } else {
        Bases.compor(valorCalculado, Bases.SALBASE, Bases.PERIC, Bases.SIND, Bases.FGTS, Bases.IRRF, Bases.INSS, Bases.PREVEST, Bases.FUNDASS, Bases.FUNDOPREV, Bases.COMPHORAMES, Bases.FUNDFIN)
    }
}

