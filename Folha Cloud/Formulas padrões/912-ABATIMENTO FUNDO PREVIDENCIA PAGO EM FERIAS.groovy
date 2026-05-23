Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.PREVIDENCIA_PROPRIA)) {
    double valorAbatimento
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorAbatimento = vvar
    } else {
        def valorFundoPrevFeriasIntegral = Funcoes.getValorCodigoEventoFerias(91, true).valor
        if (valorFundoPrevFeriasIntegral > 0) {
            valorAbatimento = Eventos.valor(907)
        }
    }
    if (valorAbatimento > 0) {
        valorCalculado = valorAbatimento
        evento.replicado(true)
    }
}

