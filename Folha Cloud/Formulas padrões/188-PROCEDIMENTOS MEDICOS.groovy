def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (valorFerias.valor <= 0) {
    if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento) || TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
        if (Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, calculo.competencia) > 0) {
            suspender 'O procedimento médico a ser descontado na competência já foi calculado em processamento de férias anterior'
        }
    }
    def vaux = Lancamentos.valor(evento)
    if (vaux >= 0) {
        valorCalculado = vaux
        valorReferencia = vaux
    } else {
        vaux = 0
        PlanoSaudeDespesas.buscaDespesasPlanoSaude(calculo.competencia).each { despesa ->
            if (despesa.tipo == TipoDespesaPlanoSaude.PROCEDIMENTOS) {
                vaux += despesa.valor
            }
        }
        valorCalculado = vaux
    }
}

