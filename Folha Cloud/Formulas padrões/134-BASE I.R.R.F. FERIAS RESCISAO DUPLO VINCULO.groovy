Funcoes.somenteFuncionarios()
if (TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    def vaux = Lancamentos.valor(evento)
    if (vaux > 0) {
        valorReferencia = vaux
        valorCalculado = vaux
    }
}

