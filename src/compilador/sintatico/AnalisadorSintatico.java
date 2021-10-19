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
 * EXP ->
 * EXPS ->
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
        identificador();
        operadorAtribuicao();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getLexema().startsWith("\"")) {
            constante();
        } else if (registroLexico.getToken() == 1
                || registroLexico.getToken() == 21
                || registroLexico.getToken() == 22) {
            constanteNumerica();
        } else if (registroLexico.getToken() == 32 || registroLexico.getToken() == 33) {
            verdadeiroOuFalso();
        } else {
            throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
        }
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
        if (registroLexico.getToken() != 1) {
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
    }

    private void virgula() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken(",")) {
            throw new AnaliseSintaticaException("Esperava-se por ponto e vírgula na linha " + registroLexico.getLinha());
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
            while (registroLexico != null && registroLexico.getToken() != TabelaDeSimbolos.obterToken("end")) {
                C();
                registroLexico = analisadorLexico.obterProxRegistroLexico();
            }
        } else {
            throw new AnaliseSintaticaException("Esperava-se por bloco de comando");
        }
    }

    private void C() {
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null) {
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("while")) {
                repeticao();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("if")) {
                condicicao(); // TODO IMPLEMENTAR 
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("write")) {
                write(); // TODO IMPLEMENTAR
            } else {
                throw new AnaliseSintaticaException("Esperava-se por comandos na linha " + registroLexico.getLinha());
            }
        } else {
            throw new AnaliseSintaticaException("Bloco de comando não foi finalizado");
        }
    }

    private void write() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("write")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada write na linha " + registroLexico.getLinha());
        }
    }

    private void repeticao() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("while")) {
            throw new AnaliseSintaticaException("Esperava-se por palavra reservada while na linha " + registroLexico.getLinha());
        }
        expressaoRelacional();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico != null && registroLexico.getToken() != TabelaDeSimbolos.obterToken("begin")) {
            C();
        } else {
            B();
        }
    }

    private void expressaoRelacional() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() == 0 || registroLexico.getToken() == 1) {
            registroLexico = analisadorLexico.obterProxRegistroLexico();
            if (registroLexico.getToken() >= 15 && registroLexico.getToken() <= 20) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                if (registroLexico.getToken() != 0 && registroLexico.getToken() != 1) {
                    throw new AnaliseSintaticaException("Esperava-se por constante ou identificador na linha " + registroLexico.getLinha());
                }
            } else {
                throw new AnaliseSintaticaException("Esperava-se por operador relacional na linha " + registroLexico.getLinha());
            }
        } else {
            throw new AnaliseSintaticaException("Esperava-se por constante ou identificador na linha " + registroLexico.getLinha());
        }
    }

    private void condicicao() {
    }

}

