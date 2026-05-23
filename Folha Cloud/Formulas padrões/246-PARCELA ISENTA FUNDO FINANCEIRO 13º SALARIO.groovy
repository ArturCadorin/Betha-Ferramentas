Funcoes.somenteAposentadosPensionistas()
if (!Funcoes.possuiPrevidencia(TipoPrevidencia.FUNDO_FINANCEIRO)) {
    suspender 'Este cálculo é realizado apenas para matrículas contribuintes do fundo financeiro'
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'A matrícula não tem direito a receber décimo terceiro'
}
double valorParcelaIsenta = EncargosSociais.RGPS.buscaMaior(1) //retorna o teto do INSS
double fundoFinanceiro = Bases.valor(Bases.FUNDFIN13)
if (fundoFinanceiro < valorParcelaIsenta) {
    valorParcelaIsenta = fundoFinanceiro
}
valorCalculado = valorParcelaIsenta - Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

