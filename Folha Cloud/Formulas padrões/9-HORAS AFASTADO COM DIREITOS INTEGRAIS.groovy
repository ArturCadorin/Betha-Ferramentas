Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afasdirinteg = Funcoes.afasdirinteg()
    if (afasdirinteg <= 0) {
        suspender "Não há afastamento com a classificação 'Licença (COM vencimentos) - Servidor Público' ou 'Licença (COM vencimentos) - Liberdade da empresa, ACT' na competência"
    }
    vaux = Funcoes.cnvdpbase(afasdirinteg)
    valorReferencia = vaux - Eventos.valorReferencia(120)
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux) - Eventos.valor(120)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    Bases.compor(valorReferencia, Bases.PAGAPROP)
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.PERIC,
            Bases.SIND,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.COMPHORAMES,
            Bases.FUNDFIN)
}

