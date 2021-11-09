package compilador;

public class Compilador {

    public Compilador(String entrada, String saida) throws Exception{
            AnalisadorLexico al = ES_Arquivo.abreArquivoEntrada(entrada);
            AnalisadorSintatico as = new AnalisadorSintatico(al);
    }
}
