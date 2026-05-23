if (!TipoMatricula.FUNCIONARIO.equals(matricula.tipo) && !TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para funcionários e estagiários"
}
if (!TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos de férias"
}
if (folha.folhaPagamento) {
    valorCalculado = 0
    double desc
    double vaux2
    def vaux = Lancamentos.valor(evento)
    if (vaux >= 0) {
        valorCalculado = vaux
        vaux2 = Numeros.trunca(vaux, 2)
        desc = EncargosSociais.IRRF.buscaContribuicao(vaux2, 3)
    } else {
        double base
        double baseOutrosVinculos
        double irrfOutrosVinculos
        boolean possuiMultiploVinculo = matricula.possuiMultiploVinculo
        if (possuiMultiploVinculo) {
            baseOutrosVinculos = Funcoes.getValorBaseMultiplosVinculos(Bases.IRRFFER, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            if (baseOutrosVinculos > 0) {
                irrfOutrosVinculos = Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            }
        }
        base = Bases.valor(Bases.IRRFFER) + baseOutrosVinculos + Bases.valor(Bases.IRRFOUTRA) + Bases.valorCalculado(Bases.IRRFFER, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL) + Bases.valorCalculado(Bases.IRRFOUTRA, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
        if (servidor.possuiMolestiaGrave) {
            base += Eventos.valor(198) + Eventos.valor(228)
        }
        if (base > EncargosSociais.IRRF.buscaContribuicao(0, 1)) {
            vaux2 = Numeros.trunca(base, 2)
            desc = EncargosSociais.IRRF.buscaContribuicao(vaux2, 3)
            valorReferencia = EncargosSociais.IRRF.buscaContribuicao(vaux2, 2)
            vaux = (vaux2 * valorReferencia / 100) - desc
            valorCalculado = vaux - irrfOutrosVinculos - Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL) - Eventos.valor(173)
        }
    }

    if (Datas.ano(calculo.dataPagamento) >= 2026) {
        valorCalculado -= Bases.valor(Bases.REDUTIRRF)
        if (valorCalculado < 0) {
            valorCalculado = 0
        }
    }

    if (valorCalculado < EncargosSociais.IRRF.minimoIrrfDarf) {
        valorCalculado = 0
    } else {
        Bases.compor(desc, Bases.DESCIRRF)
    }
}
