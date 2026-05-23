//Caso a taxa do evento de média valor décimo terceiro adiantado não tenha sido informada, será utilizada a taxa padrão de 50% para cálculo da média
Funcoes.somenteFuncionarios()
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (valorFerias.valor > 0) {
    Bases.compor(valorFerias.valor, Bases.FGTS13)
}
if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) || TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (folha.folhaPagamento) {
        if (!Funcoes.recebeDecimoTerceiro()) {
            suspender "A matrícula não tem direito a receber décimo terceiro"
        }
        if (calculo.pagarDecimoTerceiroFerias && TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            if (periodoAquisitivoDecimoTerceiro.totalMovimentacoes > 0) {
                suspender "Não é possível realizar o pagamento do décimo terceiro salário adiantado em férias pois já existem movimentações lançadas no período aquisitivo de décimo terceiro"
            }
        } else {
            if (!periodoAquisitivoDecimoTerceiro.situacao.equals(SituacaoPeriodoAquisitivoDecimoTerceiro.EM_ANDAMENTO) && !periodoAquisitivoDecimoTerceiro.situacao.equals(SituacaoPeriodoAquisitivoDecimoTerceiro.ATRASADO)) {
                suspender "O período aquisitivo de décimo terceiro já foi quitado ou está com situação diferente de 'Em andamento' ou 'Atrasado'"
            }
        }
        valorReferencia = Funcoes.avos13(12)
        if (valorReferencia <= 0) {
            suspender "Não há avos adquiridos no período aquisitivo de décimo terceiro"
        }
        double vlrreffgts = Funcoes.avos13(12, true)
        double valBasePropCalc
        double valBaseFgts
        def vvar = Lancamentos.valor(evento)
        if (vvar >= 0) {
            valorCalculado = vvar
            valBasePropCalc = valorCalculado
        } else {
            valorCalculado = mediaVantagem.calcular(valorReferencia)
            valBasePropCalc = valorCalculado
        }
        valBaseFgts = valorCalculado * vlrreffgts / valorReferencia
        double valEsocialFgts = valBaseFgts - valBasePropCalc
        Bases.compor(valBasePropCalc, Bases.FGTS13)
        Bases.compor(valEsocialFgts, Bases.FGTS13AFASES)
    }
}

