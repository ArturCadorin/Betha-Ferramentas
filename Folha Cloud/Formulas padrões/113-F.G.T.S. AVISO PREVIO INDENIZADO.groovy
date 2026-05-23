Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}
if (!Funcoes.avisoPrevioIndenizado()) {
    suspender 'Não há aviso prévio a ser indenizado ao funcionário'
}
if (funcionario.categoriaSefipVinculo.toString() == 'MENOR_APRENDIZ') {
    valorReferencia = 2
} else {
    if (evento.taxa <= 0) {
        suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
    }
    valorReferencia = evento.taxa
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    vaux = Bases.valor(Bases.FGTSAVISO) * valorReferencia / 100
    valorCalculado = Numeros.trunca(vaux,2)
}

