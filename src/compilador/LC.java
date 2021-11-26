package compilador;


import java.util.Scanner;

public class LC {

    public static void main(String[] args) {

        String arquivo = "";
        Scanner scan = new Scanner(System.in);
        try {
//            do{
//                System.out.print("Informe o nome do arquivo: ");
//                arquivo = scan.nextLine();
//                if(arquivo.length() > 0){
//                    if(!arquivo.toLowerCase().endsWith(".lc")){
//                        System.err.println("Arquivo não compatível!");
//                        System.out.print("Informe o nome do arquivo: ");
//                        arquivo = scan.nextLine();
//                    }
//                }
//
//            } while(arquivo.length() == 0);

            arquivo = "entrada.lc";
            new Compilador(arquivo, args[1]);
            System.out.println("Compilação finalizada sem erros!");
        } catch (Exception e){
            System.err.println("Erro: " + e.getMessage());
        }

    }

}
