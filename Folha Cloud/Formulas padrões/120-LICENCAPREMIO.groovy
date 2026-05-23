Funcoes.somenteFuncionarios()
def valorLancado = Lancamentos.valor(evento)
if (valorLancado >= 0) {
  valorReferencia = valorLancado
  valorCalculado = valorLancado
} else {
  def afastamentos = [ClassificacaoTipoAfastamento.LICENCA_COM_VENCIMENTOS]
  String descricaoTipoAfastamento = 'Licença prêmio'
  def diaslicenca = Funcoes.diasafastcalc30(calculo.competencia, afastamentos, descricaoTipoAfastamento)
  diaslicenca = Funcoes.cnvdpbase(diaslicenca)
  valorReferencia = diaslicenca
  valorCalculado = Funcoes.calcprop(funcionario.salario, valorReferencia)
}
if (valorCalculado > 0) {
  Bases.compor(valorCalculado, Bases.IRRF)
}

