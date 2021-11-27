# Compilador LC

Repositório utilizado para armazenar toda a implementação de um compilador em uma linguagem estrutural simplificada proposta no trabalho prático da disciplina de Compiladores do curso de Ciência da Computação da EMGE - Escola de Engenharia e de Ciência da Computação.



# :book: Sobre

O projeto é composto por um compilador completo capaz de traduzir programas escritos na linguagem fonte "LC" para um subconjunto de instruções da linguagem Assembly (80x86). O compilador possui características das linguagens C e Pascal, e produz um arquivo texto com extensão asm, o qual é convertido em linguagem de maquina pelo montador MASM e executado via linha de comando. 

<div align="center">
<img src="https://user-images.githubusercontent.com/23489043/143687307-2356ee2b-0327-4707-94eb-7d245bf3bbcc.png" alt="compilador" width="70%"" />
</div>



# :pencil: Tipos de dados e descrição da linguagem

A linguagem trata 4 tipos básicos de variáveis:

| Tipo de dado | Descrição                                                    |
| ------------ | ------------------------------------------------------------ |
| int          | O tipo int é um escalar que varia de -32768 a 32767          |
| byte         | O tipo byte é um escalar que varia de 0 a 255                |
| string       | O tipo string é um arranjo que pode conter até 255 caracteres úteis. Variáveis do tipo string são delimitadas por aspas e não podem conter quebra de linha |
| boolean      | O tipo boolean pode ter os valores true e false              |

No arquivo fonte, os caracteres permitidos são: letras, dígitos, espaço, sublinhado, ponto, vírgula, ponto-e-vírgula, dois-pontos, parênteses, colchetes, chaves, mais, menos, aspas, apóstrofo, barra, barra invertida, barra em pé, exclamação, interrogação, maior, menor e igual, além de quebra de linha.

Variáveis do tipo string são delimitadas por aspas e não podem conter quebra de linha.

Os identificadores de constantes e variáveis são compostos de letras, dígitos e o sublinhado, começando necessariamente por uma letra ou sublinhado e podem ter no máximo 255 caracteres. A linguagem faz a distinção entre letras maiúsculas e minúsculas (case-sensitive).

Comentários vem entre /* */ ou entre { }.

As palavras a seguir são reservadas:

| final |   int   | byte | string |  while  |  if  |  else  |
| :---: | :-----: | :--: | :----: | :-----: | :--: | :----: |
|  and  |   or    | not  |   ==   |    =    |  (   |   )    |
|   <   |    >    |  <>  |   >=   |   <=    |  ,   |   +    |
|   -   |    *    |  /   |   ;    |  begin  | end  | readln |
| write | writeln | true | false  | boolean |      |        |

Os comandos permitem atribuição para variáveis, entrada de valores pelo teclado e saída de valores para a tela, blocos (begin - end), estruturas de repetição (while), estruturas de teste (if - else), expressões aritméticas com inteiros e bytes, expressões lógicas e relacionais, além de atribuição, concatenação e comparação de igualdade entre strings.



# 👨‍💻 Algoritmo em LC

O algoritmo a seguir é um exemplo do código fonte de um programa escrito na linguagem LC

```
/*
*  Algoritmo: Aprovado ou Reprovado
*  Função: Calcular a nota final dos alunos da EMGE e informar se foram aprovados ou não
*  Autor: Leonardo Lima
*  Data: 29/10/2021
*/

{ Declaração de variáveis e constantes }
int nota, media, total, contador = 1;
byte atividadesMultiplas;
boolean aprovado = false;
string nome;
final notaMinima = 65;

{ Bloco Principal }
begin
    write, "Informe seu nome: ";
    readln, nome;
    while contador < 4
    begin
        write, "Informe a nota da ", contador, "º prova:";
        readln, nota;
        if nota >= 0
            total = total + nota;
        contador = contador + 1;
    end
    write, "Informe a nota obtida no trabalho prático: ";
    readln, nota;
    if nota >= 0
        total = total + nota;
    media = total / contador;
    if media >= notaMinima aprovado = true; else aprovado = false;
    if aprovado == true
    begin
        writeln, "Parabéns ", nome, ", você foi aprovado na disciplina de compiladores!";
    end
    else
    begin
        writeln, "Reprovado!";
    end
end
```



[![NPM](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/leolimaf) [![NPM](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/leonardolimaf)



