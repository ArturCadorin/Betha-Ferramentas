Funcoes.somenteFuncionarios()
categorias = [
        'AGENTE_PUBLICO',
        'AGENTE_POLITICO',
        'SERVIDOR_PUBLICO_COMISSAO',
        'SERVIDOR_PUBLICO_EFETIVO'
]
categoria = funcionario.categoriaSefipVinculo.toString()
if (categorias.contains(categoria)) {
    suspender "Este evento não é calculado para funcionários com categoria SEFIP igual ou superior a 12"
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    double quant
    double vlrProporcional
    def vencidos = PeriodosAquisitivos.buscaVencidos()
    if (vencidos.size() <= 0) {
        suspender 'Não há períodos de férias vencidas a serem pagas para o funcionário'
    }
    if (vencidos.size() < 2) {
        suspender 'Há um período de férias que está vencido. Para o pagamento de férias em dobro, devem haver dois ou mais períodos de férias vencidas a serem pagas para o funcionário'
    }
    valorCalculado = 0
    vencidos.each { feriasvenc ->
        quant++
        def diasferias = feriasvenc.diasFeriasPagosRescisao
        if (quant > 1) {
            vlrProporcional = funcionario.salario * diasferias / 30
            valorCalculado += vlrProporcional
            if (vlrProporcional > 0) {
                PeriodosAquisitivos.quitaPorRescisao(feriasvenc, vlrProporcional)
            }
        }
    }
    valorReferencia = quant - 1
}

