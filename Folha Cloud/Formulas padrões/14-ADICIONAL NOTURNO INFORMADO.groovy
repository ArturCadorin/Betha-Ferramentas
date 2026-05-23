Funcoes.somenteFuncionarios()
valorReferencia = Lancamentos.valor(evento)
if (valorReferencia > 0) {
    def base = funcionario.salario + Eventos.valor(11) + Eventos.valor(12) + Eventos.valor(13)
    valorCalculado = (base / funcionario.quantidadeHorasMes) * (evento.taxa / 100) * valorReferencia
    Bases.compor(valorCalculado,
            Bases.SALBASE,
            Bases.FGTS,
            Bases.IRRF,
            Bases.INSS,
            Bases.PREVEST,
            Bases.FUNDASS,
            Bases.FUNDOPREV,
            Bases.SALAFAM,
            Bases.MEDIAUXMAT)
}

