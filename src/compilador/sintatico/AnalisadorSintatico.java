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
                || registroLexico.getToken() == TabelaDeSimbolos.obterToken("final"))){
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
            atribuicaoFinal(); // TODO FINALIZAR
        } else {
            throw new AnaliseSintaticaException("Esperava-se por um tipo de dado na linha " + registroLexico.getLinha());
        }
    }

    private void atribuicaoFinal() {
        identificador();
        operadorAtribuicao();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getLexema().contains("\"")){
            constanteString();
        } else if (registroLexico.getToken() == 1
                || registroLexico.getToken() == 21
                || registroLexico.getToken() == 22){
            constanteNumerica();
        } else if (registroLexico.getToken() == 32 || registroLexico.getToken() == 33){
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
            constanteString();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoString();
            } else if(registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
            }
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
            virgula();
            atribuicaoNumerica();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
            pontoeVirgula();
        } else{
            throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
        }
    }

    private void constanteString() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != 1) {
            throw new AnaliseSintaticaException("Esperava-se por uma constante de string na linha " + registroLexico.getLinha());
        }
    }

    private void atribuicaoBoolean() {
        identificador();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("=")) {
            operadorAtribuicao();
            verdadeiroOuFalso();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoBoolean();
            } else if(registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
            }
        }  else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
            virgula();
            atribuicaoBoolean();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
            pontoeVirgula();
        } else{
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
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicaoNumerica();
            } else if(registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperado na linha " + registroLexico.getLinha());
            }
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
            virgula();
            atribuicaoNumerica();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
            pontoeVirgula();
        } else{
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
                if (registroLexico.getToken() != 1){
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

    }

}

