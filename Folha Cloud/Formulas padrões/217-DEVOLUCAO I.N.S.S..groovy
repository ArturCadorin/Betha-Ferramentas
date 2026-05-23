//Este evento deve ser configurado para calcular por último (Guia Geral > Calcular por último)
if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para aposentados e pensionistas"
}
if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    if (!estagiario.codESocial.equals("902")) {
        suspender "Não há desconto de contribuição previdenciária para estagiários com o 'Código eSocial' diferente de '902' informado na categoria do trabalhador"
    }
}
boolean autonomoComCodEsocialIniciandoEm7
if (TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    if (autonomo.codESocial.equals("741")) {
        suspender "Não há desconto de contribuição previdenciária para autônomos da categoria MEI com o 'Código eSocial' igual a '741' informado na categoria do trabalhador"
    }
    autonomoComCodEsocialIniciandoEm7 = autonomo.codESocial && autonomo.codESocial.startsWith('7')
}
if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && !TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos mensais ou rescisórios"
} else {
    if (SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
        suspender "O evento não é calculado no subtipo de processamento 'adiantamento'"
    }
}
if (Funcoes.possuiPrevidenciaFederal(matricula.tipo)) {
    double vvar = Lancamentos.valor(evento)
    if (vvar >= 0) {
        valorCalculado = vvar
    } else {
        boolean possuiOutrosVinculos = matricula.possuiMultiploVinculo
        double baseInssCompetencia
        double valorInssAnterior
        double baseInssBasesOutrasEmpresasFuncionario
        double baseInssBasesOutrasEmpresasAutonomo
        double valorInssBasesOutrasEmpresas
        double baseInssOutrosVinculos
        double valorInssOutrosVinculos
        double valorDevolucaoInssOutrosVinculos
        //Busca o valor dos eventos com código 169, classificação BASINSSOUTEMP (funcionário)
        //ou BASINSSOUTEMPAUTONOMO (autônomo) nas folhas de férias, conforme regra de cálculo da base/retenção de INSS
        folhasPeriodo.buscaFolhasProcessamento(TipoProcessamento.FERIAS).find { f -> f.folhaPagamento && f.inicioGozoFeriasCalculadas &&
                Datas.mes(Funcoes.paraData(f.inicioGozoFeriasCalculadas)).equals(Datas.mes(calculo.competencia)) &&
                Datas.mes(Funcoes.paraData(f.dataPagamento)).equals(Datas.mes(calculo.competencia)) &&
                f.eventos.each { e ->
                    if (e.codigo == 169) {
                        valorInssBasesOutrasEmpresas += e.valor
                    }
                    if (e.classificacao && e.classificacao.equals(ClassificacaoEvento.BASINSSOUTEMP)) {
                        baseInssBasesOutrasEmpresasFuncionario += e.valor
                    }
                    if (e.classificacao && e.classificacao.equals(ClassificacaoEvento.BASINSSOUTEMPAUTONOMO)) {
                        baseInssBasesOutrasEmpresasAutonomo += e.valor
                    }
                }
        }
        //Verifica se há as folhas mensais ou rescisórias já calculadas anteriormente na competência
        def qntFolhasCompetencia = folhasPeriodo.buscaFolhas().sum(0, { it.folhaPagamento && [TipoProcessamento.MENSAL, TipoProcessamento.RESCISAO].contains(it.tipoProcessamento) && ![SubTipoProcessamento.ADIANTAMENTO].contains(it.subTipoProcessamento) ? 1 : 0})
        if (qntFolhasCompetencia > 0) {
            //Busca o valor da base INSSFER proporcional nas folhas
            baseInssCompetencia += Bases.valor(Bases.INSSFER)
            if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                baseInssCompetencia += Bases.valorCalculado(Bases.INSSFER, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                baseInssCompetencia += Bases.valorCalculado(Bases.INSSFER, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
            }
            //Base e Retenção de Outras Empresas
            //Busca o valor das bases INSSOUTRA (funcionário) e INSSOUTAUTO (autônomo) nas folhas
            baseInssBasesOutrasEmpresasFuncionario += Bases.valor(Bases.INSSOUTRA)
            baseInssBasesOutrasEmpresasFuncionario += Funcoes.buscaBaseDeOutrosProcessamentos(Bases.INSSOUTRA)
            baseInssBasesOutrasEmpresasAutonomo += Bases.valor(Bases.INSSOUTAUTO)
            baseInssBasesOutrasEmpresasAutonomo += Funcoes.buscaBaseDeOutrosProcessamentos(Bases.INSSOUTAUTO)
            baseInssCompetencia += baseInssBasesOutrasEmpresasFuncionario
            baseInssCompetencia += baseInssBasesOutrasEmpresasAutonomo
            //Busca o valor descontado pelo código do evento de retenção INSS de outras empresas nas folhas
            //Se for um autônomo com categoria eSocial iniciada em 7, o valor não será considerado
            if (!autonomoComCodEsocialIniciandoEm7) {
                valorInssBasesOutrasEmpresas += Eventos.valor(169)
                valorInssBasesOutrasEmpresas += Funcoes.buscaValorDeOutrosProcessamentos(169)
                valorInssAnterior += valorInssBasesOutrasEmpresas
            }
            //Bases e Descontos de INSS
            //Busca o valor da base INSS da matrícula nas folhas
            baseInssCompetencia += Bases.valor(Bases.INSS)
            if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                baseInssCompetencia += Bases.valorCalculado(Bases.INSS, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                baseInssCompetencia += Bases.valorCalculado(Bases.INSS, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
            }
            //Busca o valor da base INSS dos demais vínculos do servidor nas folhas
            if (possuiOutrosVinculos) {
                baseInssOutrosVinculos += Funcoes.getValorBaseMultiplosVinculos(Bases.INSS, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                //Se for um autônomo com categoria eSocial iniciada em 7, somará também a base de INSSFER
                if (autonomoComCodEsocialIniciandoEm7) {
                    baseInssOutrosVinculos += Bases.valorCalculadoMultiplosVinculos(Bases.INSSFER, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                }
                if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                    baseInssOutrosVinculos += Funcoes.getValorBaseMultiplosVinculos(Bases.INSS, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    baseInssOutrosVinculos += Funcoes.getValorBaseMultiplosVinculos(Bases.INSS, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                    if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                        baseInssOutrosVinculos += Funcoes.getValorBaseMultiplosVinculos(Bases.INSS, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                        baseInssOutrosVinculos += Funcoes.getValorBaseMultiplosVinculos(Bases.INSS, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
                    }
                }
                baseInssCompetencia += baseInssOutrosVinculos
            }
            //Busca o valor descontado da matrícula pela classificação de INSS nas folhas
            valorInssAnterior += Eventos.valorCalculado(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia)
            valorInssAnterior += Eventos.valorCalculado(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
            valorInssAnterior += Eventos.valorCalculado(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL, calculo.competencia)
            valorInssAnterior += Eventos.valorCalculado(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
            //Busca o valor descontado dos demais vínculos do servidor pela classificação de INSS nas folhas
            //Se for um autônomo com categoria eSocial iniciada em 7, desconsiderará o valor já descontado de outras matrículas do servidor
            if (possuiOutrosVinculos && !autonomoComCodEsocialIniciandoEm7) {
                valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                    valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
                }
                if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                    valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                    valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
                }
                if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                    valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                }
                valorInssAnterior += valorInssOutrosVinculos
            }
            //Valores pagos de Devolução de INSS
            //Busca o valor pago a matrícula pela classificação de DEVINSS nas folhas
            valorInssAnterior -= Eventos.valorCalculado(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, calculo.competencia)
            valorInssAnterior -= Eventos.valorCalculado(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
            valorInssAnterior -= Eventos.valorCalculado(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL, calculo.competencia)
            valorInssAnterior -= Eventos.valorCalculado(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
            //Busca o valor pago aos demais vínculos do servidor pela classificação de DEVINSS nas folhas
            //Se for um autônomo com categoria eSocial iniciada em 7, desconsiderará o valor já devolvido de outras matrículas do servidor
            if (possuiOutrosVinculos && !autonomoComCodEsocialIniciandoEm7) {
                valorDevolucaoInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                    valorDevolucaoInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
                }
                if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                    valorDevolucaoInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                    valorDevolucaoInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
                }
                if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                    valorDevolucaoInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    valorDevolucaoInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.DEVINSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                }
                valorInssAnterior -= valorDevolucaoInssOutrosVinculos
            }
        } else {
            //Busca o valor da base INSSFER proporcional na folha atual
            baseInssCompetencia += Bases.valor(Bases.INSSFER)
            //Busca o valor da base INSSOUTRA (funcionário) e INSSOUTAUTO (autônomo) nas folhas atual e de férias
            baseInssBasesOutrasEmpresasFuncionario += Bases.valor(Bases.INSSOUTRA)
            baseInssBasesOutrasEmpresasAutonomo += Bases.valor(Bases.INSSOUTAUTO)
            baseInssCompetencia += baseInssBasesOutrasEmpresasFuncionario
            baseInssCompetencia += baseInssBasesOutrasEmpresasAutonomo
            //Busca o valor descontado pelo código do evento de retenção INSS de outras empreas nas folhas atual e de férias
            //Se for um autônomo com categoria eSocial iniciada em 7, o valor não será considerado
            if (!autonomoComCodEsocialIniciandoEm7) {
                valorInssBasesOutrasEmpresas += Eventos.valor(169)
                valorInssAnterior += valorInssBasesOutrasEmpresas
            }
            //Busca o valor da base INSS na folha atual
            baseInssCompetencia += Bases.valor(Bases.INSS)
            //Busca o valor descontado da matrícula pela classificação de INSS na folha atual
            valorInssAnterior += folha.eventos.sum(0,{ClassificacaoEvento.INSS.equals(it.classificacao) ? it.valor : 0 })
            //Busca o valor da base INSS dos demais vínculos do servidor nas folhas
            if (possuiOutrosVinculos) {
                baseInssOutrosVinculos += Funcoes.getValorBaseMultiplosVinculos(Bases.INSS, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                //Se for um autônomo com categoria eSocial iniciada em 7, somará também a base de INSSFER
                if (autonomoComCodEsocialIniciandoEm7) {
                    baseInssOutrosVinculos += Bases.valorCalculadoMultiplosVinculos(Bases.INSSFER, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                }
                baseInssCompetencia += baseInssOutrosVinculos
                //Se for um autônomo com categoria eSocial iniciada em 7, desconsiderará o valor já descontado de outras matrículas do servidor
                if (!autonomoComCodEsocialIniciandoEm7) {
                    //Busca o valor descontado dos demais vínculos do servidor pela classificação de INSS nas folhas
                    valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                    if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                        valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                        valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
                    }
                    if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                        valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                        valorInssOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(ClassificacaoEvento.INSS, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                    }
                    valorInssAnterior += valorInssOutrosVinculos
                }
            }
        }
        double tetoInss = EncargosSociais.RGPS.buscaMaior(1)
        if (baseInssCompetencia > tetoInss) {
            baseInssCompetencia = tetoInss
        }
        baseInssCompetencia -= baseInssBasesOutrasEmpresasAutonomo
        if (TipoMatricula.FUNCIONARIO.equals(matricula.tipo)) {
            double taxaBaseInssCompetencia = Numeros.trunca(baseInssCompetencia, 2)
            if (funcionario.conselheiroTutelar) {
                valorReferencia = 11
            } else {
                valorReferencia = EncargosSociais.RGPS.buscaContribuicao(taxaBaseInssCompetencia, 2)
            }
        } else {
            if (EncargosSociaisFpas.ENTIDADE_BENEFICENTE.equals(EncargosSociais.codigoFpas) && TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
                valorReferencia = 20
            } else {
                valorReferencia = 11
            }
        }
        if (autonomoComCodEsocialIniciandoEm7) {
            double descontoValorBaseOutrosVinculos = baseInssBasesOutrasEmpresasFuncionario + baseInssOutrosVinculos
            baseInssCompetencia -= descontoValorBaseOutrosVinculos
        }
        double inssValorAtualizado
        double valorDiferencaInssAnterior
        if (Funcoes.inicioCompetencia() >= Datas.data(2020, 3, 1) && !TipoMatricula.AUTONOMO.equals(matricula.tipo) && !funcionario.conselheiroTutelar) {
            inssValorAtualizado = Numeros.arredonda(Funcoes.calculoProgressivoINSS(baseInssCompetencia), 2)
        } else {
            inssValorAtualizado = Numeros.trunca((baseInssCompetencia * valorReferencia) / 100, 2)
            if (!autonomoComCodEsocialIniciandoEm7) {
                inssValorAtualizado -= valorInssOutrosVinculos
            }
        }
        valorDiferencaInssAnterior = valorInssAnterior - valorInssBasesOutrasEmpresas
        if (valorInssBasesOutrasEmpresas > inssValorAtualizado && valorDiferencaInssAnterior <= 0) {
            suspender "A retenção efetuada de I.N.S.S. de outras empresas é maior que o teto estabelecido na manutenção de estabelecimento da entidade. Para estes casos, a devolução de I.N.S.S. não será calculada, tendo em vista que o valor ultrapassa o limite estabelecido"
        }
        if (valorInssAnterior > inssValorAtualizado) {
            valorCalculado = valorInssAnterior - inssValorAtualizado
        } else {
            suspender "Não há valor de devolução a ser gerado nesta competência"
        }
    }
}

