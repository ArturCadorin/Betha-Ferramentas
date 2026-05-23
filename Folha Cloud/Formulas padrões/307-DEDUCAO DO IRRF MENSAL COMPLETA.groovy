if (TipoMatricula.AUTONOMO.equals(matricula.tipo) && autonomo.codESocial.equals("741")) {
    suspender "Não há deduções de IRRF para autônomos da categoria MEI com o 'Código eSocial' igual a '741' informado na categoria do trabalhador"
}
if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento não é calculado em processamentos de décimo terceiro"
}
if (!calculo.rra && TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos vinculados a pagamentos anteriores sem RRA'
}
if (servidor.possuiMolestiaGrave) {
    suspender 'O evento não é calculado para pessoas que possuam moléstia grave'
}
if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    double baseParcelaIsenta = Bases.valor(Bases.PARCISENIRRF)
    double valorParcelaIsenta = Eventos.valor(135) + Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.PARCISENIRRF, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
    if (valorParcelaIsenta > 0 && valorParcelaIsenta >= baseParcelaIsenta) {
        suspender 'Não há valor de dedução de IRRF a ser lançado para a matrícula pois o mesmo já foi coberto pela parcela isenta. A dedução legal será apurada apenas se rendimentos tributáveis ultrapassarem o valor da parcela isenta'
    }
}
if (folha.folhaPagamento) {
    def vvar = Lancamentos.valor(evento)
    if (vvar >= 0) {
        valorCalculado = vvar
    } else {
        double deducaoSimplificadaAnterior
        double deducaoCompletaAnterior
        double deducaoSimplificadaAnteriorMultiplosVinculos
        double deducaoCompletaAnteriorMultiplosVinculos
        boolean possuiMultiplosVinculos = matricula.possuiMultiploVinculo
        if (!EncargosSociais.IRRF.deducaoSimplificadaIrrf) {
            suspender "Para apuração da dedução a ser aplicada para a matrícula, a 'Dedução simplificada do IRRF' deve ser informada na manutenção de estabelecimento vigente"
        }
        double baseDeducaoIrrf = Bases.valor(Bases.DEDUCIRRFMES)
        if (!SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && baseDeducaoIrrf == 0) {
            suspender "Não há base de apuração de dedução nesta folha"
        }
        if (possuiMultiplosVinculos) {
            baseDeducaoIrrf += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRFMES, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            deducaoSimplificadaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            deducaoCompletaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && !calculo.rra) {
                baseDeducaoIrrf += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRFMES, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
                deducaoSimplificadaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
                deducaoCompletaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
            }
        }
        if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            deducaoSimplificadaAnterior += Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, calculo.competencia)
            deducaoCompletaAnterior += Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, calculo.competencia)
        }
        if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
            deducaoSimplificadaAnterior += Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia)
            deducaoSimplificadaAnterior += Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
            deducaoCompletaAnterior += Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia)
            deducaoCompletaAnterior += Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
            if (possuiMultiplosVinculos) {
                deducaoSimplificadaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                deducaoSimplificadaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                deducaoCompletaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                deducaoCompletaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
            }
        }
        if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && possuiMultiplosVinculos) {
            deducaoSimplificadaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
            deducaoSimplificadaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
            deducaoCompletaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
            deducaoCompletaAnteriorMultiplosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
        }
        if (deducaoSimplificadaAnterior > 0 || deducaoSimplificadaAnteriorMultiplosVinculos > 0) {
            if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
                baseDeducaoIrrf += Bases.valorCalculado(Bases.DEDUCIRRFMES, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            }
            if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                baseDeducaoIrrf += Bases.valorCalculado(Bases.DEDUCIRRFMES, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                baseDeducaoIrrf += Bases.valorCalculado(Bases.DEDUCIRRFMES, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                if (possuiMultiplosVinculos) {
                    baseDeducaoIrrf += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRFMES, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    baseDeducaoIrrf += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRFMES, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                }
            }
            if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && possuiMultiplosVinculos) {
                baseDeducaoIrrf += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRFMES, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                baseDeducaoIrrf += Bases.valorCalculadoMultiplosVinculos(Bases.DEDUCIRRFMES, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
            }
        }
        if (EncargosSociais.IRRF.deducaoSimplificadaIrrf > baseDeducaoIrrf && !calculo.rra && (!deducaoCompletaAnterior || !deducaoCompletaAnteriorMultiplosVinculos)) {
            suspender 'O valor da dedução dos descontos legais é menor do que o valor da dedução simplificada do IRRF. Por este motivo, deve ser aplicada a dedução simplificada'
        }
        baseDeducaoIrrf -= deducaoSimplificadaAnterior
        if (baseDeducaoIrrf >= EncargosSociais.IRRF.deducaoSimplificadaIrrf) {
            baseDeducaoIrrf = baseDeducaoIrrf - deducaoSimplificadaAnteriorMultiplosVinculos - deducaoCompletaAnteriorMultiplosVinculos
        }
        //Na complementar de RRA não é calculada a dedução completa e com isso é necessário zerar as diferenças vindas de processamentos anteriores, realizando assim a soma dos eventos com a classificação 'DEDUCCOMPLIRRFMENSAL'
        if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && calculo.rra) {
            valorCalculado = Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                    Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
        } else if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && calculo.rra) {
            valorCalculado = Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                    Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
        } else if (TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento) && calculo.rra) {
            valorCalculado = Eventos.valorRra(ClassificacaoEvento.INSS) + Eventos.valorRra(ClassificacaoEvento.PREVEST) + Eventos.valorRra(ClassificacaoEvento.FUNDPREV) +
                    Eventos.valorRra(ClassificacaoEvento.FUNDFIN) + Eventos.valorRra(ClassificacaoEvento.FUNDASS) + Eventos.valorRra(ClassificacaoEvento.INSS13SAL) +
                    Eventos.valorRra(ClassificacaoEvento.PREVESTDECSAL) + Eventos.valorRra(ClassificacaoEvento.FUNDPREV13SAL) + Eventos.valorRra(ClassificacaoEvento.FUNDFIN13SAL) +
                    Eventos.valorRra(ClassificacaoEvento.FUNDASS13SAL)
        } else {
            valorCalculado = baseDeducaoIrrf
        }
    }
    if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
        Bases.compor(valorCalculado, Bases.IRRFFER)
    }
    if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) || TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
        Bases.compor(valorCalculado, Bases.IRRF)
    }
    if (TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento) && calculo.rra) {
        Bases.compor(valorCalculado, Bases.IRRFRRA)
    }
}

