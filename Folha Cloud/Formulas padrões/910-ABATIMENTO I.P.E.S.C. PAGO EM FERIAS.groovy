Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.PREVIDENCIA_ESTADUAL)) {
    double valorAbatimento
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorAbatimento = vvar
    } else {
        def valorIpescFeriasIntegral = Funcoes.getValorCodigoEventoFerias(89, true).valor
        if (valorIpescFeriasIntegral > 0) {
            valorAbatimento = Eventos.valor(905)
        }
    }
    if (valorAbatimento > 0) {
        valorCalculado = valorAbatimento
        evento.replicado(true)
    }
}

