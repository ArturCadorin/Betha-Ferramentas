Funcoes.somenteFuncionarios()
def feriasprop = PeriodosAquisitivos.buscaProporcional()
if (!feriasprop) {
    suspender 'Não há dias de férias proporcionais a serem pagas para o funcionário'
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    if (feriasprop.pagouUmTercoIntegral) {
        suspender 'O valor de 1/3 das férias já foi pago integralmente em um cálculo proporcional de férias anterior deste período aquisitivo'
    }
    valorCalculado = 0
    def diasferias = feriasprop.diasFeriasPagosRescisao
    if (diasferias > 0) {
        vaux = Eventos.valor(96) + Eventos.valor(102) + Eventos.valor(103) + Eventos.valor(104) + Eventos.valor(112) + Eventos.valor(235)
        valorReferencia = evento.taxa
        valorCalculado = vaux * evento.taxa / 100
    }
}
if (valorCalculado > 0) {
    PeriodosAquisitivos.quitaPorRescisao(feriasprop, evento.taxa, valorCalculado)
}

