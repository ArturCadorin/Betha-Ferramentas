Funcoes.somenteFuncionarios()
if (!Funcoes.optanteFgts(matricula.tipo)) {
    suspender 'A matrícula não é optante de FGTS'
}
def vvar = Lancamentos.valor(evento)
if (vvar >= 0) {
    valorCalculado = vvar
} else {
    double base = Bases.valor(Bases.FGTSAFASES)
    if (base == 0) {
        suspender 'Não há valor de base de F.G.T.S. sobre afastamentos para complementar a base de cálculo do FGTS no eSocial. Por este motivo, este evento não será calculado'
    }
    valorCalculado = base
}

