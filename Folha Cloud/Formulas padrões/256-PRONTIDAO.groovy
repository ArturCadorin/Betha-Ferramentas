def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    def remuneracao = Funcoes.remuneracao(matricula.tipo)
    valorReferencia = vvar
    valorCalculado = valorReferencia * ((remuneracao.valor / remuneracao.quantidadeHorasMes))
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.FUNDFIN)
}

