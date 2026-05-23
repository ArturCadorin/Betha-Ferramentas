Funcoes.somenteFuncionarios()
if (!TipoAdmissao.TRANSFERENCIA.equals(funcionario.tipoAdmissao)) {
    suspender "Este cálculo é executado apenas para funcionários recebidos por transferência"
}
if (!funcionario.responsabilidadePagamento) {
    suspender "A responsabilidade pelo pagamento do funcionário não foi definida. Acesse os dados do contrato do funcionário e selecione qual a responsabilidade pelo pagamento entre cedente/origem e cessionário/destino"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def recebidocomonus = Funcoes.recebidocomonus()
    if (recebidocomonus <= 0) {
        suspender "Não há dias trabalhados na competência ou a responsabilidade pelo pagamento do funcionário é diferente da classificação 'Pagamento exclusivamente pelo cessionário/destino' ou 'Pagamento pelo cedente/origem e pelo cessionário/destino'"
    }
    vaux = Funcoes.cnvdpbase(recebidocomonus)
    valorReferencia = vaux
}
def salario = funcionario.salario
if (salario <= 0) {
    suspender "Não há valor de salário para o funcionário na competência"
}
vaux = Funcoes.calcprop(salario, vaux)
valorCalculado = vaux.round(2)
if (valorCalculado > 0) {
    Bases.compor(salario, Bases.HORAEXTRA)
    Bases.compor(valorReferencia, Bases.PAGAPROP)
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.PERIC,
            Bases.SIND,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.COMPHORAMES,
            Bases.FUNDFIN)
}

