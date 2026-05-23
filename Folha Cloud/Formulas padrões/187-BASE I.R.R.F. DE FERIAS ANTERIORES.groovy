if (!TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos de férias"
}
if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para estagiários"
}
valorCalculado = 0
double descferiasanteriores = Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
if (descferiasanteriores > 0) {
    double baseferanteriores = Bases.valorCalculado(Bases.IRRFFER, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
    valorCalculado = baseferanteriores - descferiasanteriores
    Bases.compor(valorCalculado, Bases.IRRFFER)
}

