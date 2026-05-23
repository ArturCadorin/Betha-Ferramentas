if (!Funcoes.pagapensao()) {
    suspender 'O servidor não possui dependentes pensão ou não há pensões que estejam vigentes'
}
def valorFerias = Funcoes.replicaFeriasNaFolhaMensal(evento.codigo)
valorCalculado = valorFerias.valor
valorReferencia = valorFerias.referencia
if (valorFerias.valor <= 0) {
    if (folha.folhaPagamento) {
        if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento) && Eventos.valorCalculado(evento.codigo, TipoValor.CALCULADO, TipoProcessamento.FERIAS, SubTipoProcessamento.INTEGRAL, calculo.competencia) > 0) {
            suspender 'A pensão alimentícia a ser descontada na competência já foi calculada em processamento de férias'
        }
        valorReferencia = Funcoes.buscaQuantidadeDependentesPensao()
        def vvar = Lancamentos.valor(evento)
        if (vvar > 0) {
            valorCalculado = vvar
        } else {
            valorCalculado = Funcoes.buscaValorDependentesPensao(Funcoes.remuneracao(matricula.tipo).valor)
        }
        Bases.compor(valorCalculado, Bases.DEDUCIRRFMES)
    }
}

