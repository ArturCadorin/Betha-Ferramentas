Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.PREVIDENCIA_PROPRIA)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorCalculado = vvar
    } else {
        def prevPropriaFerias = Funcoes.getPrevPropriaFerias()
        valorReferencia = prevPropriaFerias.referencia
        valorCalculado = prevPropriaFerias.valor
    }
    if (valorCalculado > 0) {
        evento.replicado(true)
    }
}

