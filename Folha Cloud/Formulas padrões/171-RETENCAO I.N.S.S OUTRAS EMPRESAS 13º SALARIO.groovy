Funcoes.somenteFuncionarios()
if (!funcionario.possuiPrevidenciaFederal) {
    suspender "Este cálculo é realizado apenas para funcionários contribuintes da previdência federal"
}
def retencao = Lancamentos.valor(evento)
if (retencao <= 0) {
    boolean mesDemissao
    def dataRescisao = calculo.dataRescisao
    if (dataRescisao != null) {
        if (Datas.ano(dataRescisao) == Datas.ano(calculo.competencia) && Datas.mes(dataRescisao) == Datas.mes(calculo.competencia) && calculo.quantidadeDiasCompetencia >= Datas.dia(dataRescisao)) {
            mesDemissao = true
        }
    }
    if (mesDemissao) {
        retencao = BasesOutrasEmpresas.buscaPor(TipoProcessamento.DECIMO_TERCEIRO_SALARIO).sum(0, { it.valorRetidoInss })
    } else {
        retencao = BasesOutrasEmpresas.buscaPor(calculo.tipoProcessamento).sum(0, { it.valorRetidoInss })
    }
}
if (retencao <= 0) {
    suspender "Não há retenção de INSS de outras empresas 13º salário informada para a matrícula na competência para cálculo"
}
valorCalculado = retencao - Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

