Funcoes.somenteFuncionarios()
def vaux = Lancamentos.valor(evento)
if (vaux >= 0) {
    valorReferencia = vaux
} else {
    def afaslicsvenc = Funcoes.afaslicsvenc()
    if (afaslicsvenc <= 0) {
        suspender "Não há afastamento com a classificação 'Licença (SEM vencimentos) - Servidor Público', 'Licença (NÃO remunerada)', 'Suspensão de pagamento de servidor público por não recadastramento', 'Qualificação - Afastamento por suspensão do contrato de acordo com o art. 17 da MP 1.116/2022' ou 'Qualificação - Afastamento por suspensão do contrato de acordo com o art. 19 da MP 1.116/2022' na competência"
    }
    vaux = Funcoes.cnvdpbase(afaslicsvenc)
    valorReferencia = vaux
}
double remuneracao = Funcoes.calcprop(funcionario.salario, vaux)
if (remuneracao > 0) {
    valorCalculado = remuneracao
}

