if (TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    if (TipoProcessamento.FERIAS.equals(calculo.tipoProcessamento)) {
        def vvar = Lancamentos.valor(evento)
        if (vvar > 0) {
            valorReferencia = vvar
            valorCalculado = Funcoes.calcprop(estagiario.bolsaEstudos, vvar)
            Bases.compor(valorCalculado,
                    Bases.IRRFFER,
                    Bases.COMPHORAMES)
        }
    }
}

