/* 
********************
PROGRAMA - TRABALHO PRÁTICO 
Disciplina: Algoritmo e Estrutura de Dados III - PUC Minas
Professor: Zenilton Patrocínio Jr.
Autoras:
    Julia Gontijo Lopes   - matrícula: 701327
    Rossana Oliveira de Souza  - matrícula: 705085
********************
*/

import java.io.RandomAccessFile;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class tt{

    public static Scanner ler = new Scanner(System.in);
    //Objetos das classes que serão utilizados no programa principal
    public static Prontuario prontuario;
    public static Indice indice;
    public static Diretorio diretorio;

    public static void main(String[] args) throws Exception{
       menu();
    }

    public static int menu() throws Exception{
        String opcao = "1"; //usando opção em string para evitar erros de receber o numero errado
        while (!opcao.equals("7")) {
           
            System.out.println(
                        "\n\n--MENU DE OPÇÕES--\n1 - Criar arquivo\n2 - Inserir registro\n3 - Editar registro\n4 - Remover registro\n5 - Imprimir arquivos\n6 - Simulação\n7 - Sair do programa\n\nOPÇÃO: ");
            opcao = ler.nextLine();
            if (opcao.equals("1")) {
                opcao1();
            } else if (opcao.equals("2")) {
                opcao2();
            } else if (opcao.equals("3")) {
                opcao3();
            } else if (opcao.equals("4")) {
                opcao4();
            } else if (opcao.equals("5")) {
                opcao5();
            } else if (opcao.equals("6")) {
                opcao6();
            } else if (opcao.equals("7")) {
                return 0;
            } else{
                System.out.println("Esta é uma opção inválida!\nPor favor, escolha uma opção de 1 a 6.\nCaso deseje sair do programa, digite 7");
            }
            opcao = ler.nextLine();
        }
        return 0;
    }
    
    //Cria os arquivos Diretorio, Índice e Prontuário
    public static void opcao1() throws Exception {
        try{
            //DIRETORIO//
            System.out.println(
                    "\n-- CRIANDO ARQUIVO DIRETORIO --\nFavor inserir valor para a profundidade global: ");
            diretorio = new Diretorio(ler.nextInt()); //inicializa o diretório
            diretorio.write(); //escreve no arquivo

            //INDICE//
            System.out.println(
                    "\n-- CRIANDO ARQUIVO INDICE --\nFavor inserir valor para o número de entradas em cada Bucket: ");
            indice = new Indice(ler.nextInt(), diretorio.getpGlobal()); //inicializa o índice
            indice.writeIndice(diretorio.getpGlobal()); //escreve no arquivo
            
            //PRONTUARIO//
            System.out.println(
                    "\n-- CRIANDO ARQUIVO MESTRE --\nFavor inserir valor para o tamanho possível para escrita das observações: "); 
            prontuario = new Prontuario(ler.nextInt()); //inicializa o diretório
            //OBS: ja escreve o cabecalho do arquivo no seu proprio construtor
        }catch(Exception e){
        }
    }
    
    //Insere um registro no Prontuário
    public static void opcao2() throws Exception {
        System.out.println("\n-- ACESSANDO OPÇÃO 2 --");
        Registro registro = recebeDadosInformados();
        //insere os dados recebidos no registro, no arquivo mestre (Prontuário)
        insere(registro);
    }

    //Edita um registro específico
    public static void opcao3() throws Exception {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        System.out.println("\n-- ACESSANDO OPÇÃO 3 --\nDigite o cpf do registro que deseja atualizar: ");
        int cpf = ler.nextInt();
        int positionNoIndice = diretorio.getEnderecoCorrespondente(cpf); //retorna a position no índice em que está o cpf
        int positionNoArqMestre = indice.getEnderecoPorCPF(positionNoIndice, cpf); //procura no arq de indice, no bucket escrito na position passada (positionNoIndice), o elemento cujo cpf == ao cpf de entrada e retorna a position no arqMestre
        if(positionNoArqMestre != -1){ //se pegou a posicao certinho
            System.out.println("\nConfira os dados atuais do registro informado: ");
            prontuario.imprimeRegistro(arquivoMestre, positionNoArqMestre);
            System.out.println("\nInforme os novos dados: ");
            Registro registro = recebeDadosInformadosSemCPF(cpf); //cria registro com novos dados
            prontuario.writeRegistro(arquivoMestre, positionNoArqMestre, registro); //atualiza/reescreve no arquivo
        }else System.out.println("Registro não encontrado");
        arquivoMestre.close();
    }

    //Remove um registro específico
    public static void opcao4() throws Exception {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        System.out.println("\n-- ACESSANDO OPÇÃO 3 --\nDigite o cpf do registro que deseja remover: ");
        int cpf = ler.nextInt();
        int positionNoIndice = diretorio.getEnderecoCorrespondente(cpf); //retorna a position no índice em que está o cpf
        int positionNoArqMestre = indice.getEnderecoPorCPF(positionNoIndice, cpf); //procura no arq de indice, no bucket escrito na position passada (positionNoIndice), o elemento cujo cpf == ao cpf de entrada e retorna a position no arqMestre
        System.out.println("\n-- O Registro abaixo será definitivamente excluído: \n");
        prontuario.imprimeRegistro(arquivoMestre, positionNoArqMestre);
        System.out.println("\n-- 1- Confirmar ação: \n-- 2- Voltar ao menu inicial");
        int confirmacao = ler.nextInt();

        if(confirmacao == 1){
            //apaga logicamente no prontuário (arquivo mestre) e índice
            prontuario.apagaRegistro(arquivoMestre, positionNoArqMestre);
            indice.removeElementoBucket(positionNoIndice, cpf);
            System.out.println("\nRegistro apagado com sucesso\n");
        }
        arquivoMestre.close();
    }

    //Imprime os arquivos diretório, índice e prontuário (arquivo mestre)
    public static void opcao5() throws Exception {
        System.out.print("\n-- ACESSANDO OPÇÃO 5"); 
        diretorio.imprimeDiretorioCompleto();
        indice.imprimeIndiceCompleto();
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        prontuario.imprimeProntuario(arquivoMestre);
        arquivoMestre.close();
    }

    //Simula inserções de cpf's aleatórios
    public static void opcao6() throws Exception{
        System.out.print("\n-- ACESSANDO OPÇÃO 6 - SIMULAÇÃO"); 
        opcao1(); //cria os arquivos
        System.out.println(
                    "\n-- NUMERO DE CPF's --\nFavor inserir quantidade total de cpf's para esta simulação: ");
        int n = ler.nextInt();
        List<Integer> vetKeys = new ArrayList<Integer>(n);
        double tempoInsercao = geraCpfParaSimulacao(vetKeys, n);
        pesquisaEImprimiCpfSimulacao(vetKeys, n, tempoInsercao);
    }

    public static double geraCpfParaSimulacao(List<Integer> vetKeys, int n) throws Exception{
        //utiliza random para gerar numeros aleatórios
        Random gerador = new Random();
        gerador.setSeed(4);
        double tempoInicio = System.currentTimeMillis(); //para calcular tempo de execução
        int tamTotalRegistro = prontuario.getTAM_TC() + prontuario.getTAM_OBSERVACAO();
        byte[] vetorFinal = new byte[tamTotalRegistro*n]; //vetor de bytes usado para concatenar todos os registros a sereem excritos
        for(int i=0; i<n; i++){
            int cpf = (int)Math.abs(gerador.nextInt()% 999999999); //gera cpf aleatório de 0 até 999999999
            //verifica a repeticao de cpf
            if(!vetKeys.contains(cpf)){
                vetKeys.add(cpf);
                Registro registro = new Registro(vetKeys.get(i), " ", 'f', " ", " "); //valores default
                byte[] vetorBytes = registro.toByteArray(); //transforma o registro em vetor de bytes
                byte[] bytesFaltantes = new byte[tamTotalRegistro]; //vetor com a quantidade de bytes de um registro (tam_tc + tam_observacao)
                System.arraycopy(vetorBytes, 0, bytesFaltantes, 0, vetorBytes.length); //copia o vetor de bytes do registro no inicio do vetor coma quantidaded necessaria (para pular no arquivo o tamanho certo ate escrever o proximo registro e ler corretamente)
                System.arraycopy(vetorBytes, 0, vetorFinal, i*tamTotalRegistro, vetorBytes.length); //concatena todos os vetores de registros no vetorFinal
                insereHashing(i, registro);//insere o endereço no indice (sequencial (manda 'i') pois escreve no arquivo mestre sequencialmente)
            }else{
                i--; //diminui 1 unidade para adcionar o número certo de cpfs
            }
        }
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        System.out.println("Abriu o arquivo");

        try{
            arquivoMestre.seek(12); //pula cabecalho
            arquivoMestre.write(vetorFinal); //escreve todos os registros de uma vez
            System.out.println("write");
        }catch(Exception e){
        }
        arquivoMestre.close();
        double tempoFim = System.currentTimeMillis();
        return (tempoFim - tempoInicio)/1000;
    }

    public static void pesquisaEImprimiCpfSimulacao(List<Integer> vetKeys, int n, double tempoInsercao) throws Exception{
        double tempoInicio = System.currentTimeMillis();
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        for(int i=0; i<n; i++){
            int positionNoIndice = diretorio.getEnderecoCorrespondente(vetKeys.get(i));
            int positionNoArqMestre = indice.getEnderecoPorCPF(positionNoIndice, vetKeys.get(i));
            prontuario.imprimeRegistro(arquivoMestre, positionNoArqMestre);
        }
        arquivoMestre.close();
        double tempoFim = System.currentTimeMillis();
        System.out.println("Tempo de Insercao: " + tempoInsercao + "\nTempo de pesquisa e recuperação: " + (tempoFim - tempoInicio)/1000 + "s");
    }

    //cria um registro com os dados aqui recebidos para inserir no Prontuário (sem as anotações)
    public static Registro recebeDadosInformados() throws IOException {
        int respostaCpf=0;
        System.out.println("Digite o nome: ");
        String respostaNome = ler.nextLine();

        // trata nomes com mais carcateres que o permitido
        while (respostaNome.length() > 20) {
            System.out.print("Favor digitar um nome de no maximo 20 caracteres: ");
            respostaNome = ler.nextLine();
        }
        System.out.println("Digite o sexo com o qual se identifica (F ou M): ");
        char respostaSexo = ler.next().charAt(0);
        System.out.println("Digite a data de nascimento: ");
        String respostaDataNascimento = ler.nextLine();
        respostaDataNascimento = ler.nextLine();
        try{
            System.out.println("CPF: ");
            respostaCpf = ler.nextInt();
        }catch(InputMismatchException e){
            System.out.println("O valor informado não é um valor válido.\nPor gentileza, insira os valores conforme solicitado:");
            recebeDadosInformados();
        }
        Registro registro = new Registro(respostaCpf, respostaNome, respostaSexo, respostaDataNascimento,
                "sem observações"); //anotações fica como "sem observações" por default
        return registro;
    }

    //recebe dados (incluindo anotações) para atualizar um registro
    public static Registro recebeDadosInformadosSemCPF(int cpf) throws IOException {
        System.out.println("Digite o nome: ");
        String respostaNome = ler.nextLine();
        respostaNome = ler.nextLine();

        // trata nomes com mais carcateres que o permitido
        while (respostaNome.length() > 20) {
            System.out.print("Favor digitar um nome de no maximo 20 caracteres: ");
            respostaNome = ler.nextLine();
        }
        System.out.println("Digite o sexo com o qual se identifica (F ou M): ");
        char respostaSexo = ler.next().charAt(0);
        System.out.println("Digite a data de nascimento: ");
        String respostaDataNascimento = ler.nextLine();
        respostaDataNascimento = ler.nextLine();
        System.out.println("Anotações sobre o paciente: ");
        String respostaObservacoes = ler.nextLine();
        int tam = getTamAnotacoesNoArquivo(); // le do arquivo o tamanho proposto para as anotacoes do medico

        // trata anotacoes com mais caracteres que o permitido
        while (respostaObservacoes.length() > tam) {
            System.out.print("Favor digitar anotação de, no maximo, " + tam + " caracteres: ");
            respostaObservacoes = ler.nextLine();
        }
        Registro registro = new Registro(cpf, respostaNome, respostaSexo, respostaDataNascimento,
                respostaObservacoes); //não é possível alterar o cpf. Para isso teria que apagar o registro e criar outro para que ele seja inserido corretamente no índice
        return registro;
    }

    //le do arquivo e retorna o espaço definido para as observações
    public static int getTamAnotacoesNoArquivo() throws IOException {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        try {
            arquivoMestre.seek(4); // pula o escrito referente ao tamanho ocupado pelo arquivo fora as anotações
                                   // (TAM_TC)
            int tam = arquivoMestre.readInt();
            arquivoMestre.close();
            return tam;
        } catch (IOException e){
            return 0;
        } finally{
            arquivoMestre.close();
        }
    }

    public static void insere(Registro registro)throws Exception{
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");

        //escreve o registro no fim do arquivo mestre (Prontuário)
        int endereco = prontuario.write(arquivoMestre, registro); //retorna endereco (posição) para inseri-lo no bucket com o cpf
        insereHashing(endereco, registro);
    }

    public static void insereHashing(int endereco, Registro registro) throws Exception{
        int key = diretorio.getEnderecoCorrespondente(registro.getidRegistroCpf()); //retorna o endereço do índice aonde inserir o cpf
        int pLocalBucket = indice.addElemento(key, registro.getidRegistroCpf(), endereco); //tenta adcionar o cpf e endereço no índice de posição = key

        //se for verdade, o elemento não conseguiu ser inserido e vai ter que dividir o bucket
        //se for false, o elemento foi inserido e a função acaba
        if(pLocalBucket != -1){ 
            if(pLocalBucket == diretorio.getpGlobal()){ //se for verdade, vai ter que dividir o diretório
                ElementoBucket[] elementos = indice.divideBucket(key, diretorio.getpGlobal()+1);  
                diretorio.dobraDiretorio();
                diretorio.mudaCorrespondente(key, indice.getQtdBucketsTotal()-1);
                key = diretorio.getEnderecoCorrespondente(registro.getidRegistroCpf(), pLocalBucket +1);
                indice.addElemento(key, registro.getidRegistroCpf(), endereco); //adciona o elemento (cpf e endereço) novo
                //para cada elemento que estava no bucket, refaz a operação de key e adciona novamente (de acordo com a PLocal nova)
                for(ElementoBucket element: elementos){
                    key = diretorio.getEnderecoCorrespondente(element.getCPF(), pLocalBucket +1);
                    int continuaCheio = indice.addElemento(key, element.getCPF(), element.getEndRegistro());
                    //caso todos os números continuaram a ir para o mesmo bucket, chama a função novamente para redividir o bucket e inserir os elementos
                    if(continuaCheio != -1){
                    insereHashing(endereco, registro);
                    }
                }
            }else{
                ElementoBucket[] elementos = indice.divideBucket(key, diretorio.getpGlobal()); 
                diretorio.mudaCorrespondente2(key, indice.getQtdBucketsTotal()-1);
                key = diretorio.getEnderecoCorrespondente(registro.getidRegistroCpf(), pLocalBucket +1);
                indice.addElemento(key, registro.getidRegistroCpf(), endereco); //adciona o elemento (cpf e endereço) novo
                //para cada elemento que estava no bucket, refaz a operação de key e adciona novamente (de acordo com a PLocal nova)
                for(ElementoBucket element: elementos){
                    key = diretorio.getEnderecoCorrespondente(element.getCPF(), pLocalBucket +1);
                    int continuaCheio = indice.addElemento(key, element.getCPF(), element.getEndRegistro());
                    //caso todos os números continuaram a ir para o mesmo bucket, chama a função novamente para redividir o bucket e inserir os elementos
                    if(continuaCheio != -1){
                        insereHashing(endereco, registro);
                    }
                } 
            }
        }
    }

}

