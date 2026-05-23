Funcoes.somenteFuncionarios()
if (!servidor.sexo.equals(Sexo.FEMININO)) {
    suspender "Este cálculo é realizado apenas para funcionários do sexo feminino"
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
} else {
    def diasaux = Funcoes.afasprorroglicmatlei11770()
    if (diasaux <= 0) {
        suspender "Não há afastamento com a classificação 'Licença maternidade - Prorrogação 60 dias, Lei 11.770/2008' na competência"
    }
    def cptiniauxmat = Funcoes.dtafast(ClassificacaoTipoAfastamento.PRORROGACAO_DA_LICENCA_MATERNIDADE_11_770, true)
    if (cptiniauxmat) {
        double vlrbase
        cptiniauxmat = Datas.data(Datas.ano(cptiniauxmat), Datas.mes(cptiniauxmat), 1)
        for (int i = 1; i <= 6; i++) {
            def cptaux = Datas.removeMeses(cptiniauxmat, i)
            vlrbase += Bases.valorCalculado(Bases.MEDIAUXMAT, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, cptaux) +
                    Bases.valorCalculado(Bases.MEDIAUXMAT, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR, cptaux)
        }
        if (vlrbase <= 0) {
            suspender "Não há valor de base de 'Média auxílio maternidade' para cálculo"
        }
        vaux = vlrbase / 6
        vaux = Funcoes.calcprop(vaux, Funcoes.cnvdpbase(diasaux))
    }
}
if (vaux > 0) {
    valorCalculado = Numeros.arredonda(vaux, 2)
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
            Bases.FUNDFIN)
}

