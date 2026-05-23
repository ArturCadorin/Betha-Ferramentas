if (Datas.data(calculo.competencia.ano, calculo.competencia.mes, 1) > Datas.data(2017, 11, 1)) {
    suspender 'Este evento é calculado apenas em competências anteriores a 12/2017'
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    def dataInicial = Funcoes.inicioCompetencia()
    def dataFinal
    def cptCalculo = Datas.data(calculo.competencia.ano, calculo.competencia.mes, 1)
    if (cptCalculo.equals(Datas.data(2017, 11, 1))) {
        dataFinal = Datas.data(2017, 11, 10)
    } else {
        dataFinal = Funcoes.fimCompetencia()
    }
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
    if (valorDiarias == 0) {
        suspender 'Não há valor de diárias a serem pagas na competência para o período informado'
    }
    double limite = Bases.valor(Bases.SALBASE) * 0.5
    if (valorDiarias <= limite) {
        suspender 'Para competências anteriores a 12/2017, o valor das diárias deve ser maior que o limite estabelecido'
    }
    valorReferencia = quantDiarias
    valorCalculado = valorDiarias
}
Bases.compor(valorCalculado,
                Bases.FGTS,
                Bases.INSS,
                Bases.PREVEST,
                Bases.FUNDOPREV,
                Bases.FUNDFIN,
                Bases.MEDIAUXMAT)

