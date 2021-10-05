package compilador;

import compilador.Compilador;

public class LC {

    public static void main(String[] args) {

        try {
            new Compilador(args[0], args[1]);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}
