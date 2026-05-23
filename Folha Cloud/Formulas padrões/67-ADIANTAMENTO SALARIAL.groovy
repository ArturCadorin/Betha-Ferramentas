Funcoes.somenteFuncionarios()
def afas = Funcoes.afasacidtrab() + Funcoes.afasservmil() + Funcoes.afasauxdoenc() + Funcoes.afaslicsvenc()
if (afas > 0) {
    suspender "Não é possível calcular o adiantamento salarial para funcionários com afastamento por 'Acidente de trabalho previdência', 'Serviço militar', 'Auxílio doença previdência', 'Doença do trabalho previdência', 'Licença (SEM vencimentos) - Servidor Público', 'Licença (NÃO remunerada)' ou 'Suspensão de pagamento de servidor público por não recadastramento' na competência"
}
int tipo = 1 //1-Salário contratual, 2-Salário contratual + Adicionais
def vvar = Lancamentos.valor(evento)
if (vvar > 0) {
    valorCalculado = vvar
} else {
    BigDecimal salario = Funcoes.remuneracao(matricula.tipo).valor
    def valorCompetencia = 0
    def competencia = Datas.data(Datas.ano(calculo.competencia), Datas.mes(calculo.competencia), 1)
    if (tipo == 2) {
        def competenciaAnterior = Datas.removeMeses(competencia, 1)
        def eventos = [11, 12, 13, 16, 17, 18, 19, 20, 21]
        for (e in eventos) {
            valorCompetencia += Eventos.valorCalculado(e, TipoValor.CALCULADO, TipoProcessamento.MENSAL, SubTipoProcessamento.INTEGRAL, competenciaAnterior)
        }
    }
    salario += valorCompetencia - Funcoes.acumula(evento.codigo, TipoValor.CALCULADO, competencia, competencia, TipoProcessamento.MENSAL, SubTipoProcessamento.ADIANTAMENTO)
    if (salario > 0) {
        valorCalculado = salario * evento.taxa / 100
    }
}
if (valorCalculado > 0) {
    Bases.compor(valorCalculado, Bases.MARGECONSIG)
}

