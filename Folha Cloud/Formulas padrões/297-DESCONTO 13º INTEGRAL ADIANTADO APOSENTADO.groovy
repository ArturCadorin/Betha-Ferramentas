if (!(TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.INTEGRAL.equals(calculo.subTipoProcessamento))) {
    suspender 'Este evento deve ser calculado apenas no processamento de décimo terceiro (integral)'
}
if (!TipoMatricula.APOSENTADO.equals(matricula.tipo)) {
    suspender 'Este cálculo é executado apenas para aposentados'
}
if (!Funcoes.permitecalc13integral()) {
    suspender 'O aposentado não tem direito a receber décimo terceiro ou o seu período aquisitivo contém uma situação não permitida para o cálculo do evento'
}
if (!aposentado.dataCessacaoAposentadoria && !Datas.mes(calculo.competencia).equals(12)) {
    suspender 'Este evento é calculado apenas na competência de Dezembro'
}
double valor13IntegralAdiantado
if (folha.complementoDecimoTerceiro) {
    valor13IntegralAdiantado = folhaDecimoTerceiroIntegralAntecipado.totalLiquido + folhaDecimoTerceiroIntegralAntecipado.totalDesconto
} else {
    def inicioAnoBase = Datas.data(calculo.competencia.ano, 1, 1)
    valor13IntegralAdiantado = Funcoes.acumulaClassificacao(ClassificacaoEvento.LIQ13SALINTEGADI, TipoValor.CALCULADO, inicioAnoBase, calculo.competencia, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL) + Funcoes.acumulaClassificacao(ClassificacaoEvento.TOTDESC13SALINTEGADI, TipoValor.CALCULADO, inicioAnoBase, calculo.competencia, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL)
}
if (valor13IntegralAdiantado > 0) {
    valorCalculado = valor13IntegralAdiantado
} else {
    suspender 'Não há lançamento de 13º salário integral adiantado neste ano-base. Este evento é calculado apenas quando há um 13º salário integral pago em competência anterior a Dezembro ou anterior a cessação'
}

