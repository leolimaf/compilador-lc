package compilador;

import java.util.Objects;

public class RegistroLexico {

    private int token;
    private String lexema;
    private String classe;
    private String tipo;
    private int linha;

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

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
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
