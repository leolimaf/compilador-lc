package compilador.lexico;

import compilador.erros.LexicoException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ES_Arquivo {

    private char[] itensConteudo;
    private int estado;
    private int posicao;


    public ES_Arquivo(String nomeArqEntrada) {
        abreArquivoEntrada(nomeArqEntrada);
    }

    public void abreArquivoEntrada(String nomeArqEntrada) {
        try {
            String conteudo = new String(Files.readAllBytes(Paths.get(nomeArqEntrada)), StandardCharsets.UTF_8);
            itensConteudo = conteudo.toCharArray();
            posicao = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* TODO IDENTIFICAR ULTIMO REGISTRO LEXICO */
    public RegistroLexico obterRegistroLexico() {
        char caracterAtual;
        RegistroLexico registroLexico;
        String lexema = "";
        if (isFimDoArq())
            return null;
        estado = 0;
        while (!isFimDoArq()) {
            caracterAtual = proxCaracter();
            switch (estado) {
                case 0:
                    if (lexema.startsWith("/*") && lexema.endsWith("*/") || lexema.startsWith("{") && lexema.endsWith("}")){
                        estado = 0;
                        lexema = "";
                    } else if (lexema.startsWith("/*") || lexema.endsWith("*/")){
                        lexema += caracterAtual;
                        continue;
                    } else if (caracterAtual == '{' || (lexema.startsWith("{") || lexema.endsWith("}"))){
                        lexema += caracterAtual;
                        continue;
                    } else if (isLetra(caracterAtual)) {
                        estado = 1;
                        lexema += caracterAtual;
                    } else if (isDigito(caracterAtual)) {
                        estado = 3;
                        lexema += caracterAtual;
                    } else if (isOperadorAtribuicao(caracterAtual)) {
                        estado = 5;
                        lexema += caracterAtual;
                    } else if (isOperadorRelacional(caracterAtual)) {
                        estado = 8;
                        lexema += caracterAtual;
                    } else if (isOperadorAritmetico(caracterAtual)) {
                        estado = 9;
                        lexema += caracterAtual;
                    } else if (isPontuacao(caracterAtual)){
                        estado = 11;
                        lexema += caracterAtual;
                    } else if (isEspaco(caracterAtual)) {
                        estado = 0;
                    } else
                        throw new LexicoException("Símbolo não reconhecido");
                    break;
                case 1:
                    if (isLetra(caracterAtual) || isDigito(caracterAtual)) {
                        estado = 1;
                        lexema += caracterAtual;
                    } else if (isEspaco(caracterAtual) || isOperador(caracterAtual) || isPontuacao(caracterAtual)) {
                        estado = 2;
                        retroceder();
                    } else {
                        throw new LexicoException("Símbolo não reconhecido");
                    }
                    break;
                case 2:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(RegistroLexico.TK_IDENTIFICADOR);
                    registroLexico.setLexema(lexema);
                    retroceder();
                    return registroLexico;
                case 3:
                    if (isDigito(caracterAtual)) {
                        estado = 3;
                        lexema += caracterAtual;
                    } else if (!isLetra(caracterAtual)) {
                        estado = 4;
                        retroceder();
                    } else {
                        throw new LexicoException("Número não reconhecido");
                    }
                    break;
                case 4:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(RegistroLexico.TK_CONSTANTE);
                    registroLexico.setLexema(lexema);
                    retroceder();
                    return registroLexico;
                case 5:
                    if (isOperadorAtribuicao(caracterAtual)) {
                        estado = 7;
                        lexema += caracterAtual;
                    } else if (isEspaco(caracterAtual) || isDigito(caracterAtual) || isLetra(caracterAtual)) {
                        estado = 6;
                    } else {
                        throw new LexicoException("Operador de atribuição não reconhecido");
                    }
                    break;
                case 6:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(RegistroLexico.TK_OP_ATRIBUICAO);
                    registroLexico.setLexema(lexema);
                    retroceder();
                    return registroLexico;
                case 7:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(RegistroLexico.TK_OP_RELACIONAL);
                    registroLexico.setLexema(lexema);
                    retroceder();
                    return registroLexico;
                case 8:
                    if (isEspaco(caracterAtual) || isDigito(caracterAtual) || isLetra(caracterAtual)) {
                        estado = 7;
                    } else if (isOperadorAtribuicao(caracterAtual) || (lexema.equals("<") && caracterAtual == '>')) {
                        estado = 7;
                        lexema += caracterAtual;
                    }  else {
                        throw new LexicoException("Operador relacional não reconhecido");
                    }
                    break;
                case 9:
                    if (isEspaco(caracterAtual) || isDigito(caracterAtual) || isLetra(caracterAtual)) {
                        estado = 10;
                    } else if ((lexema.equals("/") && caracterAtual == '*') || (lexema.equals("*") && caracterAtual == '/')) {
                        estado = 0;
                        lexema += caracterAtual;
                    } else {
                        throw new LexicoException("Operador aritmético não reconhecido");
                    }
                    break;
                case 10:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(RegistroLexico.TK_OP_ARITIMETICO);
                    registroLexico.setLexema(lexema);
                    retroceder();
                    return registroLexico;
                case 11:
                    registroLexico = new RegistroLexico();
                    registroLexico.setToken(RegistroLexico.TK_PONTUACAO);
                    registroLexico.setLexema(lexema);
                    retroceder();
                    return registroLexico;
            }
        }
        return null;
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
        return c == '(' || c == ')' || c == ',' || c == ';';
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
