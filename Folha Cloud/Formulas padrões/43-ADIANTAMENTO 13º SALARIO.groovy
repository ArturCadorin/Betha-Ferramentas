if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento não é calculado no processamento de rescisão. Para este processamento deve haver um evento específico por conta das rubricas do eSocial"
}
if (!(TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.INTEGRAL.equals(calculo.subTipoProcessamento))) {
    suspender "Este evento deve ser calculado apenas no processamento de décimo terceiro (integral)"
}
if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para aposentados e pensionistas"
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender "A matrícula não tem direito a receber décimo terceiro"
}
if (!Funcoes.permitecalc13integral()) {
    suspender "O período aquisitivo de décimo terceiro já foi quitado para a matrícula"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    def ano = Funcoes.anoExercicio13()
    def periodo = null
    PeriodosAquisitivosDecimoTerceiro
            .buscaPeriodosAquisitivosBySituacao(SituacaoPeriodoAquisitivoDecimoTerceiro.QUITADO_PARCIALMENTE)
                .each{ p -> if (p.anoExercicio == ano) {
                    periodo = p
                }
            }
    if (periodo == null) {
        suspender "No ano exercício de '${ano}' calculado, não há período aquisitivo de décimo terceiro com o status 'Quitado parcialmente' para realizar o desconto de décimo terceiro adiantado"
    }
    def movimentacoesAdiantamento = null
    periodo.movimentacoesByMotivo(MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.ADIANTAMENTO_DECIMO_TERCEIRO, MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.ADIANTAMENTO_FERIAS)
            .each{ mov -> movimentacoesAdiantamento = mov }
    if (movimentacoesAdiantamento == null) {
        suspender "Não há adiantamentos lançados no período aquisitivo de décimo terceiro do ano exercício de '${ano}'"
    }
    double valorAdiantado = periodo.totalMovimentacoesByMotivo(MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.ADIANTAMENTO_DECIMO_TERCEIRO) + periodo.totalMovimentacoesByMotivo(MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.ADIANTAMENTO_FERIAS)
    if (valorAdiantado <= 0) {
        suspender "Não há valores de adiantamentos de décimo terceiro lançados no período aquisitivo do ano exercício de '${ano}' para o cálculo"
    }
    valorCalculado = valorAdiantado
}
if (Eventos.valor(25) > 0 && !folha.calculoVirtual) {
    Bases.compor(valorCalculado, Bases.FGTS13)
}
valorCalculado -= Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

