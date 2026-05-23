Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.PREVIDENCIA_ESTADUAL)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorCalculado = vvar
    } else {
        def prevEstadualFerias = Funcoes.getPrevEstadualFerias()
        valorReferencia = prevEstadualFerias.referencia
        valorCalculado = prevEstadualFerias.valor
    }
    if (valorCalculado > 0) {
        evento.replicado(true)
    }
}

