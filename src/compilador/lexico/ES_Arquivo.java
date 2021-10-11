package compilador.lexico;

import compilador.TabelaDeSimbolos;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ES_Arquivo {

    public static AnalisadorLexico abreArquivoEntrada(String nomeArqEntrada) throws Exception {
        String conteudo = Files.readString(Paths.get(nomeArqEntrada)) + ";";
        return new AnalisadorLexico(conteudo, new TabelaDeSimbolos());
    }

}
