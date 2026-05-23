if (!TipoMatricula.FUNCIONARIO.equals(matricula.tipo) && !TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para funcionários e estagiários"
}
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorCalculado = vvar
} else {
    def irrfFerias = Funcoes.getIrrfFerias()
    valorReferencia = irrfFerias.referencia
    valorCalculado = irrfFerias.valor
}
if (valorCalculado > 0) {
    evento.replicado(true)
}

