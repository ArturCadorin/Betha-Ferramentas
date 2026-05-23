Funcoes.somenteFuncionarios()
valorCalculado = 0
if (funcionario.possuiPrevidenciaFederal) {
    valorReferencia = Funcoes.mesesmat13()
    if (valorReferencia > 0) {
        valorCalculado = Bases.valor(Bases.INSS13) * valorReferencia / Funcoes.avos13(12)
        if (valorCalculado > 0) {
            Bases.compor(valorCalculado, Bases.DEDUCIRRF13, Bases.ABATIRRF13)
        }
    }
}
valorCalculado -= Funcoes.buscaValorEvento13SalarioIntegralAdiantado()

