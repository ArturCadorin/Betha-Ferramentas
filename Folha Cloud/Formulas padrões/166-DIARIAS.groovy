def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    def dataInicial = Funcoes.inicioCompetencia()
    def dataFinal = Funcoes.fimCompetencia()
    int quantDiarias
    double valorDiarias
    def diarias = Diarias.buscaPorPeriodo(dataInicial, dataFinal)
    if (diarias.size() <= 0) {
        suspender 'Não há diárias lançadas para a matrícula no período informado'
    }
    diarias.each { diaria ->
        diaria.despesas.each { despesa ->
            if (despesa.quantidade != null) quantDiarias += despesa.quantidade
            if (despesa.valorTotal != null) valorDiarias += despesa.valorTotal
        }
    }
    def cptCalculo = Datas.data(calculo.competencia.ano, calculo.competencia.mes, 1)
    if (cptCalculo <= Datas.data(2017, 11, 1)) {
        double limite = Bases.valor(Bases.SALBASE) * 0.5
        if (cptCalculo.equals(Datas.data(2017, 11, 1))) {
            int quantDiarias11
            double valorDiarias11
            def diarias11 = Diarias.buscaPorPeriodo(dataInicial, Datas.data(2017, 11, 10))
            diarias11.each { diaria11 ->
                diaria11.despesas.each { despesa11 ->
                    if (despesa11.quantidade != null) quantDiarias11 += despesa11.quantidade
                    if (despesa11.valorTotal != null) valorDiarias11 += despesa11.valorTotal
                }
            }
            if (valorDiarias11 > limite) {
                quantDiarias -= quantDiarias11
                valorDiarias -= valorDiarias11
            }
        } else {
            if (valorDiarias > limite) {
                suspender 'Para competências anteriores a 11/2017, o valor das diárias não pode ser maior que o limite estabelecido'
            }
        }
    }
    if (valorDiarias <= 0) {
        suspender 'Não há valor de diárias a serem pagas na competência para o período informado'
    }
    valorReferencia = quantDiarias
    valorCalculado = valorDiarias
}

