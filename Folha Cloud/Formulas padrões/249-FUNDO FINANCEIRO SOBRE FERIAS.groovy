Funcoes.somenteFuncionarios()
if (!TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos de férias"
}
if (funcionario.possuiPrevidencia(TipoPrevidencia.FUNDO_FINANCEIRO)) {
    def baseprev = Bases.valor(Bases.FUNDFINFER)
    if (baseprev <= 0) {
        suspender 'Não há valor de base de fundo financeiro de férias para cálculo'
    }
    int forma = 1 //1-Aplicar %, 2-Usar tabela do fundo financeiro
    def vaux
    def taxa
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorReferencia = vvar
        valorCalculado = vvar
    } else {
        if (forma == 1) {
            vaux = baseprev
            taxa = evento.taxa
        } else {
            vaux = EncargosSociais.RPPS.FundoFinanceiro.buscaMaior(1)
            if (baseprev <= vaux) {
                vaux = baseprev
            }
            def vaux2 = Numeros.trunca(vaux, 2)
            taxa = EncargosSociais.RPPS.FundoFinanceiro.buscaContribuicao(vaux2, 2)
        }
        valorReferencia = taxa
        valorCalculado = (vaux * taxa) / 100
    }
    Bases.compor(valorCalculado, Bases.DEDUCIRRFMES, Bases.ABATIRRF)
}

