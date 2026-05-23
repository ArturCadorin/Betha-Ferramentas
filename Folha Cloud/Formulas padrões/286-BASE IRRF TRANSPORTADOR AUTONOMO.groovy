Funcoes.somenteTransportadoresAutonomos()
double baseIRRF
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    baseIRRF = vaux
} else {
    if (autonomo.totalServicosAutonomo <= 0) {
        suspender "Não há valor de serviços lançados para o autônomo na competência"
    }
    baseIRRF = autonomo.totalServicosAutonomo - Eventos.valor(186)
}
if (baseIRRF > 0) {
    valorCalculado = baseIRRF
    Bases.compor(valorCalculado,
            Bases.INSS,
            Bases.SALBASE,
            Bases.IRRF,
            Bases.COMPHORAMES)
}

