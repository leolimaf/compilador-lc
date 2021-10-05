package compilador;

import compilador.erros.AnaliseLexicaException;
import compilador.erros.AnaliseSintaticaException;
import compilador.lexico.AnalisadorLexico;
import compilador.lexico.ES_Arquivo;
import compilador.sintatico.AnalisadorSintatico;

public class Compilador {

    public Compilador(String entrada, String saida) {
        try {
            AnalisadorLexico al = ES_Arquivo.abreArquivoEntrada(entrada);
            TabelaDeSimbolos ts = new TabelaDeSimbolos();
            AnalisadorSintatico as = new AnalisadorSintatico(al, ts);

            as.operacaoAritmetica();

        } catch (AnaliseLexicaException e){
            System.out.println("Erro Léxico: " + e.getMessage());
        } catch (AnaliseSintaticaException e){
            System.out.println("Erro Sintático: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
