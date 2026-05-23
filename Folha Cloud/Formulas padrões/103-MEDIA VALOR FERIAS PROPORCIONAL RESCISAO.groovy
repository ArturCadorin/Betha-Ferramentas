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
    valorCalculado = 0
    def diasferias = feriasprop.diasFeriasPagosRescisao
    if (diasferias > 0) {
        valorReferencia = diasferias
        def vint = mediaVantagem.calcular()
        if (vint <= 0) {
            suspender 'Não há média valores de férias proporcionais a serem pagas ao funcionário'
        }
        valorCalculado = vint
    }
}
if (valorCalculado > 0) {
    PeriodosAquisitivos.quitaPorRescisao(feriasprop, valorReferencia, valorCalculado, true)
}

