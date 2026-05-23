Funcoes.somenteFuncionarios()
def avisoinden = Funcoes.avisoPrevioIndenizado()
if (!avisoinden) {
    suspender 'Não há aviso prévio a ser indenizado ao funcionário'
}
def vvar = Lancamentos.valor(evento)
valorReferencia = 1
if (vvar > 0) {
    valorCalculado = vvar
} else {
    def mesesaquis
    double vlorfer
    def feriasprop = PeriodosAquisitivos.buscaProporcional()
    if (feriasprop) {
        def diasferias = feriasprop.saldo
        def diasdirfer = feriasprop.configuracaoFerias.diasParaAdquirirNoPeriodo
        mesesaquis = feriasprop.configuracaoFerias.mesesParaAquisicao
        vlorfer = Eventos.valor(96) + Eventos.valor(102) + Eventos.valor(103) + Eventos.valor(104)
        valorCalculado = vlorfer / diasferias * (diasdirfer / mesesaquis)
    } else {
        def vencidos = PeriodosAquisitivos.buscaVencidos()
        if (vencidos.size() > 0) {
            int quant
            vencidos.each { feriasvenc ->
                quant++
                mesesaquis = feriasvenc.configuracaoFerias.mesesParaAquisicao
            }
            if (quant > 0) {
                def nofer = quant
                if (nofer > 5) {
                    nofer = 5
                }
                vlorfer = Eventos.valor(97) + Eventos.valor(105) + Eventos.valor(106) + Eventos.valor(107)
                valorCalculado = vlorfer / nofer / mesesaquis
            }
        } else {
            valorCalculado = 0
            valorReferencia = 0
        }
    }
}

