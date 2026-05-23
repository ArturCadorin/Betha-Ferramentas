Funcoes.somenteAposentadosPensionistas()
if (!Funcoes.possuiPrevidencia(TipoPrevidencia.PREVIDENCIA_PROPRIA)) {
    suspender 'Este cálculo é realizado apenas para matrículas contribuintes da previdência própria'
}
double valorParcelaIsenta = EncargosSociais.RGPS.buscaMaior(1) //retorna o teto do INSS
double fundoPrevidencia = Bases.valor(Bases.FUNDOPREV)
if (fundoPrevidencia < valorParcelaIsenta) {
    valorParcelaIsenta = fundoPrevidencia
}
valorCalculado = valorParcelaIsenta

