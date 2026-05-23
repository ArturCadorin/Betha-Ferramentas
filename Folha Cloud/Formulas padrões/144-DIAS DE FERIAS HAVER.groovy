Funcoes.somenteFuncionarios()
if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        def diastot
        if (UnidadePagamento.HORISTA.equals(funcionario.unidadePagamento) || UnidadePagamento.DIARISTA.equals(funcionario.unidadePagamento)) {
            diastot = calculo.quantidadeDiasCompetencia
        } else {
            diastot = 30
        }
        valorReferencia = vvar
        valorCalculado = funcionario.salario * valorReferencia / diastot
        Bases.compor(valorCalculado, Bases.IRRF)
    }
}

