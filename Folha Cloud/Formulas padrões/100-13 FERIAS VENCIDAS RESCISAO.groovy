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
        if (diasferias > 0 && !feriasvenc.pagouUmTercoIntegral) {
            vaux = Eventos.valor(97) + Eventos.valor(105) + Eventos.valor(106) + Eventos.valor(107) + Eventos.valor(236)
            vlrPeriodo = feriasvenc.valorCalculadoPago * evento.taxa / 100
            vlrPeriodo = Numeros.arredonda(vlrPeriodo, 2)
            valorCalculado += vlrPeriodo
        }
    }
    if (vlrPeriodo > 0) {
        PeriodosAquisitivos.quitaPorRescisao(feriasvenc, evento.taxa, vlrPeriodo)
    }
}
valorReferencia = quant

