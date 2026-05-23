def vaux = Lancamentos.valor(evento)
if (vaux > 0) {
    valorReferencia = vaux
} else {
    def cedidocomonus = Funcoes.cedidocomonus()
    if (cedidocomonus <= 0) {
        suspender "Não há afastamento relacionado a cessão COM ônus com a classificação 'Cedência', 'Mandato eletivo ocupando cargo em comissão', 'Mandato sindical' ou 'Exercício em outro órgão de servidor ou empregado público cedido' na competência"
    }
    vaux = Funcoes.cnvdpbase(cedidocomonus)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(Funcoes.remuneracao(matricula.tipo).valor, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
}

