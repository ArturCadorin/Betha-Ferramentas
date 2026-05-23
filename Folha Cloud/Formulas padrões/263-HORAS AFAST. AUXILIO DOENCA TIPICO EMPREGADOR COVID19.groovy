Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
} else {
    def descricaoTipoAfastamento = 'Auxílio doença típico empregador - COVID19'
    def afastamentos = [ClassificacaoTipoAfastamento.AUXILIO_DOENCA_EMPREGADOR]
    def afastCovid19 = Funcoes.diasafastcalc30(calculo.competencia, afastamentos, descricaoTipoAfastamento)
    if (afastCovid19 <= 0) {
        suspender "Não há afastamento com a descrição 'Auxílio doença típico empregador - COVID19' e classificação 'Auxílio doença empregador' na competência"
    }
    vaux = Funcoes.cnvdpbase(afastCovid19)
    valorReferencia = vaux
}
double valorCovid = Funcoes.calcprop(funcionario.salario, vaux)
double max = EncargosSociais.RGPS.buscaMaior(1)
if (valorCovid > max) {
    valorCovid = max
}
if (valorCovid > 0) {
    valorCalculado = valorCovid
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

