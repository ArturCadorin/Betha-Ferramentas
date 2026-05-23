Funcoes.somenteFuncionarios()
if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && !TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento é calculado apenas em processamentos mensais e rescisórios"
}
if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
    suspender "Este evento não é calculado no processamento mensal (adiantamento)"
}
BigDecimal valorProvFerias = Eventos.valor(321)
BigDecimal valorProvAdiantamento = Eventos.valorCalculado(ClassificacaoEvento.ECONSIGNADOPROVISAO, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.ADIANTAMENTO, calculo.competencia)
valorCalculado = valorProvFerias + valorProvAdiantamento