//Cabeçalho do Prontuário
class Cabecalho{
    private final int TAM_TC = 43; //bytes usados na escrita do registro tiodo, menos a observação
    private int TAM_OBSERVACAO; //vindo por parametro
    private int qtdRegistrosEscritos;

    Cabecalho(int tamObs)throws Exception{
        this.TAM_OBSERVACAO = tamObs;
        this.qtdRegistrosEscritos = 0;
        try{
            write();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. construtor cabeçalho");
            tt.menu();
        }
    }

    public void updateCabecalho(int qtdRegistros)throws Exception{
        this.qtdRegistrosEscritos = qtdRegistros;
        try{
            write();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. updatecabeçalho");
            tt.menu();
        }
    }

    public int getTAM_TC(){
        return this.TAM_TC;
    }

    public int getTAM_OBSERVACAO(){
        return this.TAM_OBSERVACAO;
    }

    public int getqtdRegistrosEscritos(){
        return this.qtdRegistrosEscritos;
    }

    public void setQtdRegistrosEscritos(int qtdRegistrosEscritos) {
        this.qtdRegistrosEscritos = qtdRegistrosEscritos;
    }

    //escreve no arquivo Prontuario o cabecalho
    public void write()throws Exception {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        try {
            arquivoMestre.seek(0);
            arquivoMestre.writeInt(this.TAM_TC);
            arquivoMestre.writeInt(this.TAM_OBSERVACAO);
            arquivoMestre.writeInt(this.qtdRegistrosEscritos);
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. write cabeçalho");
            tt.menu();
        } finally{
            arquivoMestre.close();
        }
    }

