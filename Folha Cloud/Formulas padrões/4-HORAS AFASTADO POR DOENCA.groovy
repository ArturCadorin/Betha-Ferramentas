Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasauxdoenc = Funcoes.afasauxdoenc()
    if (afasauxdoenc <= 0) {
        suspender "Não há afastamento com as classificações 'Auxílio doença previdência' ou 'Doença do trabalho previdência' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasauxdoenc)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
}

