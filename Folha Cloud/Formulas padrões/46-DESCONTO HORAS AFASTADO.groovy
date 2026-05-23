Funcoes.somenteFuncionarios()
if (!funcionario.possuiPrevidenciaFederal) {
    valorReferencia = Eventos.valorReferencia(7)
    valorCalculado = Eventos.valor(7)
} else {
    valorReferencia = Eventos.valorReferencia(4) + Eventos.valorReferencia(5) + Eventos.valorReferencia(7) + Eventos.valorReferencia(8) + Eventos.valorReferencia(320)
    valorCalculado = Eventos.valor(4) + Eventos.valor(5) + Eventos.valor(7) + Eventos.valor(8) + Eventos.valor(320)
    if (Sexo.MASCULINO.equals(servidor.sexo)) {
        valorReferencia += Eventos.valorReferencia(3) + Eventos.valorReferencia(163) + Eventos.valorReferencia(164) + Eventos.valorReferencia(165) + Eventos.valorReferencia(199)
        valorCalculado += Eventos.valor(3) + Eventos.valor(163) + Eventos.valor(164) + Eventos.valor(165) + Eventos.valor(199)
    }
}
valorReferencia += Eventos.valorReferencia(283)
valorCalculado += Eventos.valor(283)