    //le do arquivo Prontuario o cabecalho
    public void read() throws Exception{
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        try{
            arquivoMestre.seek(4); //pula TAM_TC que é constante
            this.TAM_OBSERVACAO = arquivoMestre.readInt();
            this.qtdRegistrosEscritos = arquivoMestre.readInt();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. read cabeçalho");
            tt.menu();
        }finally{
            arquivoMestre.close();
        }
    }


}

class Registro {
    private boolean lapide; // 0 (false) = sem lapide ;  1 (true) = com lapide (deletado)
    private int idRegistroCpf; // cpf de 0 a 99999999999
    private String nome; // apenas primeiro nome (maximo 22 bytes -> 2 bytes para escrever o tamanho)
    private char sexo; // F ou M
    private String dataNascimento; // formato: 28062001 (28 Junho 2001)
    private String observacao; // tamanho do campo recebido como parâmetro 

    Registro(int cpf, String name, char sex, String data, String obs) {
        this.lapide = false;
        this.idRegistroCpf = cpf;
        this.nome = name;
        this.sexo = sex;
        this.dataNascimento = data;
        this.observacao = obs;
    }

    Registro(int cpf, String name, char sex, String data) {
        this.lapide = false;
        this.idRegistroCpf = cpf;
        this.nome = name;
        this.sexo = sex;
        this.dataNascimento = data;
        this.observacao = "";
    }

