package TRABALHOPRATICO;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner; 
public class Aplication{
    public static void main(String[] args){

        Diretorio[] vetorDiretorioEmMemoriaPrincipal = null;
        carregaDiretorioEmMemoriaPrincipal(vetorDiretorioEmMemoriaPrincipal);

        int opcaoUsuario = exibirTelaPrincipal();
        opcaoUsuario(opcaoUsuario);   
    }

    public static int exibirTelaPrincipal(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("*************************************************************\n" +
                            "*************************************************************\n" +
                            "Escolha uma das opções abaixo:\n" +
                            "1. Criar Arquivo;\n" + 
                            "2. Inserir Registro;\n" + 
                            "3. Editar Registro;\n" + 
                            "4. Remover Registro;\n" + 
                            "5. Imprimir Arquivos;\n" + 
                            "6. Simulação.\n" +
                            "*************************************************************\n" +
                            "*************************************************************\n");
        int opcao = scanner.nextInt();
        return opcao;
    }
    public static void opcaoUsuario(int opcaoUsuario){
        if(opcaoUsuario == 1){ criarArquivo(); }
        else if(opcaoUsuario == 2){ inserirRegistro(); }
        else if(opcaoUsuario == 3){ editarRegistro(); }
        else if(opcaoUsuario == 4){ removerRegistro(); }
        else if(opcaoUsuario == 5){ imprimirArquivos(); }
        else if(opcaoUsuario == 6){ simulacao(); }
    }
    //Método para carregar diretório em memória principal
    public static void carregaDiretorioEmMemoriaPrincipal(Diretorio[] vetorDiretorioEmMemoriaPrincipal){
        FileInputStream arquivoDiretorio;
        DataInputStream leituradoArquivoDiretorio;
        int profundidadeDiretorio=0;
        try {
            arquivoDiretorio = new FileInputStream("diretorio.db");
            leituradoArquivoDiretorio = new DataInputStream(arquivoDiretorio);

            profundidadeDiretorio = leituradoArquivoDiretorio.readInt();
            vetorDiretorioEmMemoriaPrincipal = new Diretorio[profundidadeDiretorio];

            for(int i=0; i<profundidadeDiretorio-1; i++){
                vetorDiretorioEmMemoriaPrincipal[i].setDir(leituradoArquivoDiretorio.readInt());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Método para atualizar valores do arquivo diretório
    public static void atualizaDiretorio(){}

    public static void criarArquivo(){}

    public static void inserirRegistro(){}

    public static void editarRegistro(){}

    public static void removerRegistro(){}

    public static void imprimirArquivos(){}

    public static void simulacao(){}
}
class Bucket{
    private int profundidade;
    private int n;
    private NoBucket no;

    Bucket(){}

    public int getProfundidade() {
        return profundidade;
    }
    public void setProfundidade(int profundidade) {
        this.profundidade = profundidade;
    }
    public int getN() {
        return n;
    }
    public void setN(int n) {
        this.n = n;
    }
    public NoBucket getNo() {
        return no;
    }
    public void setNo(NoBucket no) {
        this.no = no;
    }
}
class NoBucket{
    private long cpf;
    private String endereco;

    NoBucket(){}

    public long getCpf() {
        return cpf;
    }
    public void setCpf(long cpf) {
        this.cpf = cpf;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
class Prontuario{
    private String nome;
    private Data dataNascimento;
    private String sexo;
    private String observacoes;

    Prontuario(){}
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public Data getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(Data dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
class Data{
    int dia;
    int mes;
    int ano;

    Data(){}

    public int getDia() {
        return dia;
    }
    public void setDia(int dia) {
        this.dia = dia;
    }
    public int getMes() {
        return mes;
    }
    public void setMes(int mes) {
        this.mes = mes;
    }
    public int getAno() {
        return ano;
    }
    public void setAno(int ano) {
        this.ano = ano;
    }
}
class Diretorio{
    int dir;

    Diretorio(){}

    Diretorio(int n){
        this.dir = n;
    }

    public int getDir() {
        return dir;
    }
    public void setDir(int dir) {
        this.dir = dir;
    }
}