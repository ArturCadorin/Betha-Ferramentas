Funcoes.somenteFuncionarios()
if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
    suspender "O evento não deve ser calculado em processamentos de férias"
}
def vaux = Lancamentos.valor(evento)
if (vaux < 0) {
    double valorProporcional = (funcionario.salario * evento.taxa) / 100
    def funcaoGratificada = FuncoesGratificadas.busca(0) //inserir id da função
    vaux = Funcoes.calcprop(valorProporcional, Funcoes.cnvdpbase(funcaoGratificada))
}
valorReferencia = vaux
valorCalculado = vaux
Bases.compor(valorCalculado,
        Bases.SALBASE,
        Bases.FGTS,
        Bases.IRRF,
        Bases.INSS,
        Bases.FUNDOPREV,
        Bases.FUNDASS,
        Bases.PREVEST,
        Bases.SALAFAM,
        Bases.MEDIAUXMAT,
        Bases.FUNDFIN)

