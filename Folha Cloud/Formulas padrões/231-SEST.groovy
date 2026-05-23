Funcoes.somenteTransportadoresAutonomos()
def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
    valorCalculado = vaux
} else {
    def base = Bases.valor(Bases.INSS)
    def vlrref = EncargosSociais.Geral.SESTAutonomo
    vaux = (base * vlrref) / 100
    valorReferencia = vlrref
    valorCalculado = Numeros.trunca(vaux, 2)
}

