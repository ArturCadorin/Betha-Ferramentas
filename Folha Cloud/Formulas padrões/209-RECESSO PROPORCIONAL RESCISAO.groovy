if (!TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender 'Este cálculo é executado somente para estagiários'
}
def feriasprop = PeriodosAquisitivos.buscaProporcional()
if (!feriasprop) {
    suspender 'Não há dias de recessos proporcionais a serem pagos para o estagiário'
}
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorReferencia = vvar
    valorCalculado = vvar
} else {
    valorCalculado = 0
    def diasferias = feriasprop.diasFeriasPagosRescisao
    if (diasferias > 0) {
        valorReferencia = diasferias
        valorCalculado = Funcoes.remuneracao(matricula.tipo).valor * diasferias / 30
    }
}
if (valorCalculado > 0) {
    PeriodosAquisitivos.quitaPorRescisao(feriasprop, valorCalculado)
}

