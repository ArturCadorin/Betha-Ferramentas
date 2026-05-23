if (!(TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.INTEGRAL.equals(calculo.subTipoProcessamento))) {
    suspender 'Este evento deve ser calculado apenas no processamento de décimo terceiro (integral)'
}
if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender 'Este cálculo não é executado para aposentados e pensionistas'
}
if (!Funcoes.permitecalc13integral()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro ou o seu período aquisitivo contém uma situação não permitida para o cálculo do evento'
}
if (Datas.mes(calculo.competencia).equals(12)) {
    suspender 'Este evento é calculado apenas em competências anteriores à Dezembro'
}
valorCalculado = folha.totalLiquido

