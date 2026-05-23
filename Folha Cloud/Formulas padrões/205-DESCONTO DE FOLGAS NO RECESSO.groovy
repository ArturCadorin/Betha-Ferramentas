if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento) || TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
        def vvar = Lancamentos.valor(evento)
        if (vvar > 0) {
            valorReferencia = vvar
            valorCalculado = Funcoes.calcprop(estagiario.bolsaEstudos, vvar)
        }
    }
}

