Funcoes.somenteAposentadosPensionistas()
Funcoes.permiteCalculoParcelaIsentaIrrf()
if (calculo.rra && !TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado pela folha complementar de RRA. O valor total será calculado pela folha do pagamento anterior na qual este cálculo de RRA está vinculado. Após este cálculo, o sistema incluirá automaticamente o valor em cada parcela do processo de pagamento anterior de RRA que foram lançadas anteriormente em folhas complementares'
}
if (!calculo.rra && TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado na folha de pagamento anterior pois o mesmo não se trata de um RRA. O valor deve ser lançado diretamente em cada folha complementar vinculada ao pagamento anterior selecionado'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    double base
    if (calculo.rra) {
        base = Bases.valor(Bases.PARISIRRFRRA)
    } else {
        base = Bases.valor(Bases.PARCISENIRRF)
    }
    double valorParcelaIsenta = EncargosSociais.IRRF.parcelaIsentaAposentadoria - Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
    if (valorParcelaIsenta > base) {
        valorParcelaIsenta = base
    }
    valorCalculado = valorParcelaIsenta
}

