if (!TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    if (Bases.valor(Bases.PAGAPROP) == 0 && Bases.valor(Bases.SALBASE) == 0) {
        suspender "Este evento deve ser calculado para matrículas com valor de base 'Paga proporcional' ou 'Salário base' na competência"
    }
}
if (TipoMatricula.AUTONOMO.equals(matricula.tipo) && autonomo.codESocial.equals("741")) {
    suspender "Não há deduções de IRRF para autônomos da categoria MEI com o 'Código eSocial' igual a '741' informado na categoria do trabalhador"
}
if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento) || TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender "Este evento não é calculado nos processamentos de décimo terceiro e em pagamentos anteriores"
}
if (servidor.possuiMolestiaGrave) {
    suspender 'O evento não é calculado para pessoas que possuam moléstia grave'
}
if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    double baseParcelaIsenta = Bases.valor(Bases.PARCISENIRRF)
    double valorParcelaIsenta = Eventos.valor(135) + Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.PARCISENIRRF, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
    if (valorParcelaIsenta > 0 && valorParcelaIsenta >= baseParcelaIsenta) {
        suspender 'Não há valor de dedução de IRRF a ser lançado para a matrícula pois o mesmo já foi coberto pela parcela isenta. A dedução simplificada será apurada apenas se rendimentos tributáveis ultrapassarem o valor da parcela isenta'
    }
}
if (folha.folhaPagamento) {
    def vvar = Lancamentos.valor(evento)
    if (vvar >= 0) {
        valorCalculado = vvar
    } else {
        boolean possuiMultiplosVinculos = matricula.possuiMultiploVinculo
        if (!EncargosSociais.IRRF.deducaoSimplificadaIrrf) {
            suspender "Para apuração da dedução a ser aplicada para a matrícula, a 'Dedução simplificada do IRRF' deve ser informada na manutenção de estabelecimento vigente"
        }
        if (possuiMultiplosVinculos) {
            if (Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento) > 0
                    || Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento) > 0) {
                suspender 'Já há dedução de IRRF aplicada na competência para este servidor neste processamento'
            }
            if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && !calculo.rra) {
                if (Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL) > 0
                        || Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL) > 0) {
                    suspender 'Já há dedução de IRRF aplicada na competência para este servidor em subtipo de processamento integral anterior'
                }
            }
        }
        if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            if (Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, calculo.competencia) > 0) {
                suspender 'Já há dedução simplificada aplicada em folha de férias anterior na competência para esta matrícula'
            }
            if (Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, calculo.competencia) > 0) {
                suspender 'Já há dedução legal aplicada em folha de férias anterior na competência para esta matrícula'
            }
        }
        if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
            if (Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia) > 0
                    || Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia) > 0) {
                suspender 'Já há dedução simplificada calculada em folha mensal anterior na competência para esta matrícula'
            }
            if (Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia) > 0
                    || Eventos.valorCalculado(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia) > 0) {
                suspender 'Já há dedução legal calculada em folha mensal anterior na competência para esta matrícula'
            }
            if (possuiMultiplosVinculos) {
                if (Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) > 0
                        || Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR) > 0) {
                    suspender 'Já há dedução simplificada aplicada em folha mensal anterior na competência para este servidor'
                }
                if (Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) > 0
                        || Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR) > 0) {
                    suspender 'Já há dedução legal aplicada em folha mensal anterior na competência para este servidor'
                }
            }
        }
        if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && possuiMultiplosVinculos) {
            if (Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL) > 0
                    || Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR) > 0) {
                suspender 'Já há dedução simplificada calculada em folha rescisória anterior na competência para este servidor'
            }
            if (Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL) > 0
                    || Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEDUCCOMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR) > 0) {
                suspender 'Já há dedução legal calculada em folha rescisória anterior na competência para este servidor'
            }
        }
        //Na complementar de RRA não é calculado o desconto simplicado e com isso é necessário zerar as diferenças vindas de processamentos anteriores, realizando assim a soma dos eventos com a classificação 'DEDUCSIMPLIRRFMENSAL'
        if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && calculo.rra) {
            valorCalculado = Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                    Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
        } else if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento) && calculo.rra) {
            valorCalculado = Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                    Eventos.valorCalculado(ClassificacaoEvento.DEDUCSIMPLIRRFMENSAL, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
        } else {
            double baseDeducaoIrrf = Bases.valor(Bases.DEDUCIRRFMES)
            if (EncargosSociais.IRRF.deducaoSimplificadaIrrf <= baseDeducaoIrrf) {
                suspender 'O valor da dedução simplificada do IRRF é menor ou igual ao valor das deduções legais. Por este motivo, deve ser aplicada a dedução legal'
            }
            valorCalculado = EncargosSociais.IRRF.deducaoSimplificadaIrrf
        }
    }
    if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
        Bases.compor(valorCalculado, Bases.IRRFFER)
    }
    if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) || TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
        Bases.compor(valorCalculado, Bases.IRRF)
    }
}

