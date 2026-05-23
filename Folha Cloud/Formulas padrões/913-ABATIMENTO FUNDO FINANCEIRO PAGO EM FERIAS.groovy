Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.FUNDO_FINANCEIRO)) {
    double valorAbatimento
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorAbatimento = vvar
    } else {
        def valorFundoFinancFeriasIntegral = Funcoes.getValorCodigoEventoFerias(249, true).valor
        if (valorFundoFinancFeriasIntegral > 0) {
            valorAbatimento = Eventos.valor(908)
        }
    }
    if (valorAbatimento > 0) {
        valorCalculado = valorAbatimento
        evento.replicado(true)
    }
}

