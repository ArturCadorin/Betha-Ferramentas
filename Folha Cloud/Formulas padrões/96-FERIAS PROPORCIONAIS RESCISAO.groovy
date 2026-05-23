Funcoes.somenteFuncionarios()
def feriasprop = PeriodosAquisitivos.buscaProporcional()
if (!feriasprop) {
    suspender 'Não há dias de férias proporcionais a serem pagas para o funcionário'
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
        valorCalculado = funcionario.salario * diasferias / 30
    }
}
if (valorCalculado > 0) {
    PeriodosAquisitivos.quitaPorRescisao(feriasprop, valorCalculado)
}

