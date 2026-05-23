Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
} else {
    def afasauxdoencemp = Funcoes.afasauxdoencemp()
    if (afasauxdoencemp <= 0) {
        suspender "Não há afastamento com a classificação 'Auxílio doença empregador' ou 'Aux doença relacionada trabalho típico empregador' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasauxdoencemp)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux) - Eventos.valor(263)
if (remuneracao > 0) {
    valorReferencia -= Eventos.valorReferencia(263)
    valorCalculado = remuneracao
    if (Eventos.valor(1) == 0 && Eventos.valor(270) == 0) {
        Bases.compor(funcionario.salario, Bases.HORAEXTRA, Bases.SIND)
    }
    Bases.compor(valorReferencia, Bases.PAGAPROP)
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.PERIC,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.COMPHORAMES,
            Bases.FUNDFIN)
}

