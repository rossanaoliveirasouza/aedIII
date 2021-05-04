package TRABALHOPRATICO;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException; 
public class Aplication{
    public static void main(String[] args){
        FileInputStream arquivoDiretorio;
        DataInputStream leituradoArquivoDiretorio;
        int profundidadeDiretorio=0;
        Diretorio[] vetorDiretorioEmMemoriaPrincipal;
        try {
            arquivoDiretorio = new FileInputStream("diretorio.db");
            leituradoArquivoDiretorio = new DataInputStream(arquivoDiretorio);
            profundidadeDiretorio = leituradoArquivoDiretorio.readInt();
            vetorDiretorioEmMemoriaPrincipal = new Diretorio[profundidadeDiretorio];

            //carrega o arquivo diretorio em memória principal com o vetor vetorDiretorioEmMemoriaPrincipal
            for(int i=0; i<profundidadeDiretorio-1; i++){
                vetorDiretorioEmMemoriaPrincipal[i].setDir(leituradoArquivoDiretorio.readInt());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
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