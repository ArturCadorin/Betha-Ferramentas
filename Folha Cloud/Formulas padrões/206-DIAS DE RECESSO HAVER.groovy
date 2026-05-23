if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
        def vvar = Lancamentos.valor(evento)
        if (vvar > 0) {
            def diastot
            if (UnidadePagamento.HORISTA.equals(estagiario.unidadePagamento) || UnidadePagamento.DIARISTA.equals(estagiario.unidadePagamento)) {
                diastot = calculo.quantidadeDiasCompetencia
            } else {
                diastot = 30
            }
            valorReferencia = vvar
            valorCalculado = estagiario.bolsaEstudos * valorReferencia / diastot
            Bases.compor(valorCalculado, Bases.IRRF)
        }
    }
}

