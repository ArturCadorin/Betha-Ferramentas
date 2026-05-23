Funcoes.somenteFuncionarios()
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento não deve ser calculado em processamentos de férias"
}
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorReferencia = vvar
    valorCalculado = vvar
} else {
    valorReferencia = evento.taxa
    valorCalculado = (Bases.valor(Bases.PERIC) - Eventos.valor(195) -
                      Bases.valor(Bases.MEDAUXMATPR)) * valorReferencia / 100
}
Bases.compor(valorCalculado,
        Bases.SALBASE,
        Bases.HORAEXTRA,
        Bases.FGTS,
        Bases.IRRF,
        Bases.INSS,
        Bases.PREVEST,
        Bases.FUNDASS,
        Bases.FUNDOPREV,
        Bases.SALAFAM,
        Bases.FUNDFIN)

