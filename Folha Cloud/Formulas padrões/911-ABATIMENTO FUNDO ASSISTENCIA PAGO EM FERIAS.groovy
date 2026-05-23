Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.ASSISTENCIA_MUNICIPAL)) {
    double valorAbatimento
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorAbatimento = vvar
    } else {
        def valorFundoAssistFeriasIntegral = Funcoes.getValorCodigoEventoFerias(90, true).valor
        if (valorFundoAssistFeriasIntegral > 0) {
            valorAbatimento = Eventos.valor(906)
        }
    }
    if (valorAbatimento > 0) {
        valorCalculado = valorAbatimento
        evento.replicado(true)
    }
}

