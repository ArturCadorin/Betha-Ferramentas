if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento) && SubTipoProcessamento.COMPLEMENTAR.equals(calculo.subTipoProcessamento)) {
        def vvar = Lancamentos.valor(evento)
        if (vvar > 0) {
            valorReferencia = vvar
            valorCalculado = vvar
            Bases.compor(valorCalculado, Bases.IRRF)
        }
    }
}

