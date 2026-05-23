Funcoes.somenteFuncionarios()
def avisodesc = Funcoes.avisoPrevioDescontado()
if (!avisodesc) {
    suspender 'Não há aviso prévio a ser descontado para o funcionário'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
    valorReferencia = vvar
} else {
    valorReferencia = 0
    def diasavisodesc = calculo.quantidadeDiasAvisoPrevio
    if (diasavisodesc > 0) {
        valorReferencia = diasavisodesc
        int diasM = 30
        if (UnidadePagamento.HORISTA.equals(funcionario.unidadePagamento) || UnidadePagamento.DIARISTA.equals(funcionario.unidadePagamento)) {
            diasM = calculo.quantidadeDiasCompetencia
        }
        valorCalculado = (funcionario.salario / diasM) * diasavisodesc
    } else {
        valorCalculado = funcionario.salario
    }
}

