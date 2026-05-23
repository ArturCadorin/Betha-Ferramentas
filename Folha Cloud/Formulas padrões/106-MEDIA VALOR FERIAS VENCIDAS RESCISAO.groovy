Funcoes.somenteFuncionarios()
def vencidos = PeriodosAquisitivos.buscaVencidos()
if (vencidos.size() <= 0) {
    suspender 'Não há períodos de férias vencidas a serem pagas para o funcionário'
}
double quant
double vlrPeriodo
def vvar = Lancamentos.valor(evento)
valorCalculado = 0
vencidos.each { feriasvenc ->
    quant++
    if (vvar >= 0) {
        vlrPeriodo = vvar
        valorCalculado += vlrPeriodo
    } else {
        def diasferias = feriasvenc.diasFeriasPagosRescisao
        if (diasferias > 0) {
            def valorMedia = mediaVantagem.calcular(feriasvenc)
            vlrPeriodo = valorMedia
            valorCalculado += vlrPeriodo
        }
    }
    if (vlrPeriodo > 0) {
        PeriodosAquisitivos.quitaPorRescisao(feriasvenc, 0, vlrPeriodo, true)
    }
}
valorReferencia = quant

