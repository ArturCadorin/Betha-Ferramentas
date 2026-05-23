Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'A matrícula não é optante de FGTS'
}
if (!Funcoes.recebeDecimoTerceiro()) {
    suspender 'O funcionário não tem direito a receber décimo terceiro'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    double base = Bases.valor(Bases.FGTS13AFASES)
    if (base == 0) {
        suspender 'Não há valor de base de F.G.T.S. 13º sobre afastamentos para complementar a base de cálculo do FGTS de 13º salário no eSocial. Por este motivo, este evento não será calculado'
    }
    valorCalculado = base
}

