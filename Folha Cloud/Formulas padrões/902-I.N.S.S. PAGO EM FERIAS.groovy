Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidenciaFederal) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorCalculado = vvar
    } else {
        def inssFerias = Funcoes.getInssFerias()
        valorReferencia = inssFerias.referencia
        valorCalculado = inssFerias.valor
    }
    if (valorCalculado > 0) {
        evento.replicado(true)
        Bases.compor(valorCalculado, Bases.MARGECONSIG)
    }
}

