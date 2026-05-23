Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afassuspcontajuizamentorescindapurfaltagrave = Funcoes.afassuspcontajuizamentorescindapurfaltagrave()
    if (afassuspcontajuizamentorescindapurfaltagrave <= 0) {
        suspender "Não há afastamento com as classificações 'Suspensão contratual decorrente de ajuizamento de reclamação trabalhista pleiteando rescisão indireta do contrato' ou 'Suspensão contratual para ajuizamento de inquérito para apuração de falta grave' na competência"
    }
    vaux = Funcoes.cnvdpbase(afassuspcontajuizamentorescindapurfaltagrave)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
}

