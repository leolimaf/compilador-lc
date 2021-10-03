package compilador;

import compilador.lexico.RegistroLexico;

import java.util.HashMap;
import java.util.Hashtable;

public class TabelaDeSimbolos {

    private final int TAM_MAX = 256;
    private HashMap<Integer, String> tabSimbolos;
    private Hashtable<Integer, RegistroLexico> hashtable;

    public TabelaDeSimbolos() {
        hashtable = new Hashtable();

        carregarTabelaDeSimbolos();
    }

    private void carregarTabelaDeSimbolos() {
        tabSimbolos = new HashMap();
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
        tabSimbolos.put(12, "begin");
        tabSimbolos.put(13, "end");
        tabSimbolos.put(14, "readln");
        tabSimbolos.put(15, "write");
        tabSimbolos.put(16, "writeln");
        tabSimbolos.put(17, "true");
        tabSimbolos.put(18, "false");
        tabSimbolos.put(19, "boolean");
    }
}
