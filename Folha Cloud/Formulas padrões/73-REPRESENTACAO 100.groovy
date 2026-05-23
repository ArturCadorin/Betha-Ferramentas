if (TipoMatricula.FUNCIONARIO.equals(matricula.tipo)) {
    def vaux = Funcoes.cnvdpbase(Funcoes.diastrab())
    if (vaux > 0) {
        valorReferencia = vaux
        valorCalculado = Funcoes.calcprop(funcionario.salario, valorReferencia)
        Bases.compor(valorCalculado, Bases.IRRF)
    }
}

