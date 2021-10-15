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

        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("int")) {
            atribuicao();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("boolean")) {
            atribuicao();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("byte")) {
            atribuicao();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("string")) {
            atribuicao();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("final")) {
            atribuicao();
        } else {
            throw new AnaliseSintaticaException("Esperava-se tipo de dado na linha " + registroLexico.getLinha());
        }
    }

    private void atribuicao() {
        identificador();
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("=")) {
            operadorAtribuicao();
            constanteNumerica();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                atribuicao();
            } else if(registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
                pontoeVirgula();
            } else {
                throw new AnaliseSintaticaException("Caracter inesperador na linha " + registroLexico.getLinha());
            }
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
            virgula();
            atribuicao();
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(";")){
            pontoeVirgula();
        } else{
            throw new AnaliseSintaticaException("Caracter inesperador na linha " + registroLexico.getLinha());
        }
    }

    private void identificador() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("id")) {
            throw new AnaliseSintaticaException("Esperava-se identificador na linha " + registroLexico.getLinha());
        }
    }

    private void operadorAtribuicao() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("=")) {
            throw new AnaliseSintaticaException("Esperava-se operador de atribuição na linha " + registroLexico.getLinha());
        }
    }

    private void constanteNumerica() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != 1) {
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("+") || registroLexico.getToken() == TabelaDeSimbolos.obterToken("-")) {
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                if (registroLexico.getToken() != 1){
                    throw new AnaliseSintaticaException("Esperava-se por um valor na linha " + registroLexico.getLinha());
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

