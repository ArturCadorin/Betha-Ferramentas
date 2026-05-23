Funcoes.somenteFuncionarios()
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorCalculado = vvar
} else {
    valorReferencia = evento.taxa
    valorCalculado = Funcoes.remuneracao(matricula.tipo).valor * evento.taxa / 100
}

