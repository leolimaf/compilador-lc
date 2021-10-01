package compilador.main;

public class LC {

    public static void main(String[] args) {

        /* TODO PASSAR O NOME DOS ARQUIVOS A PARTIR DOS ARGS QUANDO O COMPILADOR FOR CHAMADO*/
        try {
            new Compilador("entrada.lc", "saida.asm");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}
