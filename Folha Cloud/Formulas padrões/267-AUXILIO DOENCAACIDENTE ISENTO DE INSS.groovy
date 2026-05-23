def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    if (!calculo.isencaoInssPatronalAfastamento) {
        suspender "Este evento é calculado apenas para entidades que tem o parâmetro geral 'Isentar o INSS patronal nos afastamentos' ativo"
    }
    def dias = Afastamentos.buscaDiasAcidenteOuDoencaEmpregadorIsentosInss()
    if (dias <= 0) {
        suspender 'Este evento é calculado apenas em competências com afastamentos por auxílio doença/acidente de trabalho pagos pelo empregador e que originem um auxílio doença/acidente de trabalho pago pela previdência'
    } else {
        vaux = Funcoes.cnvdpbase(dias)
        valorReferencia = vaux
    }
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
    Bases.compor(valorReferencia, Bases.PAGAPROP)
    Bases.compor(valorCalculado, Bases.INSS)
}

