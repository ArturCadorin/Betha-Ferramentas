if (!TipoMatricula.AUTONOMO.equals(matricula.tipo)) {
    suspender 'Este cálculo é executado apenas para autônomos'
}
double valorISS = 0
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorISS = vaux
} else {
    if (autonomo.totalServicosAutonomo <= 0) {
        suspender 'Não há valor de serviços lançados para o autônomo na competência'
    }
    def competencia = Funcoes.inicioCompetencia().formatar('yyyy-MM')
    autonomo.buscaServicosAutonomos().each { servico ->
        if (servico.deduzIss && competencia == (servico.competencia.toString()).take(7)) {
            if (servico.valor > 0 && servico.aliquota > 0) {
                valorISS += (servico.valor * servico.aliquota) / 100
            }
        }
    }
    if (valorISS <= 0) {
        suspender 'Não há deduções ou alíquotas de ISS informadas para a competência'
    }
}
valorCalculado = valorISS

