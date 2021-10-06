//package compilador.sintatico;
//
//import compilador.TabelaDeSimbolos;
//import compilador.erros.AnaliseSintaticaException;
//import compilador.lexico.AnalisadorLexico;
//import compilador.lexico.RegistroLexico;
//
//public class AnalisadorSintatico {
//
//    private AnalisadorLexico analisadorLexico;
//    private TabelaDeSimbolos tabelaDeSimbolos; // TODO VERIFICAR SE PRECISO PASSAR TABELA DE SIMBOLOS PARA ESSA CLASSE
//    private RegistroLexico registroLexico;
//
//    public AnalisadorSintatico(AnalisadorLexico analisadorLexico) {
//        this.analisadorLexico = analisadorLexico;
//    }
//
//    // ================= ATRIBUIÇÃO ============================
//
//    public void atribuicao() {
//        identificador();
//        operadorAtribuicao();
//        operacaoAritmetica();
//    }
//
//    public void identificador() {
//        registroLexico = analisadorLexico.obterRegistroLexico();
//        if (registroLexico.getToken() != RegistroLexico.TK_IDENTIFICADOR) {
//            throw new AnaliseSintaticaException("Esperava-se identificador");
//        }
//    }
//
//    public void operadorAtribuicao() {
//        registroLexico = analisadorLexico.obterRegistroLexico();
//        if (registroLexico.getToken() != RegistroLexico.TK_OP_ATRIBUICAO) {
//            throw new AnaliseSintaticaException("Esperava-se operador de atribuição");
//        }
//    }
//
//    // ================= OPERAÇÃO ARITMETICA ===================
//
//    public void operacaoAritmetica() {
//        identificadorOuConstante();
//        _operacaoAtitmetica();
//    }
//
//    public void _operacaoAtitmetica() {
//        registroLexico = analisadorLexico.obterRegistroLexico();
//        if (registroLexico != null && !registroLexico.getLexema().equals(";")) {
//            operadorAritimético();
//            identificadorOuConstante();
//            _operacaoAtitmetica();
//        }
//    }
//
//    public void operadorAritimético() {
//        if (registroLexico.getToken() != RegistroLexico.TK_OP_ARITIMETICO) {
//            throw new AnaliseSintaticaException("Esperava-se operador aritmético");
//        }
//    }
//
//    public void identificadorOuConstante() {
//        registroLexico = analisadorLexico.obterRegistroLexico();
//        if (registroLexico.getToken() != RegistroLexico.TK_IDENTIFICADOR && registroLexico.getToken() != RegistroLexico.TK_CONSTANTE) {
//            throw new AnaliseSintaticaException("Esperava-se identificador ou constante");
//        }
//    }
//
//    // ================= ENCERRAR INSTRUÇÃO =================
//
//    // TODO DESCOBRIR COMO ENCERRAR A INSTRUÇÃO
//    public void pontoVirgula() {
//        if (registroLexico != null && !registroLexico.getLexema().equals(";")) {
//            throw new AnaliseSintaticaException("Esperava-se ponto e vírgula");
//        }
//    }
//}
