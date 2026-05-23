Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
} else {
    def afasacidtrabemp = Funcoes.afasacidtrabemp()
    if (afasacidtrabemp <= 0) {
        suspender "Não há afastamento com a classificação 'Acidente de trabalho empregador' ou 'Acidente de trajeto empregador' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasacidtrabemp)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
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

