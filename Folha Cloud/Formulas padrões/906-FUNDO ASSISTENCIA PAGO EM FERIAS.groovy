Funcoes.somenteFuncionarios()
if (funcionario.possuiPrevidencia(TipoPrevidencia.ASSISTENCIA_MUNICIPAL)) {
    def vvar = Lancamentos.valor(evento)
    if (vvar > 0) {
        valorCalculado = vvar
    } else {
        def assistMunicipalFerias = Funcoes.getAssistMunicipalFerias()
        valorReferencia = assistMunicipalFerias.referencia
        valorCalculado = assistMunicipalFerias.valor
    }
    if (valorCalculado > 0) {
        evento.replicado(true)
    }
}

