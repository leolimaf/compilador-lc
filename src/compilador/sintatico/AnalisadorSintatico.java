package compilador.sintatico;

import compilador.TabelaDeSimbolos;
import compilador.erros.AnaliseSintaticaException;
import compilador.lexico.AnalisadorLexico;
import compilador.lexico.RegistroLexico;

/*
 * GRAMÁTICA DA LINGUAGEM
 *
 * S -> {D}* B
 * D -> int id [=[+|-]const] {,id [=[+|-]const]}*;
 *    | string id [=const] {,id [=const]}*;
 *    | boolean id [=const] {,id [=const]}*;
 *    | final id = [+|-]const;
 *    | byte id [=[+|-]const] {,id [=[+|-]const]}*;
 * B -> begin {C}* end
 * C -> (write | writeln) {,EXP}+;
 *   | readln, id;
 *   | while EXP (C | B)
 *   | if EXP (C | B) [else (C | B)]
 * EXP -> identificador | constante | operações aritméticas, relacionais e lógicas
 * EXPS -> {EXP}+
 * T ->
 * F ->
 *
 * */

public class AnalisadorSintatico {

    private AnalisadorLexico analisadorLexico;
    private RegistroLexico registroLexico;

    public AnalisadorSintatico(AnalisadorLexico analisadorLexico) {
        this.analisadorLexico = analisadorLexico;
        S();
    }

