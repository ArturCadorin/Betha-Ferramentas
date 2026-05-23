Funcoes.somenteTransportadoresAutonomos()
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorCalculado = vaux * 0.80
} else {
    valorCalculado = autonomo.totalServicosAutonomo * 0.80
}
if (valorCalculado > 0) {
    valorCalculado = Numeros.trunca(valorCalculado, 2)
    Bases.compor(valorCalculado, Bases.INSS)
}

