package compilador.main;

import compilador.erros.LexicoException;
import compilador.lexico.ES_Arquivo;
import compilador.lexico.RegistroLexico;

public class LC {

    public static void main(String[] args) {

        try {
            ES_Arquivo arq = new ES_Arquivo("entrada.lc");
            RegistroLexico rl = null;
            do {
                rl = arq.obterRegistroLexico();
                if (rl != null) {
                    System.out.println(rl);
                }
            } while (rl != null);
        } catch (LexicoException e){
            System.out.println("Erro léxico: " + e.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
