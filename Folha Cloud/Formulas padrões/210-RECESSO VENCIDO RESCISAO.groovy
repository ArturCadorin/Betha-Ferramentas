if (!TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado somente para estagiários"
}
def vencidos = PeriodosAquisitivos.buscaVencidos()
if (vencidos.size() <= 0) {
    suspender "Não há períodos de recessos vencidos a serem pagos para o estagiário"
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
            vlrPeriodo = Funcoes.remuneracao(matricula.tipo).valor * diasferias / 30
            valorCalculado += vlrPeriodo
        }
    }
    if (vlrPeriodo > 0) {
        PeriodosAquisitivos.quitaPorRescisao(feriasvenc, vlrPeriodo)
    }
}
valorReferencia = quant

