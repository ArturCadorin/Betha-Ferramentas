Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afaslicsvenc = Funcoes.afasReintegracaoComPerdaDireito()
    if (afaslicsvenc <= 0) {
        suspender "Não há afastamento com a classificação 'Reintegração com perdas de direito' na competência"
    }
    vaux = Funcoes.cnvdpbase(afaslicsvenc)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
}

