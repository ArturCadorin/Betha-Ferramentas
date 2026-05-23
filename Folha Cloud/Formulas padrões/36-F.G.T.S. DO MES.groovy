Funcoes.somenteFuncionarios()

if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}

if (funcionario.categoriaSefipVinculo.toString() == 'MENOR_APRENDIZ') {
    valorReferencia = 2
} else {
    if (evento.taxa <= 0) {
        suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
    }
    valorReferencia = evento.taxa
}

BigDecimal vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    BigDecimal base = Bases.valor(Bases.FGTS)
    BigDecimal fgtsAux = Eventos.valor(904)

    if (calculo.recolherFgtsPelaSefip) {
        BigDecimal baseFgts13 = Bases.valor(Bases.FGTS13) +
                Bases.valorCalculado(Bases.FGTS13, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL) +
                Bases.valorCalculado(Bases.FGTS13, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO)

        if (baseFgts13 > 0) {
            base += baseFgts13
            fgtsAux += Eventos.valor(284) +
                    Eventos.valor(37) +
                    Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL, calculo.competencia) +
                    Eventos.valorCalculado(ClassificacaoEvento.FGTS13SAL, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO, calculo.competencia)
        }
    }

    vaux = base * valorReferencia / 100
    vaux -= fgtsAux
    valorCalculado = Numeros.trunca(vaux,2)
}

