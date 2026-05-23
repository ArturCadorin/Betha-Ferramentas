if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para aposentados e pensionistas"
}
if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    if (!estagiario.codESocial.equals("902")) {
        suspender "Não há desconto de contribuição previdenciária para estagiários com o 'Código eSocial' diferente de '902' informado na categoria do trabalhador"
    }
}
if (TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    if (autonomo.codESocial.equals("741")) {
        suspender "Não há desconto de contribuição previdenciária para autônomos da categoria MEI com o 'Código eSocial' igual a '741' informado na categoria do trabalhador"
    }
}
if (Funcoes.possuiPrevidenciaFederal(matricula.tipo)) {
    double base
    def dataRescisao
    boolean possuiOutrosVinculos = matricula.possuiMultiploVinculo
    if (TipoMatricula.FUNCIONARIO.equals(matricula.tipo)) {
        dataRescisao = calculo.dataRescisao
    }
    int diaFinal
    int afasservmil = Funcoes.afasservmil()
    int afasacidtrab = Funcoes.afasacidtrab()
    if (dataRescisao == null) {
        if (calculo.quantidadeDiasCompetencia > 30 || afasservmil > 0 || afasacidtrab > 0) {
            diaFinal = 30
        } else {
            diaFinal = calculo.quantidadeDiasCompetencia
        }
    } else {
        diaFinal = Datas.dia(dataRescisao)
    }
    double abatimentoInss
    int diasSemDireito = afasservmil + afasacidtrab //dias sem direito
    if (diasSemDireito > 0) {
        abatimentoInss = Numeros.arredonda(Funcoes.calcprop(Funcoes.remuneracao(matricula.tipo).valor, Funcoes.cnvdpbase(diasSemDireito)), 2)
    }
    int dias = diaFinal - diasSemDireito
    boolean rescisaoSemRecalcularMensal = TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && !calculo.recalcularMensal
    if (dias > 0) {
        double baseMatricula = Bases.valor(Bases.INSS) + Bases.valor(Bases.INSSFER)
        if (rescisaoSemRecalcularMensal) {
            baseMatricula += Bases.valorCalculado(Bases.INSS, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
            baseMatricula += Bases.valorCalculado(Bases.INSSFER, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
            baseMatricula += Bases.valorCalculado(Bases.INSS, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
            baseMatricula += Bases.valorCalculado(Bases.INSSFER, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
        }
        //Busca o valor dos eventos com código 169, classificação BASINSSOUTEMP (funcionário)
        //ou BASINSSOUTEMPAUTONOMO (autônomo) nas folhas de férias, conforme regra de cálculo da base/retenção de INSS
        double retencaoInssOutraEmpresaFerias
        double baseInssOutraEmpresaFuncionarioFerias
        double baseInssOutraEmpresaAutonomoFerias
        folhasPeriodo.buscaFolhasProcessamento(TipoProcessamento.FERIAS).find { f -> f.folhaPagamento && f.inicioGozoFeriasCalculadas &&
                Datas.mes(Funcoes.paraData(f.inicioGozoFeriasCalculadas)).equals(Datas.mes(calculo.competencia)) &&
                Datas.mes(Funcoes.paraData(f.dataPagamento)).equals(Datas.mes(calculo.competencia)) &&
                f.eventos.each { e ->
                    if (e.codigo == 169) {
                        retencaoInssOutraEmpresaFerias += e.valor
                    }
                    if (e.classificacao && e.classificacao.equals(ClassificacaoEvento.BASINSSOUTEMP)) {
                        baseInssOutraEmpresaFuncionarioFerias += e.valor
                    }
                    if (e.classificacao && e.classificacao.equals(ClassificacaoEvento.BASINSSOUTEMPAUTONOMO)) {
                        baseInssOutraEmpresaAutonomoFerias += e.valor
                    }
                }
        }
        double baseInssOutraEmpresaFuncionario = Bases.valor(Bases.INSSOUTRA) + Funcoes.buscaBaseDeOutrosProcessamentos(Bases.INSSOUTRA) + baseInssOutraEmpresaFuncionarioFerias
        double baseInssOutraEmpresaAutonomo = Bases.valor(Bases.INSSOUTAUTO) + Funcoes.buscaBaseDeOutrosProcessamentos(Bases.INSSOUTAUTO) + baseInssOutraEmpresaAutonomoFerias
        double basePrevidencia = baseMatricula + baseInssOutraEmpresaFuncionario + baseInssOutraEmpresaAutonomo
        if (diasSemDireito == 0) {
            base = basePrevidencia
        } else {
            base = basePrevidencia - abatimentoInss
        }
        boolean autonomoComCodEsocialIniciandoEm7 = TipoMatricula.AUTONOMO.equals(matricula.tipo) && autonomo.codESocial && autonomo.codESocial.startsWith('7')
        if (base > 0) {
            double baseOutrosVinculos
            double inssOutrosVinculos
            if (possuiOutrosVinculos) {
                baseOutrosVinculos = Funcoes.getValorBaseMultiplosVinculos(Bases.INSS, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                if (baseOutrosVinculos > 0) {
                    if (autonomoComCodEsocialIniciandoEm7) {
                        baseOutrosVinculos += Bases.valorCalculadoMultiplosVinculos(Bases.INSSFER, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                    }
                    base += baseOutrosVinculos
                    basePrevidencia += baseOutrosVinculos
                }
                inssOutrosVinculos = Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                    inssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
                }
                if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                    inssOutrosVinculos += Funcoes.getValorInssFeriasMultiplosVinculos()
                    inssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                    inssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
                }
                if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                    inssOutrosVinculos += Funcoes.getValorInssFeriasMultiplosVinculos()
                    inssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    inssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                }
            }
            def vaux = Lancamentos.valor(evento)
            if (vaux >= 0) {
                valorReferencia = vaux
                valorCalculado = vaux
            } else {
                double baseIntegralPrevidencia = basePrevidencia
                double descHorasAuxMaternidade
                if (Funcoes.afasadocao() > 0) {
                    descHorasAuxMaternidade = Eventos.valor(124)
                    baseIntegralPrevidencia -= descHorasAuxMaternidade
                }
                def valorMaximoRgps = EncargosSociais.RGPS.buscaMaior(1)
                if (baseIntegralPrevidencia > valorMaximoRgps) {
                    vaux = baseIntegralPrevidencia - valorMaximoRgps
                    Bases.compor(vaux, Bases.EXCEINSS)
                }
                vaux = base
                if (vaux > valorMaximoRgps) {
                    vaux = valorMaximoRgps
                }
                vaux -= baseInssOutraEmpresaAutonomo
                if (TipoMatricula.FUNCIONARIO.equals(matricula.tipo)) {
                    vaux2 = Numeros.trunca(vaux, 2)
                    if (funcionario.conselheiroTutelar) {
                        valorReferencia = 11
                    } else {
                        valorReferencia = EncargosSociais.RGPS.buscaContribuicao(vaux2, 2)
                    }
                } else {
                    if (EncargosSociaisFpas.ENTIDADE_BENEFICENTE.equals(EncargosSociais.codigoFpas) && TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
                        valorReferencia = 20
                    } else {
                        valorReferencia = 11
                    }
                }
                if (descHorasAuxMaternidade > 0) {
                    base -= descHorasAuxMaternidade
                    if (base > valorMaximoRgps) {
                        base = valorMaximoRgps
                    }
                    base -= baseInssOutraEmpresaAutonomo
                } else {
                    base = vaux
                }
                double valorRetencaoInss
                if (autonomoComCodEsocialIniciandoEm7) {
                    double descontoValorBaseOutrosVinculos = baseInssOutraEmpresaFuncionario + baseOutrosVinculos
                    base -= descontoValorBaseOutrosVinculos
                } else {
                    valorRetencaoInss = Eventos.valor(169) + Funcoes.buscaValorDeOutrosProcessamentos(169) + retencaoInssOutraEmpresaFerias
                }
                double inssCalculadoEmFolhaMensalAnteriorRescisao
                if (rescisaoSemRecalcularMensal) {
                    inssCalculadoEmFolhaMensalAnteriorRescisao += Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    inssCalculadoEmFolhaMensalAnteriorRescisao += Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                }
                double inssFerias = Funcoes.getInssFerias().valor
                if (Funcoes.inicioCompetencia() >= Datas.data(2020, 3, 1) && !TipoMatricula.ESTAGIARIO.equals(matricula.tipo) && !TipoMatricula.AUTONOMO.equals(matricula.tipo) && !funcionario.conselheiroTutelar) {
                    vaux = Funcoes.calculoProgressivoINSS(base)
                    valorCalculado = Numeros.arredonda(vaux, 2) - Numeros.arredonda(inssFerias, 2) - inssOutrosVinculos - valorRetencaoInss - inssCalculadoEmFolhaMensalAnteriorRescisao
                } else {
                    vaux = (base * valorReferencia) / 100
                    valorCalculado = Numeros.trunca(vaux, 2) - Numeros.arredonda(inssFerias, 2) - valorRetencaoInss - inssCalculadoEmFolhaMensalAnteriorRescisao
                    if (!autonomoComCodEsocialIniciandoEm7) {
                        valorCalculado -= inssOutrosVinculos
                    }
                }
            }
            if (valorCalculado < 0) {
                Bases.compor(Numeros.absoluto(valorCalculado), Bases.DEVINSS)
            }
            Bases.compor(valorCalculado, Bases.DEDUCIRRFMES, Bases.ABATIRRF, Bases.MARGECONSIG)
        }
    }
    if (base != 0 || (base == 0 && diasSemDireito > 0 && Funcoes.diastrab() == 0)) {
        Bases.compor(abatimentoInss, Bases.ABATINSS)
    }
}

