Funcoes.somenteFuncionarios()
if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorReferencia = vvar
        valorCalculado = Funcoes.calcprop(funcionario.salario,vvar)
    }
}

