if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento) || TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
    suspender 'Este evento não é calculado em processamentos de férias e décimo terceiro'
}
double valorReembolso = ReembolsoDespesaPlanoSaude.buscaValor(TipoSegurado.AMBOS, TipoReembolso.AMBOS, TipoValorReembolso.AMBOS)
if (valorReembolso == 0) {
    suspender 'Não há valor de reembolso de depesas com plano de saúde a serem buscados para a matrícula com os parâmetros informados'
}
if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    double valorReembolsadoMensal = Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL) + Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.COMPLEMENTAR)
    if (valorReembolso <= valorReembolsadoMensal) {
        suspender 'O valor de reembolso de depesas com plano de saúde a ser calculado pelo evento já foi lançado em folha mensal anterior'
    } else {
        valorReembolso -= valorReembolsadoMensal
    }
}
valorCalculado = valorReembolso

