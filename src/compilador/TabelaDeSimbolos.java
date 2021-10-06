package compilador;

import compilador.lexico.RegistroLexico;

import java.util.Hashtable;
import java.util.Map;

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
        tabSimbolos.put(12, "=");
        tabSimbolos.put(13, "(");
        tabSimbolos.put(14, ")");
        tabSimbolos.put(15, "==");
        tabSimbolos.put(16, "<");
        tabSimbolos.put(17, ">");
        tabSimbolos.put(18, "<>");
        tabSimbolos.put(19, ">=");
        tabSimbolos.put(20, "<=");
        tabSimbolos.put(21, "+");
        tabSimbolos.put(22, "-");
        tabSimbolos.put(23, "*");
        tabSimbolos.put(24, "/");
        tabSimbolos.put(25, ",");
        tabSimbolos.put(26, ";");
        tabSimbolos.put(27, "begin");
        tabSimbolos.put(28, "end");
        tabSimbolos.put(29, "readln");
        tabSimbolos.put(30, "write");
        tabSimbolos.put(31, "writeln");
        tabSimbolos.put(32, "true");
        tabSimbolos.put(33, "false");
        tabSimbolos.put(34, "boolean");
    }

    public void insereSimbolo(){

    }

    public boolean contemSimbolo(String lexema) {
        return tabSimbolos.containsValue(lexema);
    }

    public int obterToken(String lexema){
        int key = -1;
        for (Map.Entry entry: tabSimbolos.entrySet()) {
            if(lexema.equals(entry.getValue())){
                key = (int) entry.getKey();
                break;
            }
        }
        return key;
    }

}
