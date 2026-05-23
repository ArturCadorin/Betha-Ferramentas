Funcoes.somenteFuncionarios()
if (!(TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento))) {
    suspender "Este evento é calculado apenas no processamento mensal (adiantamento)"
}
if (evento.taxa <= 0) {
    suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
}
def emprestimos = Emprestimos.busca()
if (emprestimos.size() <= 0) {
    suspender "Não há empréstimos do eConsignado lançados na competência para este funcionário"
}
valorCalculado = 0
BigDecimal valor = 0
BigDecimal valorParcelasAcum = 0
BigDecimal valorMargem = Bases.valor(Bases.MARGECONSIG) * evento.taxa / 100
Boolean ultrapassouLimiteSaldo = false
emprestimos.each { e ->
    e.parcelas.each { p ->
        if (!ultrapassouLimiteSaldo) {
            valor = p.valorParcela
            valorParcelasAcum += valor
            BigDecimal valoresPagos = Emprestimos.valoresPagosEconsignado()
            if ((valorParcelasAcum + valoresPagos) > valorMargem) {
                valor = valorMargem - valoresPagos
                ultrapassouLimiteSaldo = true
            }
            valorCalculado += valor
        }
    }
}

