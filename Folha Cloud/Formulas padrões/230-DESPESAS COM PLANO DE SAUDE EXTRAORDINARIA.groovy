def valorFerias = Funcoes.replicaEventoVariavel(evento.codigo)
if (valorFerias.valor > 0) {
    valorCalculado = valorFerias.valor
    valorReferencia = valorFerias.referencia
} else {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        double valorVariavel
        if (!TipoProcessamento.DECIMO_TERCEIRO_SALARIO.equals(calculo.tipoProcessamento)) {
            valorVariavel = vvar - Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            if (valorVariavel <= 0) {
                suspender 'O valor do evento de despesas com plano de saúde (extraordinária) lançado em variável já foi integralmente pago em processamento de férias nesta competência'
            }
        }
        valorCalculado = valorVariavel
    }
}

