Funcoes.somenteFuncionarios()
if (funcionario.optanteFgts) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorCalculado = vvar
    } else {
        def fgtsFerias = Funcoes.getfgtsFerias()
        valorReferencia = fgtsFerias.referencia
        valorCalculado = fgtsFerias.valor
    }
    if (valorCalculado > 0) {
        evento.replicado(true)
    }
}

