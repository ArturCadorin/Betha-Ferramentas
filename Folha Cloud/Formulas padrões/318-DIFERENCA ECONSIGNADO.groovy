Funcoes.somenteFuncionarios()
if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && !TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento é calculado apenas em processamentos mensais e rescisórios"
}
if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
    suspender "Este evento não é calculado no processamento mensal (adiantamento)"
}
BigDecimal dependencia = Eventos.valor(316) //apenas para forçar a dependência entre eventos
if (dependencia > 0) {
    valorCalculado = Emprestimos.diferencaPagamentoEconsignado()
}

