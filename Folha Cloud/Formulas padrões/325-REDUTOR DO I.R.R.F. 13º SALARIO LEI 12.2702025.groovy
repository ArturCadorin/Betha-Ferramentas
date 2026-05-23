if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro'
}

if (Datas.ano(calculo.dataPagamento) <= 2025) {
    suspender "O redutor do IRRF aprovado na lei 12.270/2025 só é válido para folhas com pagamento a partir de 2026"
}

if (SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
    suspender"O evento não é calculado no subtipo de processamento 'adiantamento'"
}

if (calculo.rra || TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos vinculados a pagamentos anteriores'
}

if (folha.folhaPagamento) {
    def forcarDependencia = Bases.valor(Bases.IRRF13)
    def rendimentoTributavel = Lancamentos.valor(evento)
    if (rendimentoTributavel >= 0) {
        valorCalculado = rendimentoTributavel
    } else {
        BigDecimal rendTribOutrosVinculos = 0
        if (matricula.possuiMultiploVinculo) {
            rendTribOutrosVinculos = Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF13, calculo.tipoProcessamento,
                    calculo.subTipoProcessamento)

            if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF13, calculo.tipoProcessamento,
                        SubTipoProcessamento.INTEGRAL)
            }

            if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF13, TipoProcessamento.RESCISAO,
                        SubTipoProcessamento.INTEGRAL)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF13, TipoProcessamento.RESCISAO,
                        SubTipoProcessamento.COMPLEMENTAR)
            }

            if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF,
                        TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL)
            }
        }
        rendimentoTributavel = Irrf.rendimentoTributavel(Bases.IRRF13) + rendTribOutrosVinculos
        valorCalculado = Irrf.calcularRedutorIrrf(rendimentoTributavel)
    }
    valorReferencia = rendimentoTributavel
    Bases.compor(valorCalculado, Bases.REDUTIRRF13)
}
