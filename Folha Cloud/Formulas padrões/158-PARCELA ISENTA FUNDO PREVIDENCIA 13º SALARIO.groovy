Funcoes.somenteAposentadosPensionistas()
if (!Funcoes.possuiPrevidencia(TipoPrevidencia.PREVIDENCIA_PROPRIA)) {
    suspender 'Este cálculo é realizado apenas para matrículas contribuintes da previdência própria'
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro'
}
double valorParcelaIsenta = EncargosSociais.RGPS.buscaMaior(1) //retorna o teto do INSS
double fundoPrevidencia = Bases.valor(Bases.FUNDPREV13)
if (fundoPrevidencia < valorParcelaIsenta) {
    valorParcelaIsenta = fundoPrevidencia
}
valorCalculado = valorParcelaIsenta - Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