    Registro(boolean lapd, int cpf, String name, char sex, String data, String obs) {
        this.lapide = lapd;
        this.idRegistroCpf = cpf;
        this.nome = name;
        this.sexo = sex;
        this.dataNascimento = data;
        this.observacao = obs;
    }

    Registro() {}

    public int getidRegistroCpf(){
        return this.idRegistroCpf;
    }

    public String toString() {
        return "\nLAPIDE: "+ this.lapide + "\nCPF: " + this.idRegistroCpf + "\nNome: " + this.nome + "\nSexo.: " + this.sexo
                + "\nData de Nascimento: " + this.dataNascimento + "\nObservações: " + this.observacao;
    }

    public void apagaRegistro() {
        this.lapide = true;
    }

    public boolean isApagado() {
        return this.lapide;
    }

    //escreve no arquivo
    public void write(RandomAccessFile file) throws Exception {
        try {                                                
            file.write(toByteArray());
        } catch (Exception error) {
        }
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeBoolean(lapide);
        dos.writeInt(idRegistroCpf);
        dos.writeUTF(nome);
        dos.writeChar(sexo);
        dos.writeUTF(dataNascimento);
        dos.writeUTF(observacao);

        return baos.toByteArray();
    }

    //le do arquivo
    public void read(RandomAccessFile file, int tamTC, int tamOBS) throws Exception {
        try {
            byte[] array = new byte[tamTC + tamOBS]; //tamanho utilizado em casa registro = tamTC + tamOBS
            file.read(array);
            fromByteArray(array);
        } catch (IOException error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. read registro");
            tt.menu();
        }
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.lapide = dis.readBoolean();
        this.idRegistroCpf = dis.readInt();
        this.nome = dis.readUTF();
        this.sexo = dis.readChar();
        this.dataNascimento = dis.readUTF();
        this.observacao = dis.readUTF();
    }
}

