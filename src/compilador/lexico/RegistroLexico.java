package compilador.lexico;

import java.util.Objects;

public class RegistroLexico {

    public static final String TOKENS_TEXT[] = {
            "identificador",
            "constante",
            "operador de atribuição",
            "operador relacional",
            "operador aritmético",
            "pontuação"
    };

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroLexico that = (RegistroLexico) o;
        return token == that.token && lexema.equals(that.lexema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, lexema);
    }
}
