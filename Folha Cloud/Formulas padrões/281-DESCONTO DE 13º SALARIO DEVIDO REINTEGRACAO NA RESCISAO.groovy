Funcoes.somenteFuncionarios()
if (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento é calculado apenas em processamentos rescisórios"
}
if (!Funcoes.permitecalc13integral()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro ou o seu período aquisitivo contém uma situação não permitida para o cálculo do evento'
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
if (Eventos.valor(274) > 0 && !folha.calculoVirtual) {
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

