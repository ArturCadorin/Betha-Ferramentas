if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorCalculado = vvar
    } else {
        def valorIntegral = Eventos.valorCalculado(126, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) +
                Eventos.valorCalculado(127, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) +
                Eventos.valorCalculado(128, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) +
                Eventos.valorCalculado(129, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
        def valorComplementar = Eventos.valor(126) + Eventos.valor(127) + Eventos.valor(128) + Eventos.valor(129)
        if (valorComplementar < valorIntegral) {
            valorCalculado = valorIntegral - valorComplementar
        }
    }
}

