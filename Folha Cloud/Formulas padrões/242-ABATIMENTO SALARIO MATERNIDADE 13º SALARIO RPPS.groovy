Funcoes.somenteFuncionarios()
if (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && !(TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.INTEGRAL.equals(calculo.subTipoProcessamento))) {
    suspender "Este evento deve ser calculado apenas nos processamentos de rescisão e décimo terceiro (integral)"
}
if (!Funcoes.permiteCalculoAuxilioMaternidade()) {
    suspender "A matrícula não tem direito a receber o abatimento salário maternidade"
}
if (!Funcoes.possuiPrevidencia(TipoPrevidencia.PREVIDENCIA_PROPRIA)) {
    suspender "Este cálculo é realizado apenas para matrículas contribuintes da previdência própria"
}
if (!Funcoes.permitecalc13integral()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro ou o seu período aquisitivo contém uma situação não permitida para o cálculo do evento'
}
double base = Bases.valor(Bases.ABTMAT13RPPS)
if (base <= 0) {
    suspender "Não há valor de base de abatimento de auxílio maternidade de décimo terceiro salário - RPPS para cálculo"
}
double vlrCalc
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    vlrCalc = vvar
} else {
    vlrCalc = base
}
valorCalculado = vlrCalc - Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

