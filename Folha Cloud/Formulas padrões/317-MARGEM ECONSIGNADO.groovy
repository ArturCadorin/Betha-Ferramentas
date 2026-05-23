Funcoes.somenteFuncionarios()
if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && !TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento é calculado apenas em processamentos mensais e rescisórios"
}
if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
    suspender "Este evento não é calculado no processamento mensal (adiantamento)"
}
if (!Emprestimos.existeEmprestimoEconsignado()) {
    suspender "Não há empréstimos do eConsignado lançados na competência para este funcionário"
}
if (evento.taxa <= 0) {
    suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
}
BigDecimal baseInssCompetencia = Bases.valor(Bases.INSS) + Bases.valor(Bases.INSSFER)
if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    baseInssCompetencia += Bases.valor(Bases.INSS13)
}
BigDecimal baseCalculoMargemEConsignado = baseInssCompetencia + Bases.valor(Bases.MARGECONSIG)
if (baseCalculoMargemEConsignado > 0) {
    valorCalculado = baseCalculoMargemEConsignado * evento.taxa / 100
}

