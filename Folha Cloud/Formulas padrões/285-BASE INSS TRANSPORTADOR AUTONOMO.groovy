Funcoes.somenteTransportadoresAutonomos()
double baseINSS
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    baseINSS = vaux
} else {
    if (autonomo.totalServicosAutonomo <= 0) {
        suspender "Não há valor de serviços lançados para o autônomo na competência"
    }
    baseINSS = autonomo.totalServicosAutonomo - Eventos.valor(181)
}
if (baseINSS > 0) {
    valorCalculado = baseINSS
    Bases.compor(valorCalculado,
            Bases.INSS,
            Bases.SALBASE,
            Bases.IRRF,
            Bases.COMPHORAMES)
}

