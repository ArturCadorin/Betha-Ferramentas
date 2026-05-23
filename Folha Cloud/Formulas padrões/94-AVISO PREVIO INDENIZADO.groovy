Funcoes.somenteFuncionarios()
if (!Funcoes.avisoPrevioIndenizado()) {
    suspender 'Não há aviso prévio a ser indenizado ao funcionário'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorReferencia = vvar
    valorCalculado = vvar
} else {
    def diasavisoinden = calculo.quantidadeDiasAvisoPrevio
    if (diasavisoinden > 0) {
        def diasM = 30
        if (UnidadePagamento.HORISTA.equals(funcionario.unidadePagamento) || UnidadePagamento.DIARISTA.equals(funcionario.unidadePagamento)) {
            diasM = calculo.quantidadeDiasCompetencia
        }
        valorCalculado = (funcionario.salario / diasM) * diasavisoinden
    } else {
        valorCalculado = funcionario.salario
    }
}
Bases.compor(valorCalculado, Bases.FGTSAVISO)

