package compilador.lexico;

import compilador.TabelaDeSimbolos;
import compilador.erros.AnaliseLexicaException;

public class AnalisadorLexico {

    private char[] itensConteudo;
    private int estado;
    private int posicao;
    private boolean isString;
    private  int linha; // TODO VERIFICAR COMO OBTER A LINHA
    private TabelaDeSimbolos tabelaDeSimbolos;

    public AnalisadorLexico(String conteudo, TabelaDeSimbolos tabelaDeSimbolos) {
        this.itensConteudo = conteudo.toCharArray();
        this.tabelaDeSimbolos = tabelaDeSimbolos;
    }

    /* TODO IDENTIFICAR ULTIMO REGISTRO LEXICO */
    public RegistroLexico obterRegistroLexico(){
        char caracterAtual;
        RegistroLexico registroLexico;
        StringBuilder lexema = new StringBuilder();
        if (isFimDoArq())
            return null;
        estado = 0;
        while (!isFimDoArq()) {
            caracterAtual = proxCaracter();
            switch (estado) {
                case 0:
                    if (lexema.toString().startsWith("/*") && lexema.toString().endsWith("*/") || lexema.toString().startsWith("{") && lexema.toString().endsWith("}")) {
                        lexema = new StringBuilder();
                        retroceder();
                    } else if (lexema.toString().startsWith("/*") || lexema.toString().endsWith("*/")) {
                        lexema.append(caracterAtual);
                    } else if (caracterAtual == '{' || (lexema.toString().startsWith("{") || lexema.toString().endsWith("}"))) {
                        lexema.append(caracterAtual);
                    } else if (lexema.toString().startsWith("\"") && lexema.toString().endsWith("\"") && lexema.length() > 1) {
                        retroceder();
                        isString = true;
                        estado = 4;
                    } else if (lexema.toString().startsWith("\"")) {
                        if (caracterAtual == '\n'){
                            throw new AnaliseLexicaException("Strings não podem conter quebras de linha");
                        }
                        estado = 0;
                        lexema.append(caracterAtual);
                    } else if (isLetra(caracterAtual)) {
                        estado = 1;
                        lexema.append(caracterAtual);
                    } else if (isDigito(caracterAtual)) {
                        estado = 3;
                        lexema.append(caracterAtual);
                    } else if (isOperadorAtribuicao(caracterAtual)) {
                        estado = 5;
                        lexema.append(caracterAtual);
                    } else if (isOperadorRelacional(caracterAtual)) {
                        estado = 8;
                        lexema.append(caracterAtual);
                    } else if (isOperadorAritmetico(caracterAtual)) {
                        estado = 9;
                        lexema.append(caracterAtual);
                    } else if (isPontuacao(caracterAtual)) {
                        if (caracterAtual == '\"') {
                            estado = 0;
                        } else {
                            estado = 12;
                        }
                        lexema.append(caracterAtual);
                    } else if (isEspaco(caracterAtual)) {
                        estado = 0;
                    } else{
                        throw new AnaliseLexicaException("Símbolo não reconhecido");
                    }
                    break;
                case 1:
                    if (isLetra(caracterAtual) || isDigito(caracterAtual)) {
                        estado = 1;
                        lexema.append(caracterAtual);
                    } else if (isEspaco(caracterAtual) || isOperador(caracterAtual) || isPontuacao(caracterAtual) || isComentario(caracterAtual)) {
                        estado = 2;
                        retroceder();
                    } else {
                        throw new AnaliseLexicaException("Símbolo não reconhecido");
                    }
                    break;
                case 2:
                    registroLexico = new RegistroLexico();
                    if (!tabelaDeSimbolos.contemSimbolo(lexema.toString())){
                        registroLexico.setToken(tabelaDeSimbolos.obterToken(lexema.toString()));
                    } else {
                        registroLexico.setToken(0);
                    }
                    registroLexico.setLexema(lexema.toString());
                    retroceder();
                    return registroLexico;
                case 3:
                    if (isDigito(caracterAtual)) {
                        estado = 3;
                        lexema.append(caracterAtual);
                    } else if (!isLetra(caracterAtual)) {
                        estado = 4;
                        retroceder();
                    } else {
                        throw new AnaliseLexicaException("Número não reconhecido");
                    }
                    break;
                case 4:
                    registroLexico = new RegistroLexico();
                    if (!tabelaDeSimbolos.contemSimbolo(lexema.toString()) && !isString){
                        registroLexico.setToken(tabelaDeSimbolos.obterToken(lexema.toString()));
                    } else {
                        registroLexico.setToken(1);
                        isString = false;
                    }
                    registroLexico.setLexema(lexema.toString());
                    retroceder();
                    return registroLexico;
                case 5:
                    if (isOperadorAtribuicao(caracterAtual)) {
                        estado = 7;
                        lexema.append(caracterAtual);
                    } else if (isEspaco(caracterAtual) || isDigito(caracterAtual) || isLetra(caracterAtual)) {
                        estado = 6;
                        retroceder();
                    } else {
                        throw new AnaliseLexicaException("Operador de atribuição não reconhecido");
                    }
                    break;
                case 6:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(tabelaDeSimbolos.obterToken(lexema.toString()));
                    registroLexico.setLexema(lexema.toString());
                    retroceder();
                    return registroLexico;
                case 7:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(tabelaDeSimbolos.obterToken(lexema.toString()));
                    registroLexico.setLexema(lexema.toString());
                    retroceder();
                    return registroLexico;
                case 8:
                    if (isEspaco(caracterAtual) || isDigito(caracterAtual) || isLetra(caracterAtual)) {
                        estado = 7;
                    } else if (isOperadorAtribuicao(caracterAtual) || (lexema.toString().equals("<") && caracterAtual == '>')) {
                        estado = 7;
                        lexema.append(caracterAtual);
                    } else {
                        throw new AnaliseLexicaException("Operador relacional não reconhecido");
                    }
                    break;
                case 9:
                    if (isEspaco(caracterAtual) || isDigito(caracterAtual) || isLetra(caracterAtual) || caracterAtual == '\"') {
                        estado = 10;
                        retroceder();
                    } else if ((lexema.toString().equals("/") && caracterAtual == '*') || (lexema.toString().equals("*") && caracterAtual == '/')) {
                        estado = 0;
                        lexema.append(caracterAtual);
                    } else {
                        throw new AnaliseLexicaException("Operador aritmético não reconhecido");
                    }
                    break;
                case 10:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(tabelaDeSimbolos.obterToken(lexema.toString()));
                    registroLexico.setLexema(lexema.toString());
                    retroceder();
                    return registroLexico;
                case 11:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(tabelaDeSimbolos.obterToken(lexema.toString()));
                    registroLexico.setLexema(lexema.toString());
                    retroceder();
                    return registroLexico;
                case 12:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(tabelaDeSimbolos.obterToken(lexema.toString()));
                    registroLexico.setLexema(lexema.toString());
                    retroceder();
                    return registroLexico;
            }
        }
        return null;
    }

    private boolean isComentario(char c) {
        return c == '{' || c == '}';
    }

    private boolean isDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
    }

    private boolean isOperador(char c) {
        return (isOperadorAtribuicao(c) || isOperadorAritmetico(c) || isOperadorRelacional(c));
    }

    private boolean isOperadorAtribuicao(char c) {
        return c == '=';
    }

    private boolean isOperadorRelacional(char c) {
        return c == '>' || c == '<';
    }

    private boolean isOperadorAritmetico(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isPontuacao(char c) {
        return c == '(' || c == ')' || c == '.' || c == ',' || c == ';' || c == ':' || c == '[' || c == ']'
                || c == '\\' || c == '|' || c == '"' || c == '\'' || c == '!' || c == '?';
    }

    private boolean isEspaco(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private char proxCaracter() {
        return itensConteudo[posicao++];
    }

    private void retroceder() {
        posicao--;
    }

    private boolean isFimDoArq() {
        return posicao == itensConteudo.length;
    }
}
