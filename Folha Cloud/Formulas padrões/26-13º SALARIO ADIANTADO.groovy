def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (valorFerias.valor > 0) {
    Bases.compor(valorFerias.valor, Bases.FGTS13)
}
if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) || TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (folha.folhaPagamento) {
        if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
            suspender "Este cálculo não é executado para aposentados e pensionistas"
        }
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
        double valBasePropCalc
        double valBaseFgts
        double vlrreffgts = Funcoes.avos13(12, true)
        def vvar = Lancamentos.valor(evento)
        if (vvar >= 0) {
            valorCalculado = vvar
            valBasePropCalc = vvar
            valBaseFgts = vvar * vlrreffgts / valorReferencia
        } else {
            if (!evento.taxa || evento.taxa == 0) {
                suspender "Para o cálculo do décimo terceiro adiantado deve ser informada uma taxa e ela deve ser superior a zero"
            }
            double salario = Funcoes.remuneracao(matricula.tipo).valor
            valBasePropCalc = (salario * valorReferencia / 12) * evento.taxa / 100
            valorCalculado = valBasePropCalc
            valBaseFgts = (salario * vlrreffgts / 12) * evento.taxa / 100
        }
        double valEsocialFgts = valBaseFgts - valBasePropCalc
        Bases.compor(valEsocialFgts, Bases.FGTS13AFASES)
        Bases.compor(valBasePropCalc, Bases.FGTS13)
    }
}

