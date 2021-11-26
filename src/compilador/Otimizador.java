package compilador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Otimizador {
    public static List<String> buffer;
    public static List<String> saida = new ArrayList<>();
    public BufferedWriter arquivo;
    private final String pasta = "C:\\Sources\\compilador-lc";

    public Otimizador() throws IOException {
        buffer = new ArrayList<>();
        if (!new File(pasta).exists()){
            new File(pasta).mkdir();
        }
        arquivo = new BufferedWriter(new FileWriter("C:\\Sources\\compilador-lc\\saida.asm"));
    }

    public void otimizar(){
        String[] tmp = buffer.get(0).split(" ", buffer.get(0).indexOf(';') > 0 ? buffer.get(0).indexOf(';') : buffer.get(0).length());
        for(String s : buffer){
            if(!s.equals(buffer.get(0))){
                String[] comando = s.split(" ", s.indexOf(';') > 0 ? s.indexOf(';') : s.length());
                if(tmp[0].equals("mov") && comando[0].equals("mov")){
                    if(!(tmp[1].contains(comando[2]) && tmp[2].contains(comando[1]))){
                        saida.add(s);
                    }
                }else if(tmp[0].equals("j") || tmp[0].equals("je") || tmp[0].equals("jg") || tmp[0].equals("jge") || tmp[0].equals("jl") || tmp[0].equals("jle") || tmp[0].equals("jmp") || tmp[0].equals("jne")){
                    if(!tmp[1].contains(comando[0])){
                        saida.add(s);
                    }
                }else{
                    saida.add(s);
                }
                tmp = comando;
            }else{
                saida.add(s);
            }
        }
    }

    public void InserirNoArquivo() throws IOException {
        for(String s : saida){
            arquivo.write(s);
            arquivo.newLine();
        }
        arquivo.close();
    }
}
