if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para aposentados e pensionistas"
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender "A matrícula não tem direito a receber décimo terceiro"
}
if (!Funcoes.permitecalc13integral()) {
    suspender "O período aquisitivo de décimo terceiro já foi quitado para a matrícula"
}
def retencao = Lancamentos.valor(evento)
if (retencao <= 0) {
    boolean mesDemissao
    def dataRescisao = calculo.dataRescisao
    if (dataRescisao != null) {
        if (Datas.ano(dataRescisao) == Datas.ano(calculo.competencia) && Datas.mes(dataRescisao) == Datas.mes(calculo.competencia) && calculo.quantidadeDiasCompetencia >= Datas.dia(dataRescisao)) {
            mesDemissao = true
        }
    }
    if (mesDemissao) {
        retencao = BasesOutrasEmpresas.buscaPor(TipoProcessamento.DECIMO_TERCEIRO_SALARIO).sum(0, { it.valorRetidoIrrf })
    } else {
        retencao = BasesOutrasEmpresas.buscaPor(calculo.tipoProcessamento).sum(0, { it.valorRetidoIrrf })
    }
}
if (retencao <= 0) {
    suspender "Não há retenção de IRRF de outras empresas 13º salário informada para a matrícula na competência para cálculo"
}
valorCalculado = retencao - Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

