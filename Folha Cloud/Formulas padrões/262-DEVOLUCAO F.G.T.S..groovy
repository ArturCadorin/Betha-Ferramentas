//Este evento deve ser configurado para calcular por último (Guia Geral > Calcular por último)
Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}
if (!TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && !TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos mensais ou rescisórios"
}
if (funcionario.categoriaSefipVinculo.toString() == 'MENOR_APRENDIZ') {
    valorReferencia = 2
} else {
    if (evento.taxa <= 0) {
        suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
    }
    valorReferencia = evento.taxa
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    double baseFgtsCompetencia
    double valorFgtsAnterior
    def tipoProcessamentoCalculo
    if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
        tipoProcessamentoCalculo = TipoProcessamento.MENSAL
    } else {
        tipoProcessamentoCalculo = TipoProcessamento.RESCISAO
    }
    //Busca o valor da base FGTS da matrícula nas folhas
    baseFgtsCompetencia += Bases.valor(Bases.FGTS)
    //Verifica se há as folhas mensais ou rescisórias já calculadas anteriormente na competência
    def qntFolhasCompetencia = folhasPeriodo.buscaFolhas().sum(0, { it.folhaPagamento && [TipoProcessamento.MENSAL, TipoProcessamento.RESCISAO].contains(it.tipoProcessamento) && ![SubTipoProcessamento.ADIANTAMENTO].contains(it.subTipoProcessamento) ? 1 : 0})
    if (qntFolhasCompetencia > 0) {
        //Busca o valor pago para a matrícula pela classificação de FGTS nas folhas
        valorFgtsAnterior += Eventos.valorCalculado(ClassificacaoEvento.FGTS, TipoValor.CALCULADO, tipoProcessamentoCalculo, SubTipoProcessamento.INTEGRAL, calculo.competencia)
        valorFgtsAnterior += Eventos.valorCalculado(ClassificacaoEvento.FGTS, TipoValor.CALCULADO, tipoProcessamentoCalculo, SubTipoProcessamento.COMPLEMENTAR, calculo.competencia)
    } else {
        //Busca o valor pago para a matrícula pela classificação de FGTS na folha atual
        valorFgtsAnterior += folha.eventos.sum(0,{ClassificacaoEvento.FGTS.equals(it.classificacao) ? it.valor : 0 })
    }
    if (calculo.recolherFgtsPelaSefip) {
        double baseFgts13 = Bases.valor(Bases.FGTS13) +
                Bases.valorCalculado(Bases.FGTS13, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL) +
                Bases.valorCalculado(Bases.FGTS13, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO)
        if (baseFgts13 > 0) {
            baseFgtsCompetencia += baseFgts13
            valorFgtsAnterior += Eventos.valor(284) +
                    Eventos.valor(37) +
                    Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                    Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO, calculo.competencia)
        }
    }
    vaux = baseFgtsCompetencia * valorReferencia / 100
    double fgtsValorAtualizado = Numeros.trunca(vaux,2)
    if (valorFgtsAnterior > fgtsValorAtualizado) {
        valorCalculado = valorFgtsAnterior - fgtsValorAtualizado
    } else {
        suspender "Não há valor de devolução a ser gerado nesta competência"
    }
}

