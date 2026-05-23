Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasservmil = Funcoes.afasservmil()
    if (afasservmil <= 0) {
        suspender "Não há afastamento com a classificação 'Serviço militar' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasservmil)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    Bases.compor(valorCalculado, Bases.FGTS, Bases.INSS, Bases.COMPHORAMES)
}

