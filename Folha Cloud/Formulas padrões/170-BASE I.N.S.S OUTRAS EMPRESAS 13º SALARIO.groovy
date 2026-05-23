Funcoes.somenteFuncionarios()
if (!funcionario.possuiPrevidenciaFederal) {
    suspender "Este cálculo é realizado apenas para funcionários contribuintes da previdência federal"
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
        base = BasesOutrasEmpresas.buscaPor(TipoProcessamento.DECIMO_TERCEIRO_SALARIO).sum(0, { it.baseInss })
    } else {
        base = BasesOutrasEmpresas.buscaPor(calculo.tipoProcessamento).sum(0, { it.baseInss })
    }
}
if (base <= 0) {
    suspender "Não há valor de base de INSS de outras empresas 13º salário informado para a matrícula na competência para cálculo"
}
valorCalculado = base
Bases.compor(valorCalculado, Bases.INSSOUTRA13)

