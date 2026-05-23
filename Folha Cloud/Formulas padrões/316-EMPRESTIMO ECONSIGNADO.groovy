Funcoes.somenteFuncionarios()
if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && !TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento é calculado apenas em processamentos mensais e rescisórios"
}
if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
    suspender "Este evento não é calculado no processamento mensal (adiantamento)"
}
valorCalculado = 0
BigDecimal valor = 0
def emprestimos = Emprestimos.busca()
if (emprestimos.size() <= 0) {
    suspender "Não há empréstimos do eConsignado lançados na competência para este funcionário"
}
emprestimos.each { e ->
    e.parcelas.each { p ->
        valor = p.valorParcela
        BigDecimal valoresPagos = Emprestimos.valoresPagosEconsignado()
        if ((valor + valoresPagos) > Eventos.valor(317)) {
            valor = Eventos.valor(317) - valoresPagos
        }
        Emprestimos.pagarParcela(e.id, p.id, valor)
        valorCalculado += valor
    }
}

