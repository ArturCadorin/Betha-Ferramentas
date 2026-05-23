def vaux = Lancamentos.valor(evento)
if (vaux <= 0) {
    if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.INTEGRAL.equals(calculo.subTipoProcessamento)) {
        vaux = Eventos.valorCalculado(67, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.ADIANTAMENTO)
        vaux -= Eventos.valorCalculado(ClassificacaoEvento.ADISALNDESC, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.ADIANTAMENTO, calculo.competencia)
    }
}
valorCalculado = vaux

