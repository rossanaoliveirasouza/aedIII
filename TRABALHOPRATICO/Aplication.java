package TRABALHOPRATICO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
 
public class Aplication{
    public static void main(String[] args){
        FileOutputStream diretorio;
        ObjectOutputStream out;
       
        try {
            //diretorio = new FileOutputStream("diretorio.db");
            out = new ObjectOutputStream (new FileOutputStream("diretorio.db"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class Bucket{
    private int profundidade;
    private int n;
    private NoBucket no;
}
class NoBucket{
    private long cpf;
    private String endereco;


}
class Prontuario{
    private String nome;
    private Data dataNascimento;
    private String sexo;
    private String observacoes;
}
class Data{
    int dia;
    int mes;
    int ano;
}