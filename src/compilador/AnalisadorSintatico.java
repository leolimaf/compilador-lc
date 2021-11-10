package compilador;

import compilador.erros.AnaliseSemanticaException;
import compilador.erros.AnaliseSintaticaException;

import java.util.ArrayList;
import java.util.List;

public class AnalisadorSintatico {

    private AnalisadorLexico analisadorLexico;
    private RegistroLexico registroLexico, aux = new RegistroLexico();
    private final String msgErro = "Caracter inesperado na linha ";
    private List<RegistroLexico> identificadores = new ArrayList<>();

    public AnalisadorSintatico(AnalisadorLexico analisadorLexico) {
        this.analisadorLexico = analisadorLexico;
        S();
    }

    public void S() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        while (registroLexico != null
                && (registroLexico.getLexema().equals("int")
                || registroLexico.getLexema().equals("boolean")
                || registroLexico.getLexema().equals("byte")
                || registroLexico.getLexema().equals("string")
                || registroLexico.getLexema().equals("final"))) {
            D();
            registroLexico = analisadorLexico.obterProxRegistroLexico();
        }
        B();
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
        aux = analisadorLexico.obterProxRegistroLexico(true);
        if (identificadores.contains(aux)){
            throw new AnaliseSemanticaException("Identificador [" + aux.getLexema() + "] na linha " + aux.getLinha() + " já foi declarado");
        }
        aux.setClasse("classe_final");
        atribuicao();
        identificadores.add(aux);
        pontoeVirgula();
    }

    private void atribuicaoString() {
        if (registroLexico.getLexema().equals("string")){
            aux.setTipo("tipo_string");
        }
        VerificarIdentificadorJaDeclarado();
        if (registroLexico.getLexema().equals("=")) {
            operadorAtribuicao();
            constante();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getLexema().equals(",")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoString();
            } else if (registroLexico.getLexema().equals(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else if (registroLexico.getLexema().equals(",")) {
            virgula();
            atribuicaoNumerica();
        } else if (registroLexico.getLexema().equals(";")) {
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void constante() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getLexema().contains("\"")){
            aux.setTipo(registroLexico.getTipo());
        }
        if (registroLexico.getToken() == 1 || registroLexico.getToken() == 0) {
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getLexema().equals("+")
                    || registroLexico.getLexema().equals("-")
                    || registroLexico.getLexema().equals("*")
                    || registroLexico.getLexema().equals("/")
            ) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                constante();
            }
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void atribuicaoBoolean() {
        if (registroLexico.getLexema().equals("boolean")){
            aux.setTipo("tipo_boolean");
        }
        VerificarIdentificadorJaDeclarado();
        if (registroLexico.getLexema().equals("=")) {
            operadorAtribuicao();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == 0 || registroLexico.getToken() == 1) {
                expressaoRelacional();
            } else {
                verdadeiroOuFalso();
            }
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getLexema().equals(",")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoBoolean();
            } else if (registroLexico.getLexema().equals(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else if (registroLexico.getLexema().equals(",")) {
            virgula();
            atribuicaoBoolean();
        } else if (registroLexico.getLexema().equals(";")) {
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }

    }

    private void verdadeiroOuFalso() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("true") && !registroLexico.getLexema().equals("false")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        aux.setTipo(registroLexico.getTipo());
    }

    private void atribuicaoNumerica() {
        if (registroLexico.getLexema().equals("int")){
            aux.setTipo("tipo_int");
        } else if (registroLexico.getLexema().equals("byte")){
            aux.setTipo("tipo_byte");
        }
        VerificarIdentificadorJaDeclarado();
        if (registroLexico.getLexema().equals("=")) {
            operadorAtribuicao();
            constanteNumerica();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getLexema().equals(",")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoNumerica();
            } else if (registroLexico.getLexema().equals(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
            }
        } else if (registroLexico.getLexema().equals(",")) {
            virgula();
            atribuicaoNumerica();
        } else if (registroLexico.getLexema().equals(";")) {
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
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
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
    }

    private void identificador() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != 0) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        };
    }

    private void operadorAtribuicao() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("=")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void constanteNumerica() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != 1) {
            if (registroLexico.getLexema().equals("+") || registroLexico.getLexema().equals("-")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                if (registroLexico.getToken() != 1) {
                    throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
                }
            }
        }
        aux.setTipo(registroLexico.getTipo());
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() <= 24 && registroLexico.getToken() >= 21) {
            operadorAritmetico();
            constanteNumerica();
        }
    }

    private void operadorAritmetico() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("+")
                && !registroLexico.getLexema().equals("-")
                && !registroLexico.getLexema().equals("*")
                && !registroLexico.getLexema().equals("/")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void virgula() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals(",")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void pontoeVirgula() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals(";")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void B() {
        if (registroLexico != null && registroLexico.getLexema().equals("begin")) {
            while (registroLexico != null && !registroLexico.getLexema().equals("end")) { // TODO IDENTIFICAR BEGIN SEM END E VICE VERSA
                C();
                registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            }
        } else {
            throw new AnaliseSintaticaException("Esperava-se por bloco de comando");
        }
    }

    private void C() {
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null) {
            if (registroLexico.getLexema().equals("while")) {
                enquanto();
            } else if (registroLexico.getLexema().equals("if")) {
                se();
                registroLexico = analisadorLexico.obterProxRegistroLexico(true);
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
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
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
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("readln")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        virgula();
        identificador();
        pontoeVirgula();
    }

    private void escreva() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("write")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        virgula();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        listaDeExpressoes();
        pontoeVirgula();
    }

    private void escrevaln() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("writeln")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        virgula();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
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
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (!registroLexico.getLexema().equals(";")) {
            virgula();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            listaDeExpressoes();
        }
    }

    private void enquanto() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("while")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        expressaoRelacional();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null && !registroLexico.getLexema().equals("begin")) {
            C();
        } else {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            B();
            registroLexico = analisadorLexico.obterProxRegistroLexico();
        }
    }

    private void expressaoRelacional() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() == 0 || registroLexico.getToken() == 1) {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            if (registroLexico.getToken() >= 15 && registroLexico.getToken() <= 20) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                if (registroLexico.getToken() != 0
                        && registroLexico.getToken() != 1
                        && !registroLexico.getLexema().equals("true")
                        && !registroLexico.getLexema().equals("false")) {
                    throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
                }
                registroLexico = analisadorLexico.obterProxRegistroLexico(true);
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
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("and")
                && !registroLexico.getLexema().equals("or")
                && !registroLexico.getLexema().equals("not")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
    }

    private void se() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("if")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        expressaoRelacional();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null && !registroLexico.getLexema().equals("begin")) {
            C();
            pontoeVirgula();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico != null && registroLexico.getLexema().equals("else")) {
                seNao();
            }
        } else {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            B();
            registroLexico = analisadorLexico.obterProxRegistroLexico();
        }

    }

    private void seNao() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (!registroLexico.getLexema().equals("else")) {
            throw new AnaliseSintaticaException(msgErro + registroLexico.getLinha());
        }
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null && !registroLexico.getLexema().equals("begin")) {
            C();
        } else {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            B();
            registroLexico = analisadorLexico.obterProxRegistroLexico();
        }
    }

}

