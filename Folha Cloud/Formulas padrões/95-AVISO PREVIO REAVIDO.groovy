Funcoes.somenteFuncionarios()
def avisoinden = Funcoes.avisoPrevioIndenizado()
if (!avisoinden) {
    suspender 'Não há aviso prévio a ser indenizado ao funcionário'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
    valorReferencia = vvar
} else {
    valorReferencia = 0
    valorCalculado = funcionario.salario
}

