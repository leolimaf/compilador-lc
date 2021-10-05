package compilador;

import compilador.lexico.RegistroLexico;

import java.util.Hashtable;

public class TabelaDeSimbolos {

    private final int TAM_MAX = 256;
    private Hashtable<Integer, String> tabSimbolos;

    public TabelaDeSimbolos() {
        tabSimbolos = new Hashtable();

        carregarTabelaDeSimbolos();
    }

    private void carregarTabelaDeSimbolos() {
        tabSimbolos.put(0, "id");
        tabSimbolos.put(1, "const");
        tabSimbolos.put(2, "final");
        tabSimbolos.put(3, "int");
        tabSimbolos.put(4, "byte");
        tabSimbolos.put(5, "string");
        tabSimbolos.put(6, "while");
        tabSimbolos.put(7, "if");
        tabSimbolos.put(8, "else");
        tabSimbolos.put(9, "and");
        tabSimbolos.put(10, "or");
        tabSimbolos.put(11, "not");
        tabSimbolos.put(12, ";");
        tabSimbolos.put(13, "begin");
        tabSimbolos.put(14, "end");
        tabSimbolos.put(15, "readln");
        tabSimbolos.put(16, "write");
        tabSimbolos.put(17, "writeln");
        tabSimbolos.put(18, "true");
        tabSimbolos.put(19, "false");
        tabSimbolos.put(20, "boolean");
    }

    public void insereSimbolo(){

    }

    public boolean pesquisaSimbolo(int num) {
        return tabSimbolos.contains(num);
    }


}