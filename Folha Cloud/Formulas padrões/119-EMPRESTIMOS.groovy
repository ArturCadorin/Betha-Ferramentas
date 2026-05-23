def valorFerias = Funcoes.replicaEventoVariavel(evento.codigo)
if (valorFerias.valor > 0) {
    valorCalculado = valorFerias.valor
    valorReferencia = valorFerias.referencia
} else {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        double valorVariavel = vvar
        if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) || TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
            valorVariavel -= Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL)
            if (valorVariavel <= 0) {
                suspender 'O valor do evento de empréstimo lançado em variável já foi integralmente pago em processamento de férias nesta competência'
            }
        }
        valorCalculado = valorVariavel
    } else {
        if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && calculo.descontarEmprestimoRescisao) {
            valorCalculado = Funcoes.emprestimos()
        } else {
            valorCalculado = Funcoes.emprestimos(calculo.competencia)
        }
    }
}

