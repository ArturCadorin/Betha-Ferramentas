def vvar = Lancamentos.valor(evento)
if (TipoMatricula.FUNCIONARIO.equals(matricula.tipo)) {
    if (vvar > 0) {
        valorReferencia = vvar
        valorCalculado = funcionario.salario / evento.taxa * valorReferencia
        Bases.compor(valorCalculado, Bases.IRRF)
    }
}

