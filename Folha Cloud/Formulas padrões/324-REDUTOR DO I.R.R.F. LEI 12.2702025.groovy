if (Datas.ano(calculo.dataPagamento) <= 2025) {
    suspender "O redutor do IRRF aprovado na lei 12.270/2025 só é válido para folhas com pagamento a partir de 2026"
}

if (TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    if (autonomo.codESocial.equals("741")) {
        suspender "Não há desconto de imposto de renda para autônomos da categoria MEI com o 'Código eSocial' igual a '741' informado na categoria do trabalhador"
    }
}

if (calculo.rra || TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos vinculados a pagamentos anteriores'
}

if (folha.folhaPagamento) {
    def forcarDependencia = Bases.valor(Bases.IRRF) + Bases.valor(Bases.IRRFFER)
    def rendimentoTributavel = Lancamentos.valor(evento)
    if (rendimentoTributavel >= 0) {
        valorCalculado = rendimentoTributavel
    } else {
        BigDecimal rendTribOutrosVinculos = 0
        if (matricula.possuiMultiploVinculo) {
            rendTribOutrosVinculos = Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF, calculo.tipoProcessamento,
                    calculo.subTipoProcessamento)
            rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRFFER, calculo.tipoProcessamento,
                    calculo.subTipoProcessamento)

            if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF, calculo.tipoProcessamento,
                        SubTipoProcessamento.INTEGRAL)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRFFER, calculo.tipoProcessamento,
                        SubTipoProcessamento.INTEGRAL)
            }

            if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF, TipoProcessamento.RESCISAO,
                        SubTipoProcessamento.INTEGRAL)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF, TipoProcessamento.RESCISAO,
                        SubTipoProcessamento.COMPLEMENTAR)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRFFER, TipoProcessamento.RESCISAO,
                        SubTipoProcessamento.INTEGRAL)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRFFER, TipoProcessamento.RESCISAO,
                        SubTipoProcessamento.COMPLEMENTAR)
            }

            if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF, TipoProcessamento.MENSAL,
                        SubTipoProcessamento.INTEGRAL)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRF, TipoProcessamento.MENSAL,
                        SubTipoProcessamento.COMPLEMENTAR)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRFFER, TipoProcessamento.MENSAL,
                        SubTipoProcessamento.INTEGRAL)
                rendTribOutrosVinculos += Irrf.rendimentoTributavelMultiplosVinculos(Bases.IRRFFER, TipoProcessamento.MENSAL,
                        SubTipoProcessamento.COMPLEMENTAR)
            }
        }
        rendimentoTributavel = Irrf.rendimentoTributavel(Bases.IRRF) + Irrf.rendimentoTributavel(Bases.IRRFFER) + rendTribOutrosVinculos

        if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            rendimentoTributavel += Irrf.rendimentoTributavel(Bases.IRRF, calculo.tipoProcessamento, calculo.subTipoProcessamento) +
                    Irrf.rendimentoTributavel(Bases.IRRFFER, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        }

        valorCalculado = Irrf.calcularRedutorIrrf(rendimentoTributavel)
    }
    valorReferencia = rendimentoTributavel
    Bases.compor(valorCalculado, Bases.REDUTIRRF)
}
