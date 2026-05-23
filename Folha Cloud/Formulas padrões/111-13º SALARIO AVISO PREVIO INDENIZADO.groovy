Funcoes.somenteFuncionarios()
if (!Funcoes.avisoPrevioIndenizado()) {
    suspender 'Não há aviso prévio a ser indenizado ao funcionário'
}
if (!Funcoes.permitecalc13integral()) {
    suspender 'O funcionário não tem direito a receber décimo terceiro ou o seu período aquisitivo contém uma situação não permitida para o cálculo do evento'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    valorCalculado = Funcoes.remuneracao(matricula.tipo).valor / 12
}
valorReferencia = 1
if (valorCalculado > 0) {
    def mesproc = Datas.mes(calculo.competencia)
    def avos13 = Funcoes.avos13(12)
    if (mesproc == 1 && avos13 <= 0) {
        Bases.compor(valorCalculado, Bases.IRRF13)
    } else {
        Bases.compor(valorCalculado, Bases.IRRF13, Bases.FGTS13)
    }
}

