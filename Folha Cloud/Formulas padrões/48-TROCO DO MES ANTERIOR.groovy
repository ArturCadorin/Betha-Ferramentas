def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorCalculado = vvar
} else {
    def mesAnterior = Datas.removeMeses(calculo.competencia, 1)
    def soma = Eventos.valorCalculado(39, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.ADIANTAMENTO, mesAnterior) +
            Eventos.valorCalculado(39, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, mesAnterior) +
            Eventos.valorCalculado(39, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.ADIANTAMENTO, mesAnterior) +
            Eventos.valorCalculado(39, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL, mesAnterior) +
            Eventos.valorCalculado(39, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, mesAnterior) + Eventos.valorCalculado(38, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.ADIANTAMENTO, mesAnterior)
    valorCalculado = soma
}

