Funcoes.somenteAposentadosPensionistas()
if (!Funcoes.possuiPrevidencia(TipoPrevidencia.FUNDO_FINANCEIRO)) {
    suspender 'Este cálculo é realizado apenas para matrículas contribuintes do fundo financeiro'
}
double valorParcelaIsenta = EncargosSociais.RGPS.buscaMaior(1) //retorna o teto do INSS
double fundoFinanceiro = Bases.valor(Bases.FUNDFIN)
if (fundoFinanceiro < valorParcelaIsenta) {
    valorParcelaIsenta = fundoFinanceiro
}
valorCalculado = valorParcelaIsenta

