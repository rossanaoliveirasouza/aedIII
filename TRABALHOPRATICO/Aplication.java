import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
 
public class Aplication{

    public static int pGlobal;
    public static int nEntradasBucket;
    public static void main(String[] args){

        //System.out.print("--MENU DE OPÇÕES--\n1 - Criar arquivo\n2 - Inserir registro\n3 - Editar registro\n4 - Remover registro\n5 - Imprimir arquivos\n6 - Simulação\n\nOPÇÃO: ");
        //int opção = in.nextInt();

       
        try {
            ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream("diretorio.dat"));
            System.out.println("Profundidade inicial: ");
            //pGlobal = in.nextInt();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Bucket implements Serializable{
    private int pLocal;
    private NoBucket[] no;
    private int n;

    public Bucket(int pGlobal, int nEntradasBucket){
        this.pLocal = pGlobal;
        this.n = nEntradasBucket;
        this.no = new NoBucket[n];
        for(int i=0; i<n; i++){
            no[i] = new NoBucket();
        }
    }
    
    public boolean isFull(){
        for(int i=0; i<n; i++){
            if(!no[i].isFull())
                return false;
        }
        return true;
    }

    public void addElement(long CPF, long num){
        if(this.isFull()){
            //chamar função de dividir bucket;
            //chamar função addElement da classe Bucket recursivamente;
        }else{
            for(int i=0; i<n; i++){
                if(!no[i].isFull()){
                    no[i].setValue(CPF, num);
                    i=n;
                }
            }
        }
    }

    public Bucket divideBucket(int pGlobal){
        if(pGlobal == pLocal){
            //se profundidade for igual a do diretorio, atualizar o diretorio (aumentar ele e mudar a profundidade do mesmo)
        }
        this.pLocal += 1;
        Bucket buck = new Bucket(pLocal, this.n); //cria bucket vazio com profundidade correta
        this.reedestribuiCPF(buck);
        return buck;
    }

    public void reedestribuiCPF(Bucket b1){

    }

    public void removeElement(long CPF){ 
        for(int i=0; i<n; i++){
            if(CPF == no[i].getCPFValue()){
                no[i].setValue(-1, -1);
            }
        }
    }


}

class Indice implements Serializable{
    private Bucket[] bucks;

}

class NoBucket{ 
    private long cpfKey;
    private long numRegistroArqMestre;

    public NoBucket(){
        cpfKey = -1;
        numRegistroArqMestre = -1;
    }

    public NoBucket(long CPF, long Num){
        cpfKey = CPF;
        numRegistroArqMestre = Num;
    }

    public boolean isFull(){
        return (cpfKey != -1);
    }

    public void setValue(long CPF, long Num){
        cpfKey = CPF;
        numRegistroArqMestre = Num;
    }

    public long getCPFValue(){
        return cpfKey;
    }

    public long getNumRegistroValue(){
        return numRegistroArqMestre;
    }
}

class Prontuario implements Serializable{
    private String nome;
    private Data dataNascimento;
    private String sexo;
    private String observacoes;
}

class NoDiretorio{
    public int id;
    public Bucket apontaBucket;
    public NoDiretorio proxDirect;

    public NoDiretorio(){
        id = -1;
        apontaBucket = null;
        proxDirect = null;
    }

    public NoDiretorio(int i, Bucket buck){
        id = i;
        apontaBucket = buck;
        proxDirect = null;
    }

}

class Diretorio implements Serializable{
    private int p; //profundidade
    private NoDiretorio diretorio;

    public Diretorio(){
        
    }

    public Diretorio(int prof){
        p = prof;
    }

    public void setDiretorioProfundidade(int profundidade){
        p = profundidade;
    }

    public int getDiretorioProfundidade(){
        return p;
    }

    public int hashingKey(int keyCPF){
        return (keyCPF % p);
    }

}

class Data{
    int dia;
    int mes;
    int ano;
}
