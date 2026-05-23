def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
} else {
    def cedidosemonus = Funcoes.cedidosemonus()
    if (cedidosemonus <= 0) {
        suspender "Não há afastamento relacionado a cessão SEM ônus com a classificação 'Cedência', 'Mandato eletivo ocupando cargo em comissão' ou 'Mandato sindical' na competência"
    }
    vaux = Funcoes.cnvdpbase(cedidosemonus)
    valorReferencia = vaux
}
double salario = funcionario.salario
if (salario <= 0) {
    suspender "Não há valor de salário para o funcionário na competência"
}
double remuneracao = Funcoes.calcprop(salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    if (Eventos.valor(1) == 0 && Eventos.valor(270) == 0) {
        Bases.compor(salario, Bases.HORAEXTRA, Bases.SIND)
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

