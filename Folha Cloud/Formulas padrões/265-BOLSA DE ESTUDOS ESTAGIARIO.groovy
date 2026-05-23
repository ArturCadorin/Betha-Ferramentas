if (!TipoMatricula.ESTAGIARIO.equals(matricula.tipo)) {
    suspender "Este cálculo é executado apenas para estagiários"
}
if (!estagiario.codESocial.equals("901")) {
    suspender "Este cálculo é executado apenas para bolsa de estudos do estagiário com o 'Código eSocial' igual a '901' informado na categoria do trabalhador"
}
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def diastrab = Funcoes.diastrab()
    def horastrab = Funcoes.cnvdpbase(diastrab)
    def compoemes = Lancamentos.valorComposicaoMes()
    vaux = horastrab - compoemes
    if (vaux < 0) {
        vaux = 0
    }
    valorReferencia = vaux
}
def salario = estagiario.bolsaEstudos
if (salario <= 0) {
    suspender "Não há valor de bolsa de estudos para o estagiário na competência"
}
vaux = Funcoes.calcprop(salario, vaux)
valorCalculado = vaux.round(2)
if (valorCalculado > 0) {
    Bases.compor(valorReferencia, Bases.PAGAPROP)
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.IRRF,
            Bases.COMPHORAMES)
}

