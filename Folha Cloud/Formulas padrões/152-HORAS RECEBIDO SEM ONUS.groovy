Funcoes.somenteFuncionarios()
if (!funcionario.codESocial.equals("410")) {
    suspender "Este cálculo é executado apenas para funcionários recebidos por transferência"
}
if (!funcionario.responsabilidadePagamento) {
    suspender "A responsabilidade pelo pagamento do funcionário não foi definida. Acesse os dados do contrato do funcionário e selecione qual a responsabilidade pelo pagamento entre cedente/origem e cessionário/destino"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def recebidosemonus = Funcoes.recebidosemonus()
    if (recebidosemonus <= 0) {
        suspender "Não há dias trabalhados na competência ou a responsabilidade pelo pagamento do funcionário é diferente da classificação 'Pagamento exclusivamente pelo cedente/origem' ou 'Pagamento pelo cedente/origem com ressarcimento pelo cessionário/destino'"
    }
    vaux = Funcoes.cnvdpbase(recebidosemonus)
    valorReferencia = vaux
}
double salario = funcionario.salario
if (salario <= 0) {
    suspender "Não há valor de salário para o funcionário na competência"
}
double remuneracao = Funcoes.calcprop(salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
}

