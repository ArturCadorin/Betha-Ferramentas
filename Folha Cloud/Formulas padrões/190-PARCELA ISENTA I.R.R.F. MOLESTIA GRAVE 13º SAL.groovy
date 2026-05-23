Funcoes.somenteAposentadosPensionistas()
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro'
}
if (!servidor.possuiMolestiaGrave) {
    suspender 'O evento deve ser calculado apenas para pessoas que possuam moléstia grave'
}
if (calculo.rra || TipoProcessamento.PAGAMENTO_ANTERIOR.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos vinculados a pagamentos anteriores'
}
valorCalculado = Bases.valor(Bases.PAISIR13SA) - Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

