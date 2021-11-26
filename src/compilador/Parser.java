package compilador;

import compilador.erros.AnaliseSemanticaException;
import compilador.erros.AnaliseSintaticaException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private AnalisadorLexico analisadorLexico;
    private RegistroLexico registroLexico, aux = new RegistroLexico();
    private final String msgErro = "Token inesperado na linha ";
    private List<RegistroLexico> identificadores = new ArrayList<>();
    private Otimizador o;
    private Memoria memoria;
    private int endereco = memoria.contador;

    int F_end = 0;
    int T_end = 0;

    public Parser(AnalisadorLexico analisadorLexico) throws IOException {
        this.analisadorLexico = analisadorLexico;
        o = new Otimizador();
        memoria = new Memoria();
        S();
    }

    public void S() throws IOException {
        registroLexico = recuperarRegistro();
        if (registroLexico != null){
            o.buffer.add("sseg SEGMENT STACK ;início seg. pilha");
            o.buffer.add("byte 4000h DUP(?) ;dimensiona pilha");
            o.buffer.add("sseg ENDS ;fim seg. pilha");
            o.buffer.add("dseg SEGMENT PUBLIC ;início seg. dados");
            o.buffer.add("byte 4000h DUP(?) ;temporários");
            endereco = memoria.alocarTemp();
        }
        while (registroLexico != null
                && (registroLexico.getLexema().equals("int")
                || registroLexico.getLexema().equals("boolean")
                || registroLexico.getLexema().equals("byte")
                || registroLexico.getLexema().equals("string")
                || registroLexico.getLexema().equals("final"))) {
            D();
            registroLexico = recuperarRegistro();
        }
        o.buffer.add("dseg ENDS ;fim seg. dados");
        o.buffer.add("cseg SEGMENT PUBLIC ;início seg. código");
        o.buffer.add("ASSUME CS:cseg, DS:dseg");
        o.buffer.add("strt:");
        o.buffer.add("mov ax, dseg");
        o.buffer.add("mov ds, ax");
        B();
        o.buffer.add("mov ah, 4Ch");
        o.buffer.add("int 21h");
        o.buffer.add("cseg ENDS ;fim seg. código");
        o.buffer.add("END strt ;fim programa");
        o.otimizar();
        o.InserirNoArquivo();
    }

    private void D() {

        if (registroLexico.getLexema().equals("int")
                || registroLexico.getLexema().equals("byte")) {
            atribuicaoNumerica();
        } else if (registroLexico.getLexema().equals("boolean")) {
            atribuicaoBoolean();
        } else if (registroLexico.getLexema().equals("string")) {
            atribuicaoString();
        } else if (registroLexico.getLexema().equals("final")) {
            atribuicaoFinal();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void atribuicaoFinal() {
        aux = recuperarRegistro(true);
        if (identificadores.contains(aux)){
            throw new AnaliseSemanticaException("Identificador [" + aux.getLexema() + "] na linha " + aux.getLinha() + " já foi declarado");
        }
        aux.setClasse("classe_final");
        registroLexico = recuperarRegistro(true);
        atribuicao();
        identificadores.add(aux);
        alocarComValor(aux);
        pontoeVirgula();
    }

    private void atribuicaoString() {
        String tipoAux = aux.getTipo();
        aux = recuperarRegistro(true);
        if (registroLexico.getLexema().equals("string")){
            aux.setTipo("tipo_string");
        } else {
            aux.setTipo(tipoAux);
        }
        VerificarIdentificadorJaDeclarado();
        if (registroLexico.getLexema().equals("=")) {
            operadorAtribuicao();
            registroLexico = recuperarRegistro(true);
            if (registroLexico.getTipo() != null && !registroLexico.getTipo().equals("tipo_string")){
                throw new AnaliseSemanticaException("Tipos incompatíveis na linha " + registroLexico.getLinha());
            }
            alocarComValor(aux);
            constante();
            registroLexico = recuperarRegistro(true);
            if (registroLexico.getLexema().equals(",")) {
                registroLexico = recuperarRegistro();
                atribuicaoString();
            } else if (registroLexico.getLexema().equals(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else if (registroLexico.getLexema().equals(",")) {
            alocar(aux);
            virgula();
            atribuicaoString();
        } else if (registroLexico.getLexema().equals(";")) {
            alocar(aux);
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void constante() {
        registroLexico = recuperarRegistro();
        if (registroLexico.getLexema().contains("\"")){
            aux.setTipo(registroLexico.getTipo());
        }
        else if (registroLexico.getToken() == 1 || registroLexico.getToken() == 0) {
            registroLexico = recuperarRegistro(true);
            if (registroLexico.getLexema().equals("+")
                    || registroLexico.getLexema().equals("-")
                    || registroLexico.getLexema().equals("*")
                    || registroLexico.getLexema().equals("/")
            ) {
                registroLexico = recuperarRegistro();
                constante();
            }
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void atribuicaoBoolean() {
        String tipoAux = aux.getTipo();
        aux = recuperarRegistro(true);
        if (registroLexico.getLexema().equals("boolean")){
            aux.setTipo("tipo_logico");
        } else {
            aux.setTipo(tipoAux);
        }
        VerificarIdentificadorJaDeclarado();
        if (registroLexico.getLexema().equals("=")) {
            operadorAtribuicao();
            registroLexico = recuperarRegistro(true);
            if (registroLexico.getToken() == 0 || registroLexico.getToken() == 1) {
                expressaoRelacional();
            } else {
                verdadeiroOuFalso();
            }
            alocarComValor(aux);
            registroLexico = recuperarRegistro(true);
            if (registroLexico.getLexema().equals(",")) {
                registroLexico = recuperarRegistro();
                atribuicaoBoolean();
            } else if (registroLexico.getLexema().equals(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else if (registroLexico.getLexema().equals(",")) {
            alocar(aux);
            virgula();
            atribuicaoBoolean();
        } else if (registroLexico.getLexema().equals(";")) {
            alocar(aux);
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }

    }

    private void verdadeiroOuFalso() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("true") && !registroLexico.getLexema().equals("false")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        aux.setTipo(registroLexico.getTipo());
    }

    private void atribuicaoNumerica() {
        String tipoAux = aux.getTipo();
        aux = recuperarRegistro(true);
        if (registroLexico.getLexema().equals("int")){
            aux.setTipo("tipo_inteiro");
        } else if (registroLexico.getLexema().equals("byte")){
            aux.setTipo("tipo_byte");
        } else {
            aux.setTipo(tipoAux);
        }
        VerificarIdentificadorJaDeclarado();
        if (registroLexico.getLexema().equals("=")) {
            operadorAtribuicao();
            registroLexico = recuperarRegistro(true);
            if (registroLexico.getTipo() != null
                    && !registroLexico.getTipo().equals("tipo_inteiro")
                    && !registroLexico.getTipo().equals("tipo_byte")
                    && !registroLexico.getLexema().equals("-")){
                throw new AnaliseSemanticaException("Tipos incompatíveis na linha " + registroLexico.getLinha());
            }
            if (registroLexico.getLexema().equals("-") && aux.getTipo().equals("tipo_inteiro")){
                registroLexico = recuperarRegistro();
                registroLexico = recuperarRegistro(true);
                o.buffer.add("sword " + registroLexico.getLexema() + "; valor negativo " + aux.getLexema());
                endereco = memoria.alocarInteiro();
            } else {
                alocarComValor(aux);
            }
            constanteNumerica();
            registroLexico = recuperarRegistro(true);
            if (registroLexico.getLexema().equals(",")) {
                registroLexico = recuperarRegistro();
                atribuicaoNumerica();
            } else if (registroLexico.getLexema().equals(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else if (registroLexico.getLexema().equals(",")) {
            alocar(aux);
            virgula();
            atribuicaoNumerica();
        } else if (registroLexico.getLexema().equals(";")) {
            alocar(aux);
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void alocar(RegistroLexico temp) {
        switch(temp.getTipo()){
            case "tipo_byte":
                endereco = memoria.alocarByte();
                o.buffer.add("byte ? ;byte " + temp.getLexema());
                break;
            case "tipo_logico":
                endereco = memoria.alocarLogico();
                o.buffer.add("byte ? ;logico " + temp.getLexema());
                break;
            case "tipo_inteiro":
                endereco = memoria.alocarInteiro();
                o.buffer.add("sword ? ;inteiro " + temp.getLexema());
                break;
            case "tipo_string":
                endereco = memoria.alocarString();
                o.buffer.add("byte 100h DUP(?) ;string " + temp.getLexema() + " em " + endereco);
                break;
        }
    }

    private void alocarComValor(RegistroLexico temp){
        switch(temp.getTipo()){
            case "tipo_byte":
                o.buffer.add("byte " + registroLexico.getLexema() + "; valor positivo " + temp.getLexema());
                endereco = memoria.alocarByte();
                break;
            case "tipo_logico":
                String lexTemp = registroLexico.getLexema().equals("true") ? "0FFh" : "0h";
                o.buffer.add("byte " + lexTemp + " ;logico " + temp.getLexema());
                endereco = memoria.alocarLogico();
                break;
            case "tipo_inteiro":
                o.buffer.add("sword " + registroLexico.getLexema() + "; valor positivo " + temp.getLexema());
                endereco = memoria.alocarInteiro();
                break;
            case "tipo_string":
                o.buffer.add("byte " + registroLexico.getLexema().substring(0, registroLexico.getLexema().length() - 1) + "$\"" +
                        "; string " + temp.getLexema() + " em 16394");
                endereco = memoria.alocarString(registroLexico.getLexema().length() - 1);
                break;
        }
    }

    private void VerificarIdentificadorJaDeclarado() {
        identificador();
        if (identificadores.contains(registroLexico)){
            throw new AnaliseSemanticaException("Identificador [" + registroLexico.getLexema() + "] na linha " + registroLexico.getLinha() + " já foi declarado");
        }
        identificadores.add(registroLexico);
        registroLexico.setClasse("classe_var");
        registroLexico.setTipo(aux.getTipo());
        registroLexico = recuperarRegistro(true);
    }

    private void identificador() {
        registroLexico = recuperarRegistro();
        if (registroLexico.getToken() != 0) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        };
    }

    private void operadorAtribuicao() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("=")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void constanteNumerica() {
        registroLexico = recuperarRegistro();
        if (registroLexico.getToken() != 1) {
            if (registroLexico.getLexema().equals("+") || registroLexico.getLexema().equals("-")) {
                registroLexico = recuperarRegistro();
                if (registroLexico.getToken() != 1) {
                    throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
                }
            }
        }
        aux.setTipo(registroLexico.getTipo());
    }

    private void operadorAritmetico() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("+")
                && !registroLexico.getLexema().equals("-")
                && !registroLexico.getLexema().equals("*")
                && !registroLexico.getLexema().equals("/")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void virgula() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals(",")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void pontoeVirgula() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals(";")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void B() {
        if (registroLexico != null && registroLexico.getLexema().equals("begin")) {
            while (registroLexico != null && !registroLexico.getLexema().equals("end")) { // TODO IDENTIFICAR BEGIN SEM END E VICE VERSA
                C();
                registroLexico = recuperarRegistro(true);
            }
        } else {
            throw new AnaliseSintaticaException("Esperava-se por bloco de comando");
        }
    }

    private void C() {
        registroLexico = recuperarRegistro(true);
        if (registroLexico != null) {
            if (registroLexico.getLexema().equals("while")) {
                enquanto();
            } else if (registroLexico.getLexema().equals("if")) {
                se();
                registroLexico = recuperarRegistro(true);
                if (registroLexico != null && registroLexico.getLexema().equals("else")) {
                    seNao();
                }
            } else if (registroLexico.getLexema().equals("readln")) {
                leia();
            } else if (registroLexico.getLexema().equals("write")) {
                escreva();
            } else if (registroLexico.getLexema().equals("writeln")) {
                escrevaln();
            } else if (registroLexico.getToken() == 0) {
                atribuicao();
            } else if (registroLexico.getLexema().equals(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else {
            throw new AnaliseSintaticaException("Bloco de comando não foi finalizado");
        }
    }

    private void atribuicao() {
        identificador();
        operadorAtribuicao();
        registroLexico = recuperarRegistro(true);
        if (registroLexico.getLexema().startsWith("\"") || registroLexico.getToken() == 0) {
            constante();
        } else if (registroLexico.getToken() == 1
                || registroLexico.getLexema().equals("+")
                || registroLexico.getLexema().equals("-")) {
            constanteNumerica();
        } else if (registroLexico.getToken() == 32 || registroLexico.getToken() == 33) {
            verdadeiroOuFalso();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void leia() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("readln")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        virgula();
        identificador();
        pontoeVirgula();
    }

    private void escreva() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("write")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        virgula();
        registroLexico = recuperarRegistro(true);
        listaDeExpressoes();
        pontoeVirgula();
    }

    private void escrevaln() {
        int stringEnd = memoria.novoTemp();
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("writeln")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        virgula();
        registroLexico = recuperarRegistro(true);
        if(registroLexico.getTipo().equals("tipo_string")){
            o.buffer.add("dseg SEGMENT PUBLIC");
            o.buffer.add("byte " + registroLexico.getLexema().substring(0, registroLexico.getLexema().length() - 1) + "$\"");
            o.buffer.add("dseg ENDS");
            F_end = memoria.contador;
            memoria.alocarString(registroLexico.getLexema().length() - 1);
            o.buffer.add("; " + registroLexico.getLexema() + " em " + F_end);
            o.buffer.add("mov dx, " + stringEnd);
            o.buffer.add("mov ah, 09h");
            o.buffer.add("int 21h");
            o.buffer.add("mov ah, 02h");
            o.buffer.add("mov dl, 0Dh");
            o.buffer.add("int 21h");
            o.buffer.add("mov DL, 0Ah");
            o.buffer.add("int 21h");
        }
        listaDeExpressoes();
        pontoeVirgula();
    }

    private void listaDeExpressoes() {
        if (registroLexico.getToken() == 0) {
            identificador();
        } else if (registroLexico.getToken() == 1) {
            constante();
        } else if (registroLexico.getLexema().equals("true") || registroLexico.getLexema().equals("false")) {
            verdadeiroOuFalso();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        registroLexico = recuperarRegistro(true);
        if (!registroLexico.getLexema().equals(";")) {
            virgula();
            registroLexico = recuperarRegistro(true);
            listaDeExpressoes();
        }
    }

    private void enquanto() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("while")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        expressaoRelacional();
        registroLexico = recuperarRegistro(true);
        if (registroLexico != null && !registroLexico.getLexema().equals("begin")) {
            C();
        } else {
            registroLexico = recuperarRegistro();
            B();
            registroLexico = recuperarRegistro();
        }
    }

    private void expressaoRelacional() {
        registroLexico = recuperarRegistro();
        if (registroLexico.getTipo() != null && registroLexico.getTipo().equals("tipo_string")){
            throw new AnaliseSemanticaException("Tipos incompatíveis na linha " + registroLexico.getLinha());
        }
        if (registroLexico.getToken() == 0 || registroLexico.getToken() == 1) {
            registroLexico = recuperarRegistro();
            if (registroLexico.getToken() >= 15 && registroLexico.getToken() <= 20) {
                registroLexico = recuperarRegistro();
                if (registroLexico.getToken() != 0
                        && registroLexico.getToken() != 1
                        && !registroLexico.getLexema().equals("true")
                        && !registroLexico.getLexema().equals("false")) {
                    throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
                }
                registroLexico = recuperarRegistro(true);
                if (registroLexico.getLexema().equals("and")
                        || registroLexico.getLexema().equals("or")
                        || registroLexico.getLexema().equals("not")) {
                    operadorLogico();
                    expressaoRelacional();
                }
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void operadorLogico() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("and")
                && !registroLexico.getLexema().equals("or")
                && !registroLexico.getLexema().equals("not")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void se() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("if")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        expressaoRelacional();
        registroLexico = recuperarRegistro(true);
        if (registroLexico != null && !registroLexico.getLexema().equals("begin")) {
            C();
            pontoeVirgula();
            registroLexico = recuperarRegistro(true);
            if (registroLexico != null && registroLexico.getLexema().equals("else")) {
                seNao();
            }
        } else {
            registroLexico = recuperarRegistro();
            B();
            registroLexico = recuperarRegistro();
        }

    }

    private void seNao() {
        registroLexico = recuperarRegistro();
        if (!registroLexico.getLexema().equals("else")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        registroLexico = recuperarRegistro(true);
        if (registroLexico != null && !registroLexico.getLexema().equals("begin")) {
            C();
        } else {
            registroLexico = recuperarRegistro();
            B();
            registroLexico = recuperarRegistro();
        }
    }

    private RegistroLexico recuperarRegistro(){
        return recuperarRegistro(false);
    }

    private RegistroLexico recuperarRegistro(boolean lookahead){
        RegistroLexico rl = analisadorLexico.obterProxRegistroLexico(lookahead);
        if (identificadores.contains(rl)){
            return identificadores
                    .stream()
                    .filter(r -> r.getLexema().equals(rl.getLexema()))
                    .peek(r -> r.setLinha(rl.getLinha()))
                    .findFirst()
                    .get();
        }
        return rl;
    }

}

