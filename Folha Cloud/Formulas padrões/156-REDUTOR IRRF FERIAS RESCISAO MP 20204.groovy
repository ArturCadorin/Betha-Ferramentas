Funcoes.somenteFuncionarios()
if (Datas.data(calculo.competencia.ano, calculo.competencia.mes, 1) < Datas.data(2004, 8, 1) ||
    Datas.data(calculo.competencia.ano, calculo.competencia.mes, 1) > Datas.data(2004, 12, 1)) {
    suspender "Este cálculo é executado apenas entre o período de 08/2004 à 12/2004"
}
if (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender "O evento deve ser calculado apenas em processamentos rescisórios"
}
valorCalculado = 100

