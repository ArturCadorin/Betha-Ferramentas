Funcoes.somenteFuncionarios()
if (Funcoes.quantPrevidenciasAtivas() > 0) {
    double valorRetido
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorRetido = vvar
    } else {
        double valorPrevidFeriasIntegral = Funcoes.getValorCodigoEventoFerias(88, true).valor + Funcoes.getValorCodigoEventoFerias(89, true).valor + Funcoes.getValorCodigoEventoFerias(90, true).valor + Funcoes.getValorCodigoEventoFerias(91, true).valor + Funcoes.getValorCodigoEventoFerias(249, true).valor
        double valorPrevidFeriasDoMes = Eventos.valor(902) + Eventos.valor(905) + Eventos.valor(906) + Eventos.valor(907) + Eventos.valor(908)
        if (valorPrevidFeriasIntegral > 0 && valorPrevidFeriasDoMes > 0) {
            valorRetido = valorPrevidFeriasIntegral - valorPrevidFeriasDoMes
        }
    }
    if (valorRetido > 0) {
        valorCalculado = valorRetido
        evento.replicado(true)
    }
}

