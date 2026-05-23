if (!(TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.INTEGRAL.equals(calculo.subTipoProcessamento))) {
    suspender 'Este evento deve ser calculado apenas no processamento de décimo terceiro (integral)'
}
if (!TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender 'Este cálculo é executado apenas para pensionistas'
}
if (!Funcoes.permitecalc13integral()) {
    suspender 'O pensionista não tem direito a receber décimo terceiro ou o seu período aquisitivo contém uma situação não permitida para o cálculo do evento'
}
if (Datas.mes(calculo.competencia).equals(12)) {
    suspender 'Este evento é calculado apenas em competências anteriores à Dezembro'
}
if (pensionista.dataCessacaoBeneficio) {
    if (Datas.ano(pensionista.dataCessacaoBeneficio).equals(Datas.ano(calculo.competencia)) && Datas.mes(pensionista.dataCessacaoBeneficio).equals(Datas.mes(calculo.competencia))) {
        suspender 'Este evento não é calculado na competência de cessação do pensionista'
    }
}
valorCalculado = folha.totalDesconto

