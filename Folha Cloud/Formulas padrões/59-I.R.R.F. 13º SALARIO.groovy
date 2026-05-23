if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro'
}
if (SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
    suspender"O evento não é calculado no subtipo de processamento 'adiantamento'"
}
if (calculo.rra || TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos vinculados a pagamentos anteriores'
}
valorCalculado = 0
double desc
double vaux2
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
    vaux2 = Numeros.trunca(vaux, 2)
    desc = EncargosSociais.IRRF.buscaContribuicao(vaux2, 3)
} else {
    boolean possuiMultiploVinculo = matricula.possuiMultiploVinculo
    double baseOutrosVinculos
    double irrfOutrosVinculos
    double baseOutrosVinculos13IntegralAdiantado
    if (possuiMultiploVinculo) {
        baseOutrosVinculos = Funcoes.getValorBaseMultiplosVinculos(Bases.IRRF13, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        irrfOutrosVinculos = Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
            irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
        }
        if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
            irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
            irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
        }
        if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
            irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.DECIMO_TERCEIRO_SALARIO, SubTipoProcessamento.INTEGRAL)
        }
        if (folha.complementoDecimoTerceiro) {
            baseOutrosVinculos13IntegralAdiantado = Funcoes.buscaValorEvento13SalarioIntegralAdiantado(133)
        }
     }
    double base = Bases.valor(Bases.IRRF13) + Bases.valor(Bases.IRRFOUTRA13) + Eventos.valor(200) + baseOutrosVinculos - baseOutrosVinculos13IntegralAdiantado -
                  Eventos.valor(136) - Eventos.valorCalculadoMultiplosVinculos(136, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento) -
                  Eventos.valor(190) - Eventos.valorCalculadoMultiplosVinculos(190, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
    if (base > EncargosSociais.IRRF.buscaContribuicao(0, 1)) {
        vaux2 = Numeros.trunca(base, 2)
        desc = EncargosSociais.IRRF.buscaContribuicao(vaux2, 3)
        double valorRetencaoIrrf = Eventos.valor(175) + Funcoes.buscaValorDeOutrosProcessamentos(175) + Eventos.valorCalculadoMultiplosVinculos(175, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        valorReferencia = EncargosSociais.IRRF.buscaContribuicao(vaux2, 2)
        valorCalculado = ((vaux2 * valorReferencia) / 100) - desc - irrfOutrosVinculos - valorRetencaoIrrf
    } else {
        suspender 'Não há valor de base do IRRF sobre 13º salário para cálculo ou o valor da base é inferior ao valor da faixa inicial da tabela de IRRF que está vigente na competência de cálculo'
    }
}

if (Datas.ano(calculo.dataPagamento) >= 2026) {
    valorCalculado -= Bases.valor(Bases.REDUTIRRF13)
    if (valorCalculado < 0) {
        valorCalculado = 0
    }
}

if (valorCalculado < EncargosSociais.IRRF.minimoIrrfDarf) {
    valorCalculado = 0
} else {
    Bases.compor(desc, Bases.DESCIRRF13)
}
if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    Bases.compor(valorCalculado, Bases.MARGECONSIG)
}
valorCalculado -= Funcoes.buscaValorEvento13SalarioIntegralAdiantado()
