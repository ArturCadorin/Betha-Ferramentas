if (!Funcoes.permitecalc13integral()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro ou o seu período aquisitivo contém uma situação não permitida para o cálculo do evento'
}
if (calculo.rra || TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos vinculados a pagamentos anteriores'
}
if (SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
    suspender "O evento não é calculado no subtipo de processamento 'adiantamento'"
}
if (servidor.possuiMolestiaGrave) {
    suspender 'O evento não é calculado para pessoas que possuam moléstia grave'
}
if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    double baseParcelaIsenta13 = Bases.valor(Bases.PAISIR13SA)
    double valorParcelaIsenta13 = Eventos.valor(136) + Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.PARCISENIRRF13SAL, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
    if (valorParcelaIsenta13 > 0 && valorParcelaIsenta13 >= baseParcelaIsenta13) {
        suspender 'Não há valor de dedução de IRRF a ser lançado para a matrícula pois o mesmo já foi coberto pela parcela isenta. A dedução legal será apurada apenas se rendimentos tributáveis ultrapassarem o valor da parcela isenta'
    }
}
double deducaoIrrf13IntegralAntecipado
double deducaoSimplificada13IntegralAntecipado
double deducaoCompleta13IntegralAntecipado
double desconto13SalarioDevidoReinteg
double deducaoSimplificada13AnteriorMultiplosVinculos
double deducaoCompleta13AnteriorMultiplosVinculos
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    boolean possuiMultiplosVinculos = matricula.possuiMultiploVinculo
    if (!EncargosSociais.IRRF.deducaoSimplificadaIrrf) {
        suspender "Para apuração da dedução de 13º salário a ser aplicada para a matrícula, a 'Dedução simplificada do IRRF' deve ser informada na manutenção de estabelecimento vigente"
    }
    if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
        if (folha.complementoDecimoTerceiro) {
            folhaDecimoTerceiroIntegralAntecipado.eventos.each { dadosEventos ->
                if (ClassificacaoEvento.DEDUCSIMPLIRRF13 == dadosEventos.classificacao && dadosEventos.valor > 0) {
                    deducaoSimplificada13IntegralAntecipado += dadosEventos.valor
                }
                if (ClassificacaoEvento.DEDUCCOMPLIRRF13 == dadosEventos.classificacao && dadosEventos.valor > 0) {
                    deducaoCompleta13IntegralAntecipado += dadosEventos.valor
                }
                if (ClassificacaoEvento.DESC13SALDEVREINTG == dadosEventos.classificacao && dadosEventos.valor > 0) {
                    desconto13SalarioDevidoReinteg += dadosEventos.valor
                }
            }
        } else {
            if ((TipoMatricula.APOSENTADO.equals(matricula.tipo) && aposentado.dataCessacaoAposentadoria) || (TipoMatricula.PENSIONISTA.equals(matricula.tipo) && pensionista.dataCessacaoBeneficio)) {
                if ((TipoMatricula.APOSENTADO.equals(matricula.tipo) && Datas.ano(aposentado.dataCessacaoAposentadoria).equals(Datas.ano(calculo.competencia)) && Datas.mes(aposentado.dataCessacaoAposentadoria).equals(Datas.mes(calculo.competencia))) ||
                        (TipoMatricula.PENSIONISTA.equals(matricula.tipo) && Datas.ano(pensionista.dataCessacaoBeneficio).equals(Datas.ano(calculo.competencia)) && Datas.mes(pensionista.dataCessacaoBeneficio).equals(Datas.mes(calculo.competencia)))) {
                    def inicioAnoBase = Datas.data(calculo.competencia.ano, 1, 1)
                    deducaoSimplificada13IntegralAntecipado += Funcoes.acumulaClassificacao(ClassificacaoEvento.DEDUCSIMPLIRRF13, TipoValor.CALCULADO, inicioAnoBase, calculo.competencia, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL)
                    deducaoCompleta13IntegralAntecipado += Funcoes.acumulaClassificacao(ClassificacaoEvento.DEDUCCOMPLIRRF13, TipoValor.CALCULADO, inicioAnoBase, calculo.competencia, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL)
                    desconto13SalarioDevidoReinteg += Funcoes.acumulaClassificacao(ClassificacaoEvento.DESC13SALDEVREINTG, TipoValor.CALCULADO, inicioAnoBase, calculo.competencia, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL)
                }
            }
        }
    }
    deducaoIrrf13IntegralAntecipado = deducaoSimplificada13IntegralAntecipado + deducaoCompleta13IntegralAntecipado
    double baseDeducaoIrrf13 = Bases.valor(Bases.DEDUCIRRF13) + desconto13SalarioDevidoReinteg
    if (possuiMultiplosVinculos) {
        baseDeducaoIrrf13 += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRF13, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        deducaoSimplificada13AnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRF13, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        deducaoCompleta13AnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRF13, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
            baseDeducaoIrrf13 += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRF13, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
            deducaoSimplificada13AnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRF13, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
            deducaoCompleta13AnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRF13, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
        }
    }
    if (EncargosSociais.IRRF.deducaoSimplificadaIrrf > baseDeducaoIrrf13 && (!deducaoCompleta13IntegralAntecipado || !deducaoCompleta13AnteriorMultiplosVinculos)) {
        suspender 'O valor da dedução dos descontos legais é menor do que o valor da dedução simplificada para o IRRF 13º salário. Por este motivo, deve ser aplicada a dedução simplificada para o IRRF 13º salário'
    }
    if (baseDeducaoIrrf13 >= EncargosSociais.IRRF.deducaoSimplificadaIrrf) {
        baseDeducaoIrrf13 = baseDeducaoIrrf13 - deducaoSimplificada13AnteriorMultiplosVinculos - deducaoCompleta13AnteriorMultiplosVinculos
    }
    valorCalculado = baseDeducaoIrrf13
}
Bases.compor(valorCalculado, Bases.IRRF13)
if (deducaoIrrf13IntegralAntecipado > 0) {
    valorCalculado -= deducaoIrrf13IntegralAntecipado
}

