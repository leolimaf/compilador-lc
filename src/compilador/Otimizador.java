package compilador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Otimizador {
    public static List<String> buffer;
    public static List<String> real = new ArrayList<>();
    public BufferedWriter arquivo;
    private final String pasta = "c:/ASM/";

    public Otimizador() throws IOException {
        buffer = new ArrayList<>();
        if (!new File(pasta).exists()){
            new File(pasta).mkdir();
        }
        arquivo = new BufferedWriter(new FileWriter("c:/ASM/saida.asm"));
    }
}
