Funcoes.somenteFuncionarios()
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender 'O evento não deve ser calculado em processamentos de férias'
}
boolean possuiOcorrencia
if (funcionario.ocorrenciaSefip.toString().equals('EXPOSTO_APOSENTADORIA_15_ANOS')) {
    possuiOcorrencia = true
}
def vaux = Lancamentos.valor(evento)
if (vaux < 0 && !possuiOcorrencia) {
    suspender 'Na competência, não há lançamento de valor para a variável e o funcionário não possui a ocorrência SEFIP necessária para o cálculo'
}
valorReferencia = evento.taxa
def vcal = (EncargosSociais.salarioMinimo * valorReferencia) / 100
if (vcal > 0) {
    if (TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
        vcal *= Funcoes.avos13(12) / 12
        if (SubTipoProcessamento.ADIANTAMENTO.equals(calculo.subTipoProcessamento)) {
            vcal *= evento.getTaxa(26) / 100
        }
        valorCalculado = vcal
    } else {
        def base = 0
        if (Eventos.valor(195) > 0) {
            base = Bases.valor(Bases.PAGAPROP) - Bases.valor(Bases.MEDAUXMATPR)
        } else {
            base = Bases.valor(Bases.PAGAPROP)
        }
        if (base > 0) {
            Bases.compor(vcal, Bases.HORAEXTRA, Bases.SALAFAM)
        }
        valorCalculado = Funcoes.calcprop(vcal, base)
    }
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.MEDIAUXMAT)
}

