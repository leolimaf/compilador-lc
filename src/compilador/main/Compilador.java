package compilador.main;

import compilador.erros.AnaliseLexicaException;
import compilador.erros.AnaliseSintaticaException;
import compilador.lexico.AnalisadorLexico;
import compilador.lexico.ES_Arquivo;
import compilador.lexico.RegistroLexico;

public class Compilador {

    public Compilador(String entrada, String saida) {
        try {
            AnalisadorLexico arq = ES_Arquivo.abreArquivoEntrada(entrada);
            RegistroLexico rl = null;
            do {
                rl = arq.obterRegistroLexico();
                if (rl != null) {
                    System.out.println(rl);
                }
            } while (rl != null);
        } catch (AnaliseLexicaException e){
            System.out.println("Erro Léxico: " + e.getMessage());
        } catch (AnaliseSintaticaException e){
            System.out.println("Erro Sintático: " + e.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