class Prontuario{
    private Cabecalho cabecalho;
    private Registro registro;
    private List<Integer> posicoesDosApagados; //guarda as positions (endereços) dos registros apagados para reaproveitamento na inerção

    Prontuario(int tamObs)throws Exception{
        try{
            this.cabecalho = new Cabecalho(tamObs); //ao criar o prontuário, so o que existe é o cabeçalho
            this.posicoesDosApagados = new ArrayList<Integer>();
        }catch(Exception error){
            //tratar erro
        }
    }

    //escreve no arquivo mestre um novo registro -> retorna a posição em que foi escrita (para inserir no indice)
    public int write(RandomAccessFile file, Registro registro)throws Exception{
        try{
            file.seek(0);
            this.cabecalho.read();
            if(!this.posicoesDosApagados.isEmpty()){ //se tiver algum registro apagado
                Iterator<Integer> iter = this.posicoesDosApagados.iterator();
                int i=0;
                while(iter.hasNext()){
                    int p = iter.next();
                    readRegistro(file, p);
                    if(this.registro.isApagado()){ //confere se esta apagado mesmo no arquivo (pela lapide)
                        this.posicoesDosApagados.remove(i);
                        return writeRegistro(file, p, registro); //se tiver algum registro apagado, sobreescreve ele
                    }
                    i++;
                }
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado write registro");
            tt.menu();
        }
        //se nenhum resgitro estiver apagado, esreve o registro no final do arquivo e atualiza o cabeçalho
        this.cabecalho.updateCabecalho(this.cabecalho.getqtdRegistrosEscritos() + 1);
        return writeRegistro(file, this.cabecalho.getqtdRegistrosEscritos()-1, registro);
    }

    //escreve o registro na posição dada
    public int writeRegistro(RandomAccessFile file, int position, Registro registro) throws Exception {
        try {
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO())));                                                  
            registro.write(file);
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. write registro");
            tt.menu();
        }
        return position;
    }

    //le um registro do arquivo em uma posição dada
    public void readRegistro(RandomAccessFile file, int position) throws Exception {
        try {
            this.registro = new Registro();
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO())));
            this.registro.read(file, this.cabecalho.getTAM_TC(),this.cabecalho.getTAM_OBSERVACAO());
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. readregistro");
            tt.menu();
        }
    }

    //apaga registro logicamente
    public void apagaRegistro(RandomAccessFile file, int position) throws Exception {
        try{
            file.seek(0);
            this.cabecalho.read();
            //le registro, coloca lapide como true e reescreve
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO()))); // coloca na posição da lápide
            this.registro.read(file, this.cabecalho.getTAM_TC(), this.cabecalho.getTAM_OBSERVACAO());
            this.registro.apagaRegistro();
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO()))); // coloca na posição da lápide
            this.registro.write(file);
            this.posicoesDosApagados.add(position);
        }catch(Exception e){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. apaga registro");
            tt.menu();
        }
    }

    //imprime prontuário completo
    public void imprimeProntuario(RandomAccessFile file)throws Exception{
        System.out.println("\n*PRONTUARIOS*");
        try{
            file.seek(0);
            this.cabecalho.read();
            for(int i=0; i<cabecalho.getqtdRegistrosEscritos(); i++){
                imprimeRegistro(file, i); //le e imprime registro por registro
            }
        }catch(Exception e){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            tt.menu();
        }
    }

    //imprime um único registro
    public void imprimeRegistro(RandomAccessFile file, int position)throws Exception{
        try{
            readRegistro(file, position);
            if(!this.registro.isApagado()){ //imprime caso não estiver apagado logicamente
                System.out.println(this.registro.toString());
            }
        }catch(Exception e){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            tt.menu();
        }
    }

    public int getTAM_TC(){
        return cabecalho.getTAM_TC();
    }

    public int getTAM_OBSERVACAO(){
        return cabecalho.getTAM_OBSERVACAO();
    }
}

class Bucket{
    private int pLocal;
    private int elementosCheios; //quantidade de elementos utilizados em um bucket
    private ElementoBucket[] elemento; //cada elemento é uma posição dentro do bucket

    Bucket(int p, int nEntradas){
        this.pLocal = p;
        this.elemento = new ElementoBucket[nEntradas]; //nEntradas = quantidade de elementos por bucket
        for(int i=0; i<nEntradas; i++){
          this.elemento[i] = new ElementoBucket(-1, -1); //coloca cpf e endereçø como -1 por default
        }
    }

    public int getPLocal(){
        return this.pLocal;
    }

    public void setPLocal(int p){
        this.pLocal = p;
    }

    public void intoString(int nEntradas){
        System.out.println("\nBUCKET: \nProfundidade local = " + this.pLocal+ "quantidade de elementos cheios = "+this.elementosCheios);
        for(int i=0; i<nEntradas; i++){
            System.out.print("\nElemento "+i+":");
            this.elemento[i].intoString();
        }
        System.out.println("-----------------------------------");
    }

