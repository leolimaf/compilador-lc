package compilador.lexico;

public class RegistroLexico {

    public static final int TK_IDENTIFICADOR = 0;
    public static final int TK_CONSTANTE = 1;
    public static final int TK_OP_ATRIBUICAO = 2;
    public static final int TK_OP_RELACIONAL = 3;
    public static final int TK_OP_ARITIMETICO = 4;
    public static final int TK_PONTUACAO = 5;

    private int token;
    private String lexema;

    public RegistroLexico() {
    }

    public RegistroLexico(int token, String lexema) {
        this.token = token;
        this.lexema = lexema;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return "RegistroLexico{" +
                "token=" + token +
                ", lexema='" + lexema + '\'' +
                '}';
    }
}
