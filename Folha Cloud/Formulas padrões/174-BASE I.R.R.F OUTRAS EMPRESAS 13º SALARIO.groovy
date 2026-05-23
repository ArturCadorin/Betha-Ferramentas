if (TipoMatricula.APOSENTADO.equals(matricula.tipo) || TipoMatricula.PENSIONISTA.equals(matricula.tipo)) {
    suspender "Este cálculo não é executado para aposentados e pensionistas"
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender "A matrícula não tem direito a receber décimo terceiro"
}
if (!Funcoes.permitecalc13integral()) {
    suspender "O período aquisitivo de décimo terceiro já foi quitado para a matrícula"
}
def base = Lancamentos.valor(evento)
if (base <= 0) {
    boolean mesDemissao
    def dataRescisao = calculo.dataRescisao
    if (dataRescisao != null) {
        if (Datas.ano(dataRescisao) == Datas.ano(calculo.competencia) && Datas.mes(dataRescisao) == Datas.mes(calculo.competencia) && calculo.quantidadeDiasCompetencia >= Datas.dia(dataRescisao)) {
            mesDemissao = true
        }
    }
    if (mesDemissao) {
        base = BasesOutrasEmpresas.buscaPor(TipoProcessamento.DECIMO_TERCEIRO_SALARIO).sum(0, { it.baseIrrf })
    } else {
        base = BasesOutrasEmpresas.buscaPor(calculo.tipoProcessamento).sum(0, { it.baseIrrf })
    }
}
if (base <= 0) {
    suspender "Não há valor de base de IRRF de outras empresas 13º salário informado para a matrícula na competência para cálculo"
}
valorCalculado = base
Bases.compor(valorCalculado, Bases.IRRFOUTRA13)

