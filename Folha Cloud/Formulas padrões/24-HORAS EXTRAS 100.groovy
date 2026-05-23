Funcoes.somenteFuncionarios()
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento não deve ser calculado em processamentos de férias"
}
valorReferencia = Lancamentos.valor(evento)
if (valorReferencia > 0) {
    valorCalculado = Bases.valor(Bases.HORAEXTRA) / funcionario.quantidadeHorasMes * valorReferencia * evento.taxa / 100
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.SALAFAM,
            Bases.MEDIAUXMAT,
            Bases.FUNDFIN)
}

