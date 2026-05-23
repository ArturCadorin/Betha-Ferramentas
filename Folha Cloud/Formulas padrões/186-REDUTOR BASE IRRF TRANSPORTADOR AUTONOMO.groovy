Funcoes.somenteTransportadoresAutonomos()
double aliquota
if (autonomo.codESocial.equals('712')) {
    if (Datas.data(calculo.competencia.ano, calculo.competencia.mes, 1) >= Datas.data(2013, 01, 1)) {
        aliquota = 0.90
    } else {
        aliquota = 0.60
    }
} else {
    aliquota = 0.40
}
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorCalculado = vaux * aliquota
} else {
    valorCalculado = autonomo.totalServicosAutonomo * aliquota
}
if (valorCalculado > 0) {
    valorCalculado = Numeros.trunca(valorCalculado, 2)
    Bases.compor(valorCalculado, Bases.DEDUCIRRFMES)
}

