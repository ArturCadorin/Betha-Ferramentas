Funcoes.somenteFuncionarios()
if (!Funcoes.avisoPrevioIndenizado()) {
    suspender 'Não há aviso prévio a ser indenizado ao funcionário'
}
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorReferencia = vvar
    valorCalculado = vvar
} else {
    def vint = mediaVantagem.calcular()
    if (vint <= 0) {
        suspender "Não há média percentual de aviso prévio a serem indenizadas ao funcionário"
    }
    valorCalculado = vint
}
Bases.compor(valorCalculado, Bases.FGTSAVISO)

