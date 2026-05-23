Funcoes.somenteFuncionarios()
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
if (valorFerias.valor > 0) {
    Bases.compor(valorFerias.valor,
            Bases.INSSFER,
            Bases.FUNDOPREVFER,
            Bases.FUNDASSFER,
            Bases.PREVESTFER,
            Bases.FUNDFINFER,
            Bases.FGTS)
}
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    if (periodoAquisitivo.pagouUmTercoIntegral) {
        suspender 'O valor de 1/3 das férias já foi pago integralmente em um cálculo proporcional de férias anterior deste período aquisitivo'
    }
    valorReferencia = evento.taxa
    if (folha.folhaPagamento) {
        valorCalculado = 0
        folhas.buscaFolhas().each { f ->
            f.eventos.each { e ->
                if (e.codigo == evento.codigo) {
                    valorCalculado += e.valor
                }
            }
        }
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado,
                    Bases.FGTS,
                    Bases.DESCTERFER,
                    Bases.IRRFFER,
                    Bases.INSSFER,
                    Bases.FUNDOPREVFER,
                    Bases.FUNDASSFER,
                    Bases.PREVESTFER,
                    Bases.FUNDFINFER,
                    Bases.MEDIAUXMAT)
        }
    } else {
        def vaux = Lancamentos.valor(evento)
        if (vaux >= 0) {
            valorCalculado = vaux
        } else {
            vaux = Eventos.valor(75) + Eventos.valor(80) + Eventos.valor(81) + Eventos.valor(82) + Eventos.valor(234)
            if (calculo.pagarUmTercoIntegral && !folha.calculoVirtual) {
                def diasferprop = folha.diasGozo
                if (diasferprop > 0) {
                    def diasdirfer = periodoAquisitivo.configuracaoFerias.diasParaAdquirirNoPeriodo
                    def diasgozo = periodoConcessao.diasGozo
                    def diasabono = periodoConcessao.diasAbono
                    //Integraliza o valor do 1/3 de férias conforme os dias de gozo da férias que está sendo calculada
                    vaux = (vaux * (diasgozo / diasferprop)) * (diasdirfer - diasabono) / diasgozo
                    //Proporcionaliza o valor integralizado pelos dias de gozo da férias que está sendo calculada na competência
                    vaux = vaux * diasferprop / diasgozo
                    vaux = Numeros.arredonda(vaux, 2)
                } else {
                    vaux = 0
                }
            }
            valorCalculado = vaux * evento.taxa / 100
        }
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado, Bases.MARGECONSIG)
        }
    }
}

