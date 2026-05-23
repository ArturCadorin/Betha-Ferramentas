if (!TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para estagiários"
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
        double base = Bases.valor(Bases.IRRFFER) + Bases.valorCalculado(Bases.IRRFFER, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
        if (servidor.possuiMolestiaGrave) {
            base += Eventos.valor(215) + Eventos.valor(227)
        }
        if (base > EncargosSociais.IRRF.buscaContribuicao(0, 1)) {
            vaux2 = Numeros.trunca(base, 2)
            desc = EncargosSociais.IRRF.buscaContribuicao(vaux2, 3)
            valorReferencia = EncargosSociais.IRRF.buscaContribuicao(vaux2, 2)
            valorCalculado = (vaux2 * valorReferencia / 100) - desc + Eventos.valor(173)
        } else {
            suspender 'Não há valor de base do IRRF sobre férias para cálculo ou o valor da base é inferior ao valor da faixa inicial da tabela de IRRF que está vigente na competência de cálculo'
        }
    }
    if (valorCalculado < EncargosSociais.IRRF.minimoIrrfDarf) {
        valorCalculado = 0
    } else {
        Bases.compor(desc, Bases.DESCIRRF)
    }
}

