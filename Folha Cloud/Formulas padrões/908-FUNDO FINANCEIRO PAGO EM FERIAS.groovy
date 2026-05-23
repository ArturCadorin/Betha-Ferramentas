Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.FUNDO_FINANCEIRO)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorCalculado = vvar
    } else {
        def fundoFinancFerias = Funcoes.getFundoFinanceiroFerias()
        valorReferencia = fundoFinancFerias.referencia
        valorCalculado = fundoFinancFerias.valor
    }
    if (valorCalculado > 0) {
        evento.replicado(true)
    }
}