    //escreve o bucket
    public void write(RandomAccessFile file) throws Exception{
        try {
            file.writeInt(this.pLocal);
            file.writeInt(this.elementosCheios);
            for(int i=0; i<elemento.length; i++){
                this.elemento[i].write(file);
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. write bucket");
            tt.menu();
        }
    }

    //adciona cpf e endereço ao bucket
    public boolean adcionaElemento(int cpf, int end){
        for(int i=0; i<elemento.length; i++){
            if((elemento[i].getCPF()== -1) || elemento[i].isApagado()){ //se ainda estiver vazio (cpf = -1 por default) ou com lapide true
                elemento[i].atualizaElemento(cpf, end);
                this.elementosCheios+=1;
                return true;
            }
        }
        return false;
    }

    //apaga um elemento logicamente
    public int apagaElemento(RandomAccessFile file, int cpf)throws Exception{
        try {
            for(int i=0; i<elemento.length; i++){
                boolean achouCPF = this.elemento[i].comparaCPF(cpf); //quando cpf do indice == cpf de entrada
                if(achouCPF){
                    this.elemento[i].apagaElemento(); //seta lapide como true
                    this.elementosCheios = this.elementosCheios -1;
                    return this.elemento[i].getEndRegistro(); //retorna a o endereço apagado (para procurar no arquivo mestre e apagar de la)
                }
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. apagaElemento bucket");
            tt.menu();
        }
        return -1; //caso não tenha encontrado o cpf
    }

    //procura no indice o endereço de um registro no arquivo mestre atraves de seu cpf
    public int comparaElemento(RandomAccessFile file, int cpf)throws Exception{
        try {
            for(int i=0; i<elemento.length; i++){
                boolean achouCPF = this.elemento[i].comparaCPF(cpf);
                if(achouCPF){
                    return this.elemento[i].getEndRegistro();
                }
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. comparaElemento bucket");
            tt.menu();
        }
        return -1; //caso não tenha encontrado o cpf
    }

    public ElementoBucket[] copiaElementos(){
        return this.elemento;
    }

    //le bucket do arquivo
    public void read(RandomAccessFile file) throws Exception{
        try {
            this.pLocal = file.readInt();
            this.elementosCheios = file.readInt();
            for(int i=0; i<elemento.length; i++){
                this.elemento[i].read(file);
            }
        }
        catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. copiaElementos bucket");
            tt.menu();
        }
    }

    //retorna se todas as posições do bucket estão sendo utilizadas
    public boolean isFull(){
        return (this.elementosCheios == this.elemento.length);
    }

}

class ElementoBucket{
  private int idRegistroCpf;
  private int EndRegistro;
  private boolean lapide;

  ElementoBucket(int cpf, int end){
    this.idRegistroCpf = cpf;
    this.EndRegistro = end;
    this.lapide = false;
  }

  ElementoBucket(){}

  public void apagaElemento(){
    this.lapide = true;
  }

  public boolean isApagado(){
      return this.lapide;
  }

  public int getCPF(){
      return this.idRegistroCpf;
  }

  public int getEndRegistro(){
      return this.EndRegistro;
  }

  public void setEndRegistro(int endRegistro) {
      this.EndRegistro = endRegistro;
  }

  public void setLapide(boolean lapide) {
      this.lapide = lapide;
  }

  public void setIdRegistroCpf(int idRegistroCpf) {
      this.idRegistroCpf = idRegistroCpf;
  }

  public void intoString(){
      System.out.println("CPF: "+this.idRegistroCpf+"\nEndereço do Registro: "+ this.EndRegistro+"\nLápide: "+this.lapide);
  }

    //escreve um elemento no arquivo
  public void write(RandomAccessFile file)throws Exception{
      try{
          file.writeBoolean(this.lapide);
          file.writeInt(this.idRegistroCpf);
          file.writeInt(this.EndRegistro);
      }catch(Exception error){
        System.out.println("Ocorreu um erro inesperado. Tente novamente.");
        tt.menu();
      }
  }

    //le um elemento do arquivo
  public void read(RandomAccessFile file)throws Exception{
      try{
            this.lapide = file.readBoolean();
            this.idRegistroCpf = file.readInt();
            this.EndRegistro = file.readInt();
      }catch(Exception error){
        System.out.println("Ocorreu um erro inesperado. Tente novamente.");
        tt.menu();
      }
  }

  public boolean comparaCPF(int cpf){
      if(this.idRegistroCpf== cpf && !this.lapide){
          return true;
      }else{
          return false;
      }
  }

  public void atualizaElemento(int cpf, int end){
      this.lapide = false;
      this.idRegistroCpf = cpf;
      this.EndRegistro = end;
  }

}

class Indice{
    public static Scanner ler = new Scanner(System.in);
    //CABECALHO DO INDICE: 
    private int qtdBucketsTotal; //quantidade de buckets escritos no arquivo (conta tambem os apagados)
    private int nElementosPorBucket;
    //BUCKETS: 
    private Bucket bucket;

    Indice(int n, int pGlobal){
        this.nElementosPorBucket = n;
        this.qtdBucketsTotal = (int)Math.pow(2, pGlobal); //2^pGlobal
    }

    //escreve no arquivo indice o cabecalho e buckets iniciais
    public void writeIndice(int pGlobal)throws Exception{
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try{
            updateCabecalho(arquivoIndice, this.qtdBucketsTotal);
            this.bucket = new Bucket(pGlobal, this.nElementosPorBucket);
            arquivoIndice.seek(8);
            for(int i=0; i<(int)Math.pow(2, pGlobal); i++){
                this.bucket.write(arquivoIndice);
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. writeIndice");
            tt.menu();
        } finally{
            arquivoIndice.close();
        }
    }

    public int getQtdBucketsTotal(){
        return this.qtdBucketsTotal;
    }

    public int getnElementosPorBucket(){
        return this.nElementosPorBucket;
    }

    //le o cabecalho do arquivo
    public void readCabecalho(RandomAccessFile file) throws Exception{
        try{
            file.seek(0);
            this.qtdBucketsTotal = file.readInt();
            this.nElementosPorBucket = file.readInt();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. readCabecalho");
            tt.menu();
        }
    }

    //imprime na tela todo o indice
    public void imprimeIndiceCompleto() throws Exception{
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try{
            readCabecalho(arquivoIndice);
            System.out.println("\nINDICE\nQuantidade total de buckets: " + this.qtdBucketsTotal +
                                "\nQuantidade de elementos por buket: " + this.nElementosPorBucket+"\n");
            for(int i=0; i<this.qtdBucketsTotal; i++){
                this.bucket.read(arquivoIndice);
                this.bucket.intoString(this.nElementosPorBucket);
            }
            System.out.println("***");
            
        }catch(Exception e){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. imprimeIndiceCompleto");
            tt.menu();
        } finally{
            arquivoIndice.close();
        }
    }

    //atualiza o cabecalho no arquivo
    public void updateCabecalho(RandomAccessFile file, int qtdBuckets) throws Exception{
        try{
            file.seek(0);
            this.qtdBucketsTotal = qtdBuckets;
            file.writeInt(this.qtdBucketsTotal);
            file.writeInt(this.nElementosPorBucket);
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. updateCabecalho");
            tt.menu();
        }
    }

    //tenta adicionar um elemento a um bucket
    public int addElemento(int position, int cpf, int end) throws Exception { //recebe em qual bucket (posicao dele no arquivoindice) ficara o cpf
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try {
            arquivoIndice.seek(8 + (position * (8 + (this.nElementosPorBucket * 9))));
            this.bucket.read(arquivoIndice);
            if(!this.bucket.isFull()){
                if(this.bucket.adcionaElemento(cpf, end)){
                    System.out.println("\nElemento adcionado com sucesso");//adciona elemento e reescreve (sobreescreve) o bucket
                    arquivoIndice.seek(8 + (position * (8 + (this.nElementosPorBucket * 9))));
                    this.bucket.write(arquivoIndice);
                }
                else System.out.println("\nErro ao adcionar elemento");
                return -1; //adcionado com sucesso
            }
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. addElemento");
            tt.menu();
        } finally{
            arquivoIndice.close();
        }
        return this.bucket.getPLocal(); //caso o bucket esteja cheio, retorna a pLocal para realizar a divisão de bucket e só depois adicionar
    }

    //retorna vetor dos elementos que estavam já no bucket cheio
    public ElementoBucket[] divideBucket(int position, int pGlobal)throws Exception{ 
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try{
            arquivoIndice.seek(8 + (position * (8 + (this.nElementosPorBucket * 9))));
            this.bucket.read(arquivoIndice);
            ElementoBucket[] elementos = new ElementoBucket[this.nElementosPorBucket];
            elementos = this.bucket.copiaElementos();
            this.bucket = new Bucket(this.bucket.getPLocal()+1, this.nElementosPorBucket); //cria novo bucket (temp) com pLocal aumentada e valores todos nulos (-1)
            arquivoIndice.seek(8 + (position * (8 + (this.nElementosPorBucket * 9))));
            this.bucket.write(arquivoIndice);
            readCabecalho(arquivoIndice); //le cabecalho para não ter erro na atualização
            arquivoIndice.seek(8 + (this.qtdBucketsTotal * (8 + (this.nElementosPorBucket * 9))));
            this.bucket.write(arquivoIndice);
            updateCabecalho(arquivoIndice, this.qtdBucketsTotal+1); //atualiza e escreve cabecalho
            return elementos;
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente. divideBucket");
            tt.menu();
        }
        finally{
            arquivoIndice.close();
        }
        return null;
    }

    //apaga logicamente um elemento (de cpf de entrada) de um bucket (de posição = position)
    public int removeElementoBucket(int position, int cpf) throws Exception{
        int end= -1;
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try {
            arquivoIndice.seek(8 + (position * (8 + (this.nElementosPorBucket * 9)))); //fica no comeco do bucket
            this.bucket.read(arquivoIndice);
            end = this.bucket.apagaElemento(arquivoIndice, cpf);
            if(end != -1){ //se achou o cpf e apagou corretamente
                arquivoIndice.seek(8 + (position * (8 + (this.nElementosPorBucket * 9)))); //fica no comeco do bucket e reescreve
                this.bucket.write(arquivoIndice);
            }
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. removeElementoBucket");
            tt.menu();
        } finally{
            arquivoIndice.close();
        }
        return end; //se não achou o cpf no bucket para apagar
    }

    //retorna o endereço correspondente no arquivo mestre a um cpf dado
    public int getEnderecoPorCPF(int position, int cpf)throws Exception{
        try {
            RandomAccessFile file = new RandomAccessFile("arquivoindice.db", "rw");
            file.seek(8 + (position * (8 + (this.nElementosPorBucket * 9)))); //fica no comeco do bucket
            this.bucket.read(file);
            int end = this.bucket.comparaElemento(file, cpf); //se retornar -1 é que não achou o cpf em nenhum elemento do bucket
            file.close();
            return end;
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. getEnderecoPorCPF");
            tt.menu();
        }
        return -1; //não achou o cpf
    }

    //escreve um bucket no arquivo
    public void writeBucket(int position) throws Exception {
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try {
            arquivoIndice.seek(8 + (position * (8 + (this.nElementosPorBucket * 9))));
            this.bucket.write(arquivoIndice);   
            arquivoIndice.close();                                         
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente. writeBucket");
            tt.menu();
        } finally{
            arquivoIndice.close();
        }
    }

}

class Diretorio{
    //O arquivo Diretório tem os elementos escritos nesta ordem: pGlobal, elemento[].
    private List<Integer> elemento;
    private int pGlobal;
    private int TAM_DIR; //variável não é escrita no arquivo, usada apenas para manipulação

    Diretorio(int profundidade){
        this.pGlobal = profundidade;
        this.TAM_DIR = (int)Math.pow(2, this.pGlobal);
        this.elemento = new ArrayList<Integer>(this.TAM_DIR);
        for(int i=0; i<this.TAM_DIR; i++){
            this.elemento.add(i, i);
        }
    }

    public String toString(){
        String diretorio = "\n*DIRETORIO* \nProfundidade: " + this.pGlobal + "\nElementos: ";
        for(int element: elemento){ 
            diretorio += "\n[" + element + "]";
        }
        diretorio += "\n***";
        return diretorio;
    }

    public int getpGlobal(){
        return this.pGlobal;
    }

    public void atualizaPGlobal(int profundidade)throws Exception{
        try{
            this.pGlobal = profundidade;
            RandomAccessFile arquivoDiretorio = new RandomAccessFile("arquivodiretoio.db", "rw");
            arquivoDiretorio.writeInt(this.pGlobal);
            arquivoDiretorio.close();
        }catch(Exception error){

        }
    }

    //escreve os valores no arquivo Diretorio
    public void write()throws Exception{
        RandomAccessFile arquivoDiretorio = new RandomAccessFile("arquivodiretoio.db", "rw");
        try{
            arquivoDiretorio.writeInt(this.pGlobal);
            Iterator<Integer> iter = elemento.iterator();
            while(iter.hasNext()){
                int elm = iter.next();
                arquivoDiretorio.writeInt(elm);
            }
            arquivoDiretorio.close();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Por favor, tente novamente. write diretorio");
            tt.menu();
        } finally{
            arquivoDiretorio.close();
        }
    }

    //le o diretorio inteiro escrito no arquivo
    public void read()throws Exception{
        RandomAccessFile arquivoDiretorio = new RandomAccessFile("arquivodiretoio.db", "rw");
        try{
            this.pGlobal = arquivoDiretorio.readInt();
            for(int i=0; i<this.TAM_DIR; i++){
                this.elemento.add(i, arquivoDiretorio.readInt());
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Por favor, tente novamente. read diretorio");
            tt.menu();
        } finally{
            arquivoDiretorio.close();
        }
    }

    public void dobraDiretorio(){
        List<Integer> temp = new ArrayList<Integer>(this.TAM_DIR * 2);
        int i=0;
        //copia os elementos na outra metade do diretório criada
        Iterator<Integer> iter = elemento.iterator();
        while(iter.hasNext()){
            int elm = iter.next();
            temp.add(i, elm);
            temp.add(this.TAM_DIR+1, elm);
        }
        this.elemento = temp;
        this.TAM_DIR = this.TAM_DIR * 2;
        this.pGlobal = this.pGlobal + 1;
    }

    //altera os valores no diretório no padrão: um sim um não, explicado
    public void mudaCorrespondente2(int key, int novoEndereco){
        boolean altera = false;
        Iterator<Integer> iter = elemento.iterator();
        while(iter.hasNext()){
            int elm = iter.next();
            if(elm == key){
                if(altera){
                    elm = novoEndereco;
                    altera = false;
                }else{
                    altera = true;
                }
            }
        }
        try{
            write();
        }catch(Exception e){

        }
    }

    //faz a posicao correspondente ao novo bucket guardar o endereco dele
    public void mudaCorrespondente(int key, int novoEndereco){
        this.elemento.set(this.TAM_DIR/2 + key, novoEndereco);
        try{
            write();
        }catch(Exception e){

        }
    }

    //usado apenas para realocar elementos depois de dividir um bucket (calcula a key pela profundidade Local recebida por parâmetro)
    public int getEnderecoCorrespondente(int cpf, int pLocal){
        int key = cpf%(int)Math.pow(2, pLocal);
        return elemento.get(key);
    }

    //retorna o endereço do índice correspondente para guardar/em que o cpf está guardado
    public int getEnderecoCorrespondente(int cpf){
        int key = hashingKey(cpf);
        return elemento.get(key);
    }

    //calcula a hashing key pelo cpf (retorna uma posição do diretório)
    public int hashingKey(int cpf){
        return cpf%this.TAM_DIR;
    }

    public void imprimeDiretorioCompleto()throws Exception{
        this.read();
        System.out.println(this.toString());
    }
}
