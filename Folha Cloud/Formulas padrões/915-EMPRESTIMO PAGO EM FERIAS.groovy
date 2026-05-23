if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && calculo.recalcularMensal)) {
    suspender "O evento deve ser calculado apenas em processamentos mensais ou rescisórios"
}
if (calculo.competencia > Datas.data(2026, 01, 1)) {
    suspender "A partir da competência 01/2026, os empréstimos do eConsignado em férias devem ser calculados em um novo formato. Atualize as fórmulas dos eventos de empréstimo do eConsignado conforme o novo padrão disponibilizado pela Betha Sistemas"
}
BigDecimal valorEmprestimo = 0
def competenciaParcela
int maiorDiasFerias
folhasPeriodo.buscaFolhasProcessamento(TipoProcessamento.FERIAS).findAll{ it.inicioGozoFeriasCalculadas }.sort{ it.competencia }.each { f ->
    def dataInicial
    def dataFinal
    def dataInicialCompetencia = Datas.data(Datas.ano(f.competencia), Datas.mes(f.competencia), 1)
    def dataFinalCompetencia = Datas.removeDias(Datas.adicionaMeses(dataInicialCompetencia, 1), 1)
    def dataInicialGozoFerias = Funcoes.paraData(f.inicioGozoFeriasCalculadas)
    def dataFinalGozoFerias = Funcoes.paraData(f.fimGozoFeriasCalculadas)
    if (dataInicialGozoFerias < dataInicialCompetencia) {
        dataInicial = dataInicialCompetencia
    } else {
        dataInicial = dataInicialGozoFerias
    }
    if (dataFinalGozoFerias > dataFinalCompetencia) {
        dataFinal = dataFinalCompetencia
    } else {
        dataFinal = dataFinalGozoFerias
    }
    int diasFerias = Datas.diferencaDias(dataInicial, dataFinal) + 1
    if (diasFerias >= 15 && diasFerias > maiorDiasFerias) {
        competenciaParcela = dataInicialCompetencia
        maiorDiasFerias = diasFerias
    }
    if (f.folhaPagamento) {
        f.eventos.findAll{ it.classificacao.equals(ClassificacaoEvento.ECONSIGNADO) }.each { e ->
            valorEmprestimo += e.valor
        }
    }
}
if (valorEmprestimo > 0 && competenciaParcela && (Datas.data(calculo.competencia.ano, calculo.competencia.mes, 1) == Datas.data(Datas.ano(competenciaParcela), Datas.mes(competenciaParcela), 1))) {
    valorCalculado = valorEmprestimo
    evento.replicado(true)
}

