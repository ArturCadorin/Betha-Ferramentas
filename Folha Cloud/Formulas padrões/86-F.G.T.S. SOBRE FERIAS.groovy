Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}
if (!TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender 'O evento deve ser calculado apenas em processamentos de férias'
}
if (folha.folhaPagamento) {
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
        double base = Bases.valor(Bases.FGTS)
        double fgtsAux
        double baseFeriasAnteriores = Bases.valorCalculado(Bases.FGTS, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
        if (baseFeriasAnteriores > 0) {
            base += baseFeriasAnteriores
            fgtsAux += Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
        }
        if (calculo.recolherFgtsPelaSefip) {
            int diasFerias = Funcoes.diasferias()
            if (diasFerias >= 30 || (calculo.competencia.mes.equals(2) && diasFerias == calculo.quantidadeDiasCompetencia)) {
                double baseFgts13 = Bases.valorCalculado(Bases.FGTS13, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL) +
                        Bases.valorCalculado(Bases.FGTS13, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO)
                if (baseFgts13 > 0) {
                    base += baseFgts13
                    fgtsAux += Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                            Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO, calculo.competencia)
                }
            }
        }
        vaux = base * valorReferencia / 100
        vaux -= fgtsAux
        valorCalculado = Numeros.trunca(vaux,2)
    }
}

