if (!TipoMatricula.FUNCIONARIO.equals(matricula.tipo) && !TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para funcionários ou autônomos"
}
if (!Funcoes.possuiPrevidenciaFederal(matricula.tipo)) {
    suspender "Este cálculo é realizado apenas para matrículas contribuintes da previdência federal"
}
if (TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    if (calculo.competencia < Datas.data(2003, 4, 1)) {
        suspender "Para autônomos, este cálculo deve ser executado apenas em competências posteriores a 03/2003"
    }
    if (autonomo.codESocial.equals("741")) {
        suspender "Não há desconto de contribuição previdenciária para autônomos da categoria MEI com o 'Código eSocial' igual a '741' informado na categoria do trabalhador"
    }
}
if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
    suspender "Este evento não é calculado em processamentos de décimo terceiro"
}
if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
    if (Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL) > 0 || Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento) > 0) {
        suspender "O evento já foi calculado no mesmo processamento para outro subtipo de cálculo nesta competência"
    }
}
if (folha.folhaPagamento) {
    def retencao = Lancamentos.valor(evento)
    if (retencao <= 0) {
        if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            def diasGozoLiquido = periodoConcessao.diasGozo - periodoConcessao.diasFalta;
            if (diasGozoLiquido <= 0) {
                suspender "Este evento não é calculado no processamento de férias quando não há dias de gozo informados"
            } else {
                def cptInicioGozo = Datas.data(Datas.ano(periodoConcessao.dataInicioGozo), Datas.mes(periodoConcessao.dataInicioGozo), 1)
                if (Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, cptInicioGozo) > 0) {
                    suspender "O evento já foi calculado em processamento de férias anterior com gozo"
                }
                retencao = BasesOutrasEmpresas.buscaPor(TipoProcessamento.MENSAL, cptInicioGozo, OrigemBaseOutraEmpresa.FUNCIONARIO).sum(0, { it.valorRetidoInss })
            }
        } else {
            if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                if (Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL) > 0 || Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) > 0 || Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR) > 0) {
                    suspender "O evento já foi calculado em processamento mensal ou de férias nesta competência"
                }
            }
            if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                double valorFerias
                folhasPeriodo.buscaFolhasProcessamento(TipoProcessamento.FERIAS).find { f -> f.folhaPagamento && f.inicioGozoFeriasCalculadas &&
                        Datas.mes(Funcoes.paraData(f.inicioGozoFeriasCalculadas)).equals(Datas.mes(calculo.competencia)) &&
                        Datas.mes(Funcoes.paraData(f.dataPagamento)).equals(Datas.mes(calculo.competencia)) &&
                        f.eventos.each { e -> if (e.codigo == evento.codigo) { valorFerias += e.valor } }
                }
                if (valorFerias > 0) {
                    suspender "O evento já foi calculado no processamento de férias com gozo nesta competência"
                }
            }
            retencao = BasesOutrasEmpresas.buscaPor(TipoProcessamento.MENSAL, calculo.competencia, OrigemBaseOutraEmpresa.FUNCIONARIO).sum(0, { it.valorRetidoInss })
        }
    }
    if (retencao <= 0) {
        suspender "Não há valor lançado de retenção de INSS de outras empresas originado de funcionários para o servidor"
    }
    def remuneracao = Funcoes.remuneracao(matricula.tipo).unidade
    if (calculo.quantidadeDiasCompetencia.equals(31) && (remuneracao.equals(UnidadePagamento.HORISTA) || remuneracao.equals(UnidadePagamento.DIARISTA))) {
        valorCalculado = Funcoes.calcprop(retencao, Funcoes.cnvdpbase(31))
    } else {
        valorCalculado = Funcoes.calcprop(retencao, Funcoes.cnvdpbase(30))
    }
}

