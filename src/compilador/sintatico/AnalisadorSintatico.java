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

    public void S(){
        D();
        B();
    }

    private void D() {
        registroLexico = analisadorLexico.obterProxRegistroLexico(true);
        if (registroLexico == null || registroLexico.getToken() == TabelaDeSimbolos.obterToken("begin")){
            return;
        }
        declaracao();
    }

    private void declaracao() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("int")){
            identificador();
            registroLexico = analisadorLexico.obterProxRegistroLexico(true);
            if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("=")){
                atribuicao();
                constante();
            } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken(",")){
                registroLexico = analisadorLexico.obterProxRegistroLexico();
                declaracao();
            } else {
                throw new AnaliseSintaticaException("Declaração de variavel ou constante não foi finalizada corretamente na linha " + registroLexico.getLinha());
            }
        } else if (registroLexico.getToken() == TabelaDeSimbolos.obterToken("string")){
            identificador();
        } else {
            throw new AnaliseSintaticaException("Esperava-se tipo de dado na linha " + registroLexico.getLinha());
        }
    }

    private void identificador() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("id")){
            throw new AnaliseSintaticaException("Esperava-se identificador na linha " + registroLexico.getLinha());
        }
    }

    private void atribuicao() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != TabelaDeSimbolos.obterToken("=")){
            throw new AnaliseSintaticaException("Esperava-se operador de atribuição na linha " + registroLexico.getLinha());
        }
    }

    private void constante() {
        registroLexico = analisadorLexico.obterProxRegistroLexico();
        if (registroLexico.getToken() != 1){
            throw new AnaliseSintaticaException("Esperava-se por um valor na linha " + registroLexico.getLinha());
        }
    }

    private void B() {

    }

}

