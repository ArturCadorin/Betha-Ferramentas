Funcoes.somenteFuncionarios()
if (!funcionario.possuiMultiploVinculo) {
    suspender "Este cálculo é realizado apenas para funcionários com múltiplos vínculos"
}
if (!funcionario.possuiPrevidenciaFederal) {
    suspender "Este cálculo é realizado apenas para funcionários contribuintes da previdência federal"
}
double abatinss = 0
def diassemdireito = Funcoes.afasacidtrab() + Funcoes.afasservmil()
if (diassemdireito <= 0) {
    suspender "Não há afastamento com a classificação 'Acidente de trabalho previdência' ou 'Serviço militar' na competência"
} else {
    abatinss = Numeros.arredonda(Funcoes.calcprop(funcionario.salario,Funcoes.cnvdpbase(diassemdireito)),2)
}
double baseprev = Bases.valor(Bases.INSS) + Bases.valor(Bases.INSSOUTRA) + Funcoes.buscaBaseDeOutrosProcessamentos(Bases.INSSOUTRA)
double base = baseprev - abatinss
if (base != 0 || (base == 0 && diassemdireito > 0 && Funcoes.diastrab() == 0)) {
    valorCalculado = abatinss
    Bases.compor(valorCalculado, Bases.ABATINSS)
}

