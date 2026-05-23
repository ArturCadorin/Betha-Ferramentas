Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidenciaFederal) {
    double valorAbatimento
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorAbatimento = vvar
    } else {
        def valorInssFeriasIntegral = Funcoes.getValorCodigoEventoFerias(88, true).valor
        if (valorInssFeriasIntegral > 0) {
            valorAbatimento = Eventos.valor(902)
        }
    }
    if (valorAbatimento > 0) {
        valorCalculado = valorAbatimento
        evento.replicado(true)
    }
}

