Funcoes.somenteFuncionarios()
if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento não é calculado no processamento de rescisão. Para este processamento deve haver um evento específico por conta das rubricas do eSocial"
}
if (!(TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.INTEGRAL.equals(calculo.subTipoProcessamento))) {
    suspender "Este evento deve ser calculado apenas no processamento de décimo terceiro (integral)"
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender "O funcionário não tem direito a receber décimo terceiro"
}
if (!Funcoes.permitecalc13integral()) {
    suspender "O período aquisitivo de décimo terceiro já foi quitado para o funcionário"
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
        suspender "No ano exercício de '${ano}' calculado, não há período aquisitivo de décimo terceiro com o status 'Quitado parcialmente' para realizar o desconto de décimo terceiro devido reintegração"
    }
    def movimentacoesRescisao = null
    periodo.movimentacoesByMotivo(MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.RESCISAO, MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.RESCISAO_COMPLEMENTAR)
            .each{ mov -> movimentacoesRescisao = mov }
    if (movimentacoesRescisao == null) {
        suspender "Não há rescisões reintegradas no período aquisitivo de décimo terceiro do ano exercício de '${ano}'"
    }
    double valorReintegrado = periodo.totalMovimentacoesByMotivo(MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.RESCISAO) + periodo.totalMovimentacoesByMotivo(MotivoMovimentacaoPeriodoAquisitivoDecimoTerceiro.RESCISAO_COMPLEMENTAR)
    if (valorReintegrado <= 0) {
        suspender "Não há valores de décimo terceiro lançados no período aquisitivo do ano exercício de '${ano}' referentes a rescisão ou rescisão complementar"
    }
    valorCalculado = valorReintegrado
}
if (Eventos.valor(25) > 0 && !folha.calculoVirtual) {
    Bases.compor(valorCalculado,
            Bases.FGTS13,
            Bases.DEDUCIRRF13,
            Bases.INSS13,
            Bases.PREVEST13,
            Bases.FUNDASS13,
            Bases.FUNDPREV13,
            Bases.DESC13REINT,
            Bases.FUNDFIN13)
}
valorCalculado -= Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

