Funcoes.somenteFuncionarios()
def vaux = Funcoes.cnvdpbase(Funcoes.diastrab())
valorReferencia = vaux
valorCalculado = Funcoes.calcprop(funcionario.salario, vaux)
Bases.compor(valorReferencia, Bases.PAGAPROP)
Bases.compor(valorCalculado, Bases.IRRF)

