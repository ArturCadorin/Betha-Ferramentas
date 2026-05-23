//Este evento deve ser configurado para calcular por último (Guia Geral > Calcular por último)
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    BigDecimal totalLiquido = BigDecimal.valueOf(0)
    List eventosFolhaMensal = []
    if (SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
        folhas.buscaFolhasOutrasCompetencias(calculo.competencia, calculo.tipoProcessamento).each { folhaMensal ->
            eventosFolhaMensal.addAll(folhaMensal.eventos.collect { e ->
                [
                        codigo: e.codigo,
                        valor : e.valor
                ]
            })
        }
        BigDecimal diferenca = BigDecimal.valueOf(0)
        BigDecimal valorEvento = BigDecimal.valueOf(0)
        folhas.buscaFolhas().each { f ->
            f.eventos.each { e ->
                if (e.tipo == TipoEvento.VENCIMENTO) {
                    valorEvento = e.valor.abs()
                    diferenca = getValorMensal(e.codigo, valorEvento, eventosFolhaMensal)
                    totalLiquido += diferenca
                }
                if (e.tipo == TipoEvento.DESCONTO) {
                    valorEvento = e.valor.abs()
                    diferenca = getValorMensal(e.codigo, valorEvento, eventosFolhaMensal)
                    totalLiquido -= diferenca
                }
            }
        }
    } else {
        totalLiquido = folha.totalLiquido
    }
    if (totalLiquido < 0) {
        valorReferencia = 0
        valorCalculado = totalLiquido.abs()
    }
}
static def getValorMensal(long codigo, valor, eventos) {
    BigDecimal valorMensal = eventos.findAll{ m -> m.codigo == codigo }?.valor.sum()
    if (valorMensal == null) return valor
    if (valor > valorMensal) return (valor - valorMensal)
    return BigDecimal.valueOf(0)
}

