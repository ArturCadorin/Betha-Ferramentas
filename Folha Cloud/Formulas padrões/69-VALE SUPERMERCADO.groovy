if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) || TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        if (Funcoes.diasferias() > 0 && TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
            suspender "O evento deve ser calculado no processamento de férias pois há dias de férias na competência"
        }
        if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
            if (Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL) > 0) {
                suspender "O evento já foi calculado em processamento de férias anterior na competência"
            }
        }
        valorReferencia = vvar
        valorCalculado = vvar
    }
}

