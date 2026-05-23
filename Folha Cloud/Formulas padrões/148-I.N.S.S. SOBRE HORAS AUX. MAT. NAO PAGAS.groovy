Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidenciaFederal) {
    if (TipoProcessamento.MENSAL.equals(calculo.tipoProcessamento)) {
        def vvar = Lancamentos.valor(evento)
        if (vvar > 0) {
            valorReferencia = vvar
            valorCalculado = vvar
            Bases.compor(valorCalculado, Bases.IRRF)
        }
    }
}