    public void S() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        while (registroLexico != null
                && (registroLexico.getToken() == TabelaDeSimbolos.obterToken("int")
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("boolean")
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("byte")
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("string")
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("final"))) {
            D();
            registroLexico = analisadorLexico.obterProxRegistroLexico();
        }
        B();
    }

    private void D() {

        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("int")
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("byte")) {
            atribuicaoNumerica();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("boolean")) {
            atribuicaoBoolean();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("string")) {
            atribuicaoString();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("final")) {
            atribuicaoFinal();
        } else {
            throw new AnaliseSintaticaException("Esperava-se por um tipo de dado na linha " + registroLexico.getLinha());
        }
    }

    private void atribuicaoFinal() {
        atribuicao();
        pontoeVirgula();
    }

    private void atribuicaoString() {
        identificador();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("=")) {
            operadorAtribuicao();
            constante();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoString();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
            }
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")) {
            virgula();
            atribuicaoNumerica();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")) {
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
        }
    }

    private void constante() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() == 1 || registroLexico.getToken() == 0) {
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("+")
                    || registroLexico.getToken() == TabelaDeSimbolos.obterToken("-")
                    || registroLexico.getToken() == TabelaDeSimbolos.obterToken("*")
                    || registroLexico.getToken() == TabelaDeSimbolos.obterToken("/")
            ) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                constante();
            }
        } else {
            throw new AnaliseSintaticaException("Esperava-se por um valor constante na linha " + registroLexico.getLinha());
        }
    }

    private void atribuicaoBoolean() {
        identificador();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("=")) {
            operadorAtribuicao();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == 0 || registroLexico.getToken() == 1) {
                expressaoRelacional();
            } else {
                verdadeiroOuFalso();
            }
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoBoolean();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
            }
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")) {
            virgula();
            atribuicaoBoolean();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")) {
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
        }

    }

    private void verdadeiroOuFalso() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("true") && registroLexico.getToken() != TabelaDeSimbolos.obterToken("false")) {
            throw new AnaliseSintaticaException("Esperava-se por uma constante true ou false na linha " + registroLexico.getLinha());
        }
    }

    private void atribuicaoNumerica() {
        identificador();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("=")) {
            operadorAtribuicao();
            constanteNumerica();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoNumerica();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
            }
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")) {
            virgula();
            atribuicaoNumerica();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")) {
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
        }
    }

    private void identificador() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("id")) {
            throw new AnaliseSintaticaException("Esperava-se por um identificador na linha " + registroLexico.getLinha());
        }
    }

    private void operadorAtribuicao() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("=")) {
            throw new AnaliseSintaticaException("Esperava-se por um operador de atribuição na linha " + registroLexico.getLinha());
        }
    }

    private void constanteNumerica() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != 1) {
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("+") || registroLexico.getToken() == TabelaDeSimbolos.obterToken("-")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                if (registroLexico.getToken() != 1) {
                    throw new AnaliseSintaticaException("Esperava-se por um valor numérico na linha " + registroLexico.getLinha());
                }
            }
        }
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() <= 24 && registroLexico.getToken() >= 21) {
            operadorAritmetico();
            constanteNumerica();
        }
    }

    private void operadorAritmetico() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("+")
                && registroLexico.getToken() != TabelaDeSimbolos.obterToken("-")
                && registroLexico.getToken() != TabelaDeSimbolos.obterToken("*")
                && registroLexico.getToken() != TabelaDeSimbolos.obterToken("/")) {
            throw new AnaliseSintaticaException("Caracter inespereado na linha " + registroLexico.getLinha());
        }
    }

    private void virgula() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken(",")) {
            throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
        }
    }

    private void pontoeVirgula() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken(";")) {
            throw new AnaliseSintaticaException("Esperava-se por ponto e vírgula na linha " + registroLexico.getLinha());
        }
    }

    private void B() {
        if (registroLexico != null && registroLexico.getToken() == TabelaDeSimbolos.obterToken("begin")) {
            while (registroLexico != null && registroLexico.getToken() != TabelaDeSimbolos.obterToken("end")) { // TODO IDENTIFICAR BEGIN SEM END E VICE VERSA
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
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("while")) {
                enquanto();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("if")) {
                se();
                registroLexico = analisadorLexico.obterProxRegistroLexico(true);
                if (registroLexico != null && registroLexico.getToken() == TabelaDeSimbolos.obterToken("else")) {
                    seNao();
                }
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("readln")) {
                leia();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("write")) {
                escreva();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("writeln")) {
                escrevaln();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("id")) {
                atribuicao();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")) {
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
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
            pontoeVirgula();
        } else if (registroLexico.getToken() == 1
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("+")
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("-")) {
            constanteNumerica();
        } else if (registroLexico.getToken() == 32 || registroLexico.getToken() == 33) {
            verdadeiroOuFalso();
            pontoeVirgula();
        } else {
            throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
        }
    }

    private void leia() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("readln")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada readln na linha " + registroLexico.getLinha());
        }
        virgula();
        identificador();
        pontoeVirgula();
    }

    private void escreva() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("write")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada write na linha " + registroLexico.getLinha());
        }
        virgula();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        listaDeExpressoes();
        pontoeVirgula();
    }

    private void escrevaln() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("writeln")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada writeln na linha " + registroLexico.getLinha());
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
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("true") || registroLexico.getToken() == TabelaDeSimbolos.obterToken("false")) {
            verdadeiroOuFalso();
        } else {
            throw new AnaliseSintaticaException("Caracter inesperador na linha " + registroLexico.getLinha());
        }
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken(";")) {
            virgula();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            listaDeExpressoes();
        }
    }

    private void enquanto() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("while")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada while na linha " + registroLexico.getLinha());
        }
        expressaoRelacional();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null && registroLexico.getToken() != TabelaDeSimbolos.obterToken("begin")) {
            C();
        } else {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            B();
            registroLexico = analisadorLexico.obterProxRegistroLexico();
        }
    }

    private void expressaoRelacional() { // TODO: IDENTIFICAR AND E OR
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() == 0 || registroLexico.getToken() == 1) {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            if (registroLexico.getToken() >= 15 && registroLexico.getToken() <= 20) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                if (registroLexico.getToken() != 0
                        && registroLexico.getToken() != 1
                        && registroLexico.getToken() != TabelaDeSimbolos.obterToken("true")
                        && registroLexico.getToken() != TabelaDeSimbolos.obterToken("false")) {
                    throw new AnaliseSintaticaException("Esperava-se por constante ou identificador na linha " + registroLexico.getLinha());
                }
            } else {
                throw new AnaliseSintaticaException("Esperava-se por operador relacional na linha " + registroLexico.getLinha());
            }
        } else {
            throw new AnaliseSintaticaException("Esperava-se por constante ou identificador na linha " + registroLexico.getLinha());
        }
    }

    private void se() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("if")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada if na linha " + registroLexico.getLinha());
        }
        expressaoRelacional();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null && registroLexico.getToken() != TabelaDeSimbolos.obterToken("begin")) {
            C();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico != null && registroLexico.getToken() == TabelaDeSimbolos.obterToken("else")) {
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
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("else")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada else na linha " + registroLexico.getLinha());
        }
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null && registroLexico.getToken() != TabelaDeSimbolos.obterToken("begin")) {
            C();
        } else {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            B();
            registroLexico = analisadorLexico.obterProxRegistroLexico();
        }
    }

}

