if (TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    if (autonomo.codESocial.equals("741")) {
        suspender "Não há desconto de imposto de renda para autônomos da categoria MEI com o 'Código eSocial' igual a '741' informado na categoria do trabalhador"
    }
}
if (calculo.rra && !TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado pela folha complementar de RRA. O valor total será calculado pela folha do pagamento anterior na qual este cálculo de RRA está vinculado. Após este cálculo, o sistema incluirá automaticamente o valor em cada parcela do processo de pagamento anterior de RRA que foram lançadas anteriormente em folhas complementares'
}
if (!calculo.rra && TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado na folha de pagamento anterior pois o mesmo não se trata de um RRA. O valor deve ser lançado diretamente em cada folha complementar vinculada ao pagamento anterior selecionado'
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
    double base
    double baseOutrosVinculos
    double irrfOutrosVinculos
    boolean possuiMultiploVinculo = matricula.possuiMultiploVinculo
    if (TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
        if (possuiMultiploVinculo) {
            baseOutrosVinculos = Funcoes.getValorBaseMultiplosVinculos(Bases.IRRFRRA, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            if (baseOutrosVinculos > 0) {
                irrfOutrosVinculos = Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            }
        }
        base = Bases.valor(Bases.IRRFRRA) + baseOutrosVinculos - Eventos.valor(135) -
                Eventos.valorCalculadoMultiplosVinculos(135, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento) -
                Eventos.valor(189) - Eventos.valorCalculadoMultiplosVinculos(189, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        if (base > EncargosSociais.IRRF.buscaContribuicao(0, 1)) {
            vaux2 = Numeros.trunca(base, 2)
            desc = EncargosSociais.IRRF.buscaContribuicao(vaux2, 3)
            valorReferencia = EncargosSociais.IRRF.buscaContribuicao(vaux2, 2)
            valorCalculado = ((vaux2 * valorReferencia) / 100) - desc - irrfOutrosVinculos
        } else {
            suspender 'Não há valor de base do IRRF de RRA para cálculo ou o valor da base é inferior ao valor da faixa inicial da tabela de IRRF majorada que está vigente na competência de cálculo'
        }
    } else {
        if (possuiMultiploVinculo) {
            if (!TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
                baseOutrosVinculos = Funcoes.getValorBaseMultiplosVinculos(Bases.IRRF, calculo.tipoProcessamento, calculo.subTipoProcessamento)
            }
            if (baseOutrosVinculos > 0) {
                irrfOutrosVinculos = Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
                if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
                    irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, SubTipoProcessamento.INTEGRAL)
                }
                if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
                    irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.INTEGRAL)
                    irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.RESCISAO, SubTipoProcessamento.COMPLEMENTAR)
                }
                if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
                    irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL)
                    irrfOutrosVinculos += Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
                }
            }
        }
        base = Bases.valor(Bases.IRRF) + Bases.valor(Bases.IRRFOUTRA) + Funcoes.buscaBaseDeOutrosProcessamentos(Bases.IRRFOUTRA) + baseOutrosVinculos -
                Eventos.valor(135) - Eventos.valorCalculadoMultiplosVinculos(135, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento) -
                Eventos.valor(189) - Eventos.valorCalculadoMultiplosVinculos(189, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
        if (base > EncargosSociais.IRRF.buscaContribuicao(0, 1)) {
            vaux2 = Numeros.trunca(base, 2)
            desc = EncargosSociais.IRRF.buscaContribuicao(vaux2, 3)
            double valorRetencaoIrrf = Eventos.valor(173) + Funcoes.buscaValorDeOutrosProcessamentos(173)
            valorReferencia = EncargosSociais.IRRF.buscaContribuicao(vaux2, 2)
            valorCalculado = ((vaux2 * valorReferencia) / 100) - desc - irrfOutrosVinculos - valorRetencaoIrrf
        } else {
            suspender 'Não há valor de base do IRRF para cálculo ou o valor da base é inferior ao valor da faixa inicial da tabela de IRRF que está vigente na competência de cálculo'
        }
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
