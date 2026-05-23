Funcoes.somenteAposentadosPensionistas()
if (!servidor.possuiMolestiaGrave) {
    suspender 'O evento deve ser calculado apenas para pessoas que possuam moléstia grave'
}
if (calculo.rra && !TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado pela folha complementar de RRA. O valor total será calculado pela folha do pagamento anterior na qual este cálculo de RRA está vinculado. Após este cálculo, o sistema incluirá automaticamente o valor em cada parcela do processo de pagamento anterior de RRA que foram lançadas anteriormente em folhas complementares'
}
if (!calculo.rra && TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado na folha de pagamento anterior pois o mesmo não se trata de um RRA. O valor deve ser lançado diretamente em cada folha complementar vinculada ao pagamento anterior selecionado'
}
if (TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    valorCalculado = Bases.valorRra(Bases.PARCISENIRRF) + Bases.valorRra(Bases.PAISIR13SA)
} else {
    valorCalculado = Bases.valor(Bases.PARCISENIRRF)
}

