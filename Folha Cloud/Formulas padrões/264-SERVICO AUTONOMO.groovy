if (!TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para autônomos"
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorCalculado = vaux
} else {
    double valorServicos = autonomo.totalServicosAutonomo
    if (valorServicos <= 0) {
        suspender "Não há valor de serviços lançados para o autônomo na competência"
    }
    if (autonomo.codESocial.equals('711') || autonomo.codESocial.equals('712')) {
        double descontoTranspPassageirosOuCarga = Eventos.valor(285) + Eventos.valor(286)
        valorServicos -= descontoTranspPassageirosOuCarga
        if (valorServicos <= 0) {
            suspender "Para o transportador autônomo de passageiros ou carga não há valor de serviço a ser pago na competência"
        }
    }
    valorCalculado = valorServicos
}
Bases.compor(valorCalculado,
        Bases.SALBASE,
        Bases.INSS,
        Bases.IRRF,
        Bases.COMPHORAMES)

