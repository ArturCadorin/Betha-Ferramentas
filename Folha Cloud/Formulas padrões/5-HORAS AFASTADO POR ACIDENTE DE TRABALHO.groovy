Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasacidtrab = Funcoes.afasacidtrab()
    if (afasacidtrab <= 0) {
        suspender "Não há afastamento com a classificação 'Acidente de trabalho previdência' ou 'Acidente trabalho trajeto típico previdência' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasacidtrab)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    Bases.compor(valorCalculado, Bases.FGTS,Bases.INSS, Bases.COMPHORAMES)
}

