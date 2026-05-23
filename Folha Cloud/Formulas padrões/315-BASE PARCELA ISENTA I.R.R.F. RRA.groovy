Funcoes.somenteAposentadosPensionistas()
Funcoes.permiteCalculoParcelaIsentaIrrf()
if (!TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender "Este evento deve ser calculado apenas no processamento de pagamento anterior"
}
if (!calculo.rra) {
    suspender 'Este evento não é calculado na folha de pagamento anterior pois o mesmo não se trata de um RRA'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    valorReferencia = folha.quantidadeMesesRra
    valorCalculado = Bases.valorRra(Bases.PARCISENIRRF) + Bases.valorRra(Bases.PAISIR13SA)
}
Bases.compor(valorCalculado, Bases.PARISIRRFRRA)

