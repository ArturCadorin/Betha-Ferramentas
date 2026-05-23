Funcoes.somenteFuncionarios()
vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    double vlrPeriodo
    double vlrAux
    def vencidos = PeriodosAquisitivos.buscaVencidos()
    if (vencidos.size() <= 0) {
        suspender 'Não há períodos de férias vencidas a serem pagas para o funcionário'
    }
    if (vencidos.size() < 2 || Eventos.valor(98) <= 0) {
        suspender "Há um período de férias que está vencido. Para o pagamento do 1/3 de férias indenizadas, devem haver dois ou mais períodos de férias vencidas a serem pagas para o funcionário e o evento de 'Férias em Dobro Rescisão' também deve ser calculado"
    }
    valorCalculado = 0
    vencidos.each { feriasvenc ->
        vlrPeriodo = funcionario.salario * evento.taxa / 100
        vlrPeriodo = Numeros.arredonda(vlrPeriodo, 2)
        PeriodosAquisitivos.quitaPorRescisao(feriasvenc, vlrPeriodo)
        vlrAux += vlrPeriodo
    }
    valorCalculado += vlrAux
    valorReferencia = evento.taxa
}

