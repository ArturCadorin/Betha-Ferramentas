Funcoes.somenteAposentadosPensionistas()
Funcoes.permiteCalculoParcelaIsentaIrrf()
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro'
}
if (calculo.rra || TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos vinculados a pagamentos anteriores'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    double base = Bases.valor(Bases.PAISIR13SA)
    double valorParcelaIsenta = EncargosSociais.IRRF.parcelaIsentaAposentadoria - Funcoes.buscaValorEvento13SalarioIntegralAdiantado() - Eventos.valorCalculadoMultiplosVinculos(evento.codigo, TipoValor.CALCULADO, calculo.tipoProcessamento, calculo.subTipoProcessamento)
    if (valorParcelaIsenta > base) {
        valorParcelaIsenta = base
    }
    valorCalculado = valorParcelaIsenta
}

