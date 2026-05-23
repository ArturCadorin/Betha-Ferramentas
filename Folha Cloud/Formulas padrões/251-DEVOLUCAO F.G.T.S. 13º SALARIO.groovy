Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'O funcionário não é optante de FGTS'
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'O funcionário não tem direito a receber décimo terceiro'
}
if (!TipoProcessamento.RESCISAO.equals(calculo.tipoProcessamento)) {
    suspender 'O evento deve ser calculado apenas em processamentos rescisórios'
}
double percentual
if (funcionario.categoriaSefipVinculo.toString() == 'MENOR_APRENDIZ') {
    percentual = 2
} else {
    if (evento.taxa <= 0) {
        suspender 'Para calcular este evento é necessário definir na configuração do mesmo uma taxa para o cálculo'
    }
    percentual = evento.taxa
}
double base = Bases.valor(Bases.FGTS) + Bases.valor(Bases.FGTS13)
if (base >= 0) {
    suspender 'A soma das bases de FGTS e FGTS 13º salário devem ser negativas para o cálculo da devolução'
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorCalculado = vaux
} else {
    double baseDevolucao = base * (-1)
    valorReferencia = baseDevolucao
    vaux = baseDevolucao * percentual / 100
    valorCalculado = Numeros.trunca(vaux,2)
}

