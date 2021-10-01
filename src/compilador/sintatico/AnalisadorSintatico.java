package compilador.sintatico;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.RegistroLexico;

public class AnalisadorSintatico {

    private AnalisadorLexico scanner;
    private RegistroLexico registroLexico;

    public AnalisadorSintatico(AnalisadorLexico scanner){
        this.scanner = scanner;
    }

}
