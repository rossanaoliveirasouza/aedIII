import java.io.RandomAccessFile;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.lang.model.element.Element;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.Math;

public class MainTeste{

    //Variável auxiliar da classe scanner
    public static Scanner ler = new Scanner(System.in);
    //Objetos das classes que serão utilizados no programa principal
    public static Prontuario prontuario;
    public static Indice indice;
    public static Diretorio diretorio;
    public static int pGlobal = 1;

    public static void main(String[] args) throws Exception{
       menu();
    }

    public static void menu() throws Exception{
        int opcao = 1;
        while (opcao > 0) {
            System.out.println(
                    "\n\n--MENU DE OPÇÕES--\n1 - Criar arquivo\n2 - Inserir registro\n3 - Editar registro\n4 - Remover registro\n5 - Imprimir arquivos\n6 - Simulação\n\nOPÇÃO: ");
            opcao = ler.nextInt();
            if (opcao == 1) {
                opcao1();
            } else if (opcao == 2) {
                opcao2();
            } else if (opcao == 3) {
                opcao3();
            } else if (opcao == 4) {
                opcao4();
            } else if (opcao == 5) {
                opcao5();
            }
        }
    }
    //Cria cada um dos 3 arquivos, arquivodiretorio, arquivomestre e arquivoindice e escreve os valores iniciais nos cabeçalhos de 
    public static void opcao1() throws Exception {
        try{
            //DIRETORIO//
            System.out.println(
                    "\n-- CRIANDO ARQUIVO DIRETORIO --\nFavor inserir valor para a profundidade global: ");
            diretorio = new Diretorio(ler.nextInt());
            diretorio.write();

            //INDICE//
            System.out.println(
                    "\n-- CRIANDO ARQUIVO INDICE --\nFavor inserir valor para o número de entradas em cada Bucket: ");
            indice = new Indice(ler.nextInt(), diretorio.getpGlobal());
            indice.writeIndice(diretorio.getpGlobal());
            
            //PRONTUARIO//
            System.out.println(
                    "\n-- CRIANDO ARQUIVO MESTRE --\nFavor inserir valor para o tamanho possível para escrita das observações: ");
            prontuario = new Prontuario(ler.nextInt()); //ja escreve o cabecalho do arquivo no seu proprio construtor
        }catch(Exception e){
            //não consegui tratar o erro aqui, tava dando loop infinito
        }
    }
    //Insere um novo prontuário
    public static void opcao2() throws Exception {
        //TESTE DE ADCIONAR E REMOVER DO INDICE
        /*System.out.println("primeiro add: ");
        int op = ler.nextInt();
        indice.addElemento(0, 999999999, 3);
        System.out.println("segundo add: ");
        op = ler.nextInt();
        indice.addElemento(0, 4, 5);
        System.out.println("terceiro add: ");
        op = ler.nextInt();
        indice.addElemento(1, 2, 6);
        System.out.println("primeiro remove: ");
        op = ler.nextInt();
        indice.removeElementoBucket(0, 4);
        System.out.println("segundo remove: ");
        op = ler.nextInt();
        indice.removeElementoBucket(1, 2);
        System.out.println("quarto add: ");
        op = ler.nextInt();
        indice.addElemento(0, 4, 5);*/

        System.out.println("\n-- ACESSANDO OPÇÃO 2 --");
        Registro registro = recebeDadosInformados();
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        int endereco = prontuario.write(arquivoMestre, registro); //retorna endereco para inserir ele no bucket com o cpf
        int key = diretorio.hashingKey(registro.getidRegistroCpf());
        int pLocalBucket = indice.addElemento(key, registro.getidRegistroCpf(), endereco);
        if(pLocalBucket != -1){ //se for verdade vai ter que dividir o bucket
            if(pLocalBucket == diretorio.getpGlobal()){
                ElementoBucket[] elementos = indice.divideBucket(key, diretorio.getpGlobal()+1);
                diretorio.dobraDiretorio();
                diretorio.mudaCorrespondente(key, indice.getQtdBucketsTotal()-1); //faz a posicao correspondente ao novo bucket guardar o endereco dele
                diretorio.write();
                indice.addElemento(key, registro.getidRegistroCpf(), endereco);
                for(ElementoBucket element: elementos){
                    key = diretorio.hashingKey(element.getCPF());
                    indice.addElemento(key, element.getCPF(), element.getEndRegistro());
                }

            }else{
                ElementoBucket[] elementos = indice.divideBucket(key, diretorio.getpGlobal());
                indice.addElemento(key, registro.getidRegistroCpf(), endereco);
                for(ElementoBucket element: elementos){
                    key = diretorio.hashingKey(element.getCPF());
                    indice.addElemento(key, element.getCPF(), element.getEndRegistro());
                }
            }
        }
    }

    public static void opcao3() throws Exception {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        System.out.println("\n-- ACESSANDO OPÇÃO 3 --\nDigite o cpf do registro que deseja atualizar: ");
        int cpf = ler.nextInt();
        int positionNoIndice = diretorio.getEnderecoCorrespondente(cpf);
        System.out.println("\n\npositionNoIndice = "+ positionNoIndice);
        int positionNoArqMestre = indice.getEnderecoPorCPF(arquivoMestre, positionNoIndice, cpf); //procurar no arq de indice, na position passada (positionNoIndice) o elemento cujo cpf == ao cpf de entrada e retornar a position no arqMestre
        System.out.println("\npositionNoArqMestre = "+ positionNoArqMestre);
        if(positionNoArqMestre != -1){ //se pegou a posicao certinho
            prontuario.readRegistro(arquivoMestre, positionNoArqMestre);
            System.out.println("\nConfira os dados atuais do registro informado: ");
            prontuario.imprimeProntuario();
            System.out.println("\nInforme os novos dados: ");
            Registro registro = recebeDadosInformadosSemCPF(cpf); 
            prontuario.writeRegistro(arquivoMestre, positionNoArqMestre, registro); 
        }else System.out.println("Registro não encontrado");
        arquivoMestre.close();
    }

    public static void opcao4() throws Exception {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        System.out.println("\n-- ACESSANDO OPÇÃO 3 --\nDigite o cpf do registro que deseja remover: ");
        int registro = ler.nextInt();
        System.out.println("\n-- O Registro abaixo será definitivamente excluído: \n");
        prontuario.readRegistro(arquivoMestre, registro);
        prontuario.imprimeProntuario();
        System.out.println("\n-- 1- Confirmar ação: \n-- 2- Voltar ao menu inicial");
        int confirmacao = ler.nextInt();

        if(confirmacao == 1){
            prontuario.apagaRegistro(arquivoMestre, registro);
            System.out.println("\nRegistro apagado com sucesso\n");
        }
        arquivoMestre.close();
    }

    public static void opcao5() throws Exception {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        System.out.print("\n-- ACESSANDO OPÇÃO 5 --\nDeseja recuperar um registro expecífico, ou todos os registros do sistema? (1 - apenas um 2 - todos)\n");
        int op = ler.nextInt();
        if(op==1){
            System.out.print("\nDigite o número do registro que deseja recuperar: ");
            int registro = ler.nextInt();
            prontuario.readRegistro(arquivoMestre, registro);
            prontuario.imprimeProntuario();
        }else if(op==2){
            prontuario.readProntuario(arquivoMestre);
            prontuario.imprimeProntuario();
        }
        arquivoMestre.close();
    }

    public static Registro recebeDadosInformados() throws IOException {
        int respostaCpf=0;
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
        try{
            System.out.println("CPF: ");
            respostaCpf = ler.nextInt();
        }catch(InputMismatchException e){
            System.out.println("O valor informado não é um valor válido.\nPor gentileza, insira os valores conforme solicitado:");
            recebeDadosInformados();
        }
        System.out.println("Anotações sobre o paciente: ");
        String respostaObservacoes = ler.nextLine();
        respostaObservacoes = ler.nextLine();
        int tam = getTamAnotacoesNoArquivo(); // le do arquivo o tamanho proposto para as anotacoes do medico

        // trata anotacoes com mais caracteres que o permitido
        while (respostaObservacoes.length() > tam) {
            System.out.print("Favor digitar anotação de, no maximo, " + tam + " caracteres: ");
            respostaObservacoes = ler.nextLine();
        }
        Registro registro = new Registro(respostaCpf, respostaNome, respostaSexo, respostaDataNascimento,
                respostaObservacoes);
        return registro;
    }

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
        respostaObservacoes = ler.nextLine();
        int tam = getTamAnotacoesNoArquivo(); // le do arquivo o tamanho proposto para as anotacoes do medico

        // trata anotacoes com mais caracteres que o permitido
        while (respostaObservacoes.length() > tam) {
            System.out.print("Favor digitar anotação de, no maximo, " + tam + " caracteres: ");
            respostaObservacoes = ler.nextLine();
        }
        Registro registro = new Registro(cpf, respostaNome, respostaSexo, respostaDataNascimento,
                respostaObservacoes);
        return registro;
    }

    public static int getTamAnotacoesNoArquivo() throws IOException {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        try {
            arquivoMestre.seek(4); // pula o escrito referente ao tamanho ocupado pelo arquivo fora as anotações
                                   // (TAM_TC)
            int tam = arquivoMestre.readInt();
            arquivoMestre.close();
            return tam;
        } catch (IOException e) {
            return 0;
        } finally{
            arquivoMestre.close();
        }
    }


}

class Cabecalho{
    private final int TAM_TC = 43;
    private int TAM_OBSERVACAO; //vindo por parametro
    private int qtdRegistrosEscritos;

    Cabecalho(int tamObs)throws Exception{
        this.TAM_OBSERVACAO = tamObs;
        this.qtdRegistrosEscritos = 0;
        try{
            write();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
    }

    public void updateCabecalho(int qtdRegistros)throws Exception{
        this.qtdRegistrosEscritos = qtdRegistros;
        try{
            write();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
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

    public void write()throws Exception {
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        try {
            arquivoMestre.seek(0);
            arquivoMestre.writeInt(this.TAM_TC);
            arquivoMestre.writeInt(this.TAM_OBSERVACAO);
            arquivoMestre.writeInt(this.qtdRegistrosEscritos);
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        } finally{
            arquivoMestre.close();
        }
    }

    public void read() throws Exception{
        RandomAccessFile arquivoMestre = new RandomAccessFile("arquivomestre.db", "rw");
        try{
            arquivoMestre.seek(4);
            this.TAM_OBSERVACAO = arquivoMestre.readInt();
            this.qtdRegistrosEscritos = arquivoMestre.readInt();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }finally{
            arquivoMestre.close();
        }
    }


}

class Registro {
    private boolean lapide; // 0 (false) =sem lapide 1 (true) =com lapide (deletado)
    private int idRegistroCpf; // cpf de 0 a 99999999999
    private String nome; // apenas primeiro nome (maximo 22 bytes = 2 bytes para escrever o tamanho
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

    public void read(RandomAccessFile file, int tamTC, int tamOBS) throws Exception {
        try {
            byte[] array = new byte[tamTC + tamOBS];
            file.read(array);
            fromByteArray(array);
        } catch (IOException error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
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

    /*public void removeRegistro(RandomAccessFile file, int position) throws IOException {
        try{
            file.seek(12 + 2 +(position * (Main.TAM_TC + Main.TAM_OBSERVACAO))); // coloca na posição da lápide
            int lapide = 1;
            file.writeInt(lapide);
        }catch(IOException e){

        }
    }*/

}

class Prontuario{
    private Cabecalho cabecalho;
    private Registro[] registro;

    Prontuario(int tamObs)throws Exception{
        try{
            this.cabecalho = new Cabecalho(tamObs);
        }catch(Exception error){
            //tratar erro
        }
    }

    public int write(RandomAccessFile file, Registro registro)throws Exception{
        try{
            file.seek(0);
            this.cabecalho.read();
            for(int i=0; i<this.cabecalho.getqtdRegistrosEscritos(); i++){
                file.seek(12 + (i * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO())));
                this.registro[0].read(file, this.cabecalho.getTAM_TC(), this.cabecalho.getTAM_OBSERVACAO());
                System.out.println(this.registro[0].toString());
                if(this.registro[0].isApagado()){
                    return writeRegistro(file, i, registro);
                }
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            System.out.println(error);
            MainTeste.menu();
        }
        this.cabecalho.updateCabecalho(this.cabecalho.getqtdRegistrosEscritos() + 1);
        return writeRegistro(file, this.cabecalho.getqtdRegistrosEscritos()-1, registro);
    }

    public int writeRegistro(RandomAccessFile file, int position, Registro registro) throws Exception {
        try {
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO())));                                                  
            registro.write(file);
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
        return position;
    }

    public void readRegistro(RandomAccessFile file, int position) throws Exception {
        try {
            this.registro = new Registro[1];
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO())));
            this.registro[0] = new Registro();
            this.registro[0].read(file, this.cabecalho.getTAM_TC(),this.cabecalho.getTAM_OBSERVACAO());
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
    }

    public void readProntuario(RandomAccessFile file) throws Exception {
        try {
            this.cabecalho.read();
            this.registro = new Registro[this.cabecalho.getqtdRegistrosEscritos()];
            for(int i=0; i<this.cabecalho.getqtdRegistrosEscritos(); i++){
                file.seek(12 + (i * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO())));
                this.registro[i] = new Registro(file.readBoolean(), file.readInt(), file.readUTF(), file.readChar(), file.readUTF(), file.readUTF());
            }
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
    }

    public void apagaRegistro(RandomAccessFile file, int position) throws Exception {
        try{
            file.seek(0);
            this.cabecalho.read();
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO()))); // coloca na posição da lápide
            this.registro[0].read(file, this.cabecalho.getTAM_TC(), this.cabecalho.getTAM_OBSERVACAO());
            this.registro[0].apagaRegistro();
            file.seek(12 + (position * (this.cabecalho.getTAM_TC() + this.cabecalho.getTAM_OBSERVACAO()))); // coloca na posição da lápide
            this.registro[0].write(file);
        }catch(Exception e){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
    }

    public void imprimeProntuario(){
        for(int i=0; i<this.registro.length; i++){
            if(!this.registro[i].isApagado()){
                System.out.println(this.registro[i].toString());
            }
        }
    }

}

class Bucket{
    private int pLocal;
    private ElementoBucket[] elemento;

    Bucket(int p, int nEntradas){
        this.pLocal = p;
        this.elemento = new ElementoBucket[nEntradas];
        for(int i=0; i<nEntradas; i++){
          this.elemento[i] = new ElementoBucket(-1, -1);
        }
    }

    public int getPLocal(){
        return this.pLocal;
    }

    public void setPLocal(int p){
        this.pLocal = p;
    }

    public void intoString(int nEntradas){
        System.out.println("BUCKET: pLocal = "+this.pLocal);
        for(int i=0; i<nEntradas; i++){
            System.out.println("\nelemento "+i+":");
            this.elemento[i].intoString();
        }
    }

    public void write(RandomAccessFile file) throws Exception{
        try {
            file.writeInt(this.pLocal);
            for(int i=0; i<elemento.length; i++){
                this.elemento[i].write(file);
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
    }

    public boolean adcionaElemento(int cpf, int end){
        for(int i=0; i<elemento.length; i++){
            if((elemento[i].getCPF()== -1) || elemento[i].isApagado()){
                elemento[i].atualizaElemento(cpf, end);
                return true;
            }
        }
        return false;
    }

    public int apagaElemento(RandomAccessFile file, int cpf)throws Exception{
        try {
            for(int i=0; i<elemento.length; i++){
                boolean achouCPF = this.elemento[i].comparaCPF(cpf);
                if(achouCPF){
                    this.elemento[i].apagaElemento();
                    return this.elemento[i].getEndRegistro();
                }
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
        return -1;
    }

    public int comparaElemento(RandomAccessFile file, int cpf)throws Exception{
        try {
            for(int i=0; i<elemento.length; i++){
                boolean achouCPF = this.elemento[i].comparaCPF(cpf);
                if(achouCPF){
                    return this.elemento[i].getEndRegistro();
                }
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
        return -1;
    }

    public ElementoBucket[] copiaElementos(){
        return this.elemento;
    }

    /*public void setLapideTodosElementos(){
        for(ElementoBucket element: this.elemento){
            element.apagaElemento();
        }
    }*/

    public void read(RandomAccessFile file) throws Exception{
        try {
            this.pLocal = file.readInt();
            for(int i=0; i<elemento.length; i++){
                this.elemento[i].read(file);
            }
        }
        catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
    }

    public boolean isFull(){
        for(int i=0; i<elemento.length; i++){
            if((elemento[i].getCPF()== -1) || elemento[i].isApagado()){
                return false;
            }
        }
        return true;
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
      EndRegistro = endRegistro;
  }

  public void setLapide(boolean lapide) {
      this.lapide = lapide;
  }

  public void setIdRegistroCpf(int idRegistroCpf) {
      this.idRegistroCpf = idRegistroCpf;
  }

  public void intoString(){
      System.out.println("IdRegistroCpf = "+this.idRegistroCpf+"\nEndRegistro = "+ this.EndRegistro+"\nlapide = "+this.lapide);
  }

  public void write(RandomAccessFile file)throws Exception{
      try{
          file.writeBoolean(this.lapide);
          file.writeInt(this.idRegistroCpf);
          file.writeInt(this.EndRegistro);
      }catch(Exception error){
        System.out.println("Ocorreu um erro inesperado. Tente novamente.");
        MainTeste.menu();
      }
  }

  public void read(RandomAccessFile file)throws Exception{
      try{
            this.lapide = file.readBoolean();
            this.idRegistroCpf = file.readInt();
            this.EndRegistro = file.readInt();
      }catch(Exception error){
        System.out.println("Ocorreu um erro inesperado. Tente novamente.");
        MainTeste.menu();
      }
  }

  public boolean comparaCPF(int cpf){
      if(this.idRegistroCpf== cpf && !this.lapide){
          System.out.println("ACHOU! CPF = "+ this.idRegistroCpf + " End = "+ this.EndRegistro);
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
    private int qtdBucketsTotal;
    private int nElementosPorBucket;
    private Bucket bucket;

    Indice(int n, int pGlobal){
        this.nElementosPorBucket = n;
        this.qtdBucketsTotal = (int)Math.pow(2, pGlobal);
        //this.bucket = new Bucket(int)Math.pow(2, pGlobal);
    }

    public void writeIndice(int pGlobal)throws Exception{
        long[] enderecos = new long[100];
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try{
            updateCabecalho(arquivoIndice, this.qtdBucketsTotal);
            arquivoIndice.seek(8);
            for(int i=0; i<(int)Math.pow(2, pGlobal); i++){
                enderecos[i] = arquivoIndice.getFilePointer();
                //System.out.println("End "+i+" = " + enderecos[i]);
                this.bucket = new Bucket(pGlobal, this.nElementosPorBucket);
                this.bucket.write(arquivoIndice);
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
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

    public void readCabecalho(RandomAccessFile file) throws Exception{
        try{
            file.seek(0);
            this.qtdBucketsTotal = file.readInt();
            this.nElementosPorBucket = file.readInt();
        }catch(Exception error){

        }
    }

    public void updateCabecalho(RandomAccessFile file, int qtdBuckets) throws Exception{
        try{
            file.seek(0);
            this.qtdBucketsTotal = qtdBuckets;
            file.writeInt(this.qtdBucketsTotal);
            file.writeInt(this.nElementosPorBucket);
        }catch(Exception error){
        }
    }

    public Bucket criaBucket(int profundidade){
        return new Bucket(profundidade, this.nElementosPorBucket);
    }

    public int addElemento(int position, int cpf, int end) throws Exception{ //recebe em qual bucket (posicao dele no arquivoindice) ficara o cpf
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try {
            arquivoIndice.seek(8 + (position * (this.nElementosPorBucket * 10)));
            this.bucket.read(arquivoIndice);
            if(!this.bucket.isFull()){
                if(this.bucket.adcionaElemento(cpf, end)){
                    System.out.println("\nElemento adcionado com sucesso");//adciona elemento e escreve o bucket
                    arquivoIndice.seek(8 + (position * (this.nElementosPorBucket * 10)));
                    this.bucket.write(arquivoIndice);
                }
                else System.out.println("\nErro ao adcionar elemento");
                return -1;
            }
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        } finally{
            arquivoIndice.close();
        }
        return this.bucket.getPLocal();
    }

    public ElementoBucket[] divideBucket(int position, int pGlobal)throws Exception{ //tem que retornar a position do novo bucket
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try{
            arquivoIndice.seek(8 + (position * (this.nElementosPorBucket * 10)));
            this.bucket.read(arquivoIndice);
            ElementoBucket[] elementos = new ElementoBucket[this.nElementosPorBucket];
            elementos = this.bucket.copiaElementos();
            //this.bucket.setLapideTodosElementos();
            this.bucket = new Bucket(this.bucket.getPLocal()+1, this.nElementosPorBucket);
            arquivoIndice.seek(8 + (position * (this.nElementosPorBucket * 10)));
            this.bucket.write(arquivoIndice);
            readCabecalho(arquivoIndice);
            arquivoIndice.seek(8 + (this.qtdBucketsTotal * (this.nElementosPorBucket * 10)));
            this.bucket.write(arquivoIndice);
            updateCabecalho(arquivoIndice, this.qtdBucketsTotal+1);
            return elementos;
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
        finally{
            arquivoIndice.close();
        }
        return null;
        /*RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try{
            arquivoIndice.seek(8 + (position * (this.nElementosPorBucket * 10)));
            this.bucket.read(arquivoIndice);
            //Bucket bucket2 = new Bucket(this.bucket.getPLocal() + 1, this.nElementosPorBucket);
            this.bucket.setPLocal(this.bucket.getPLocal()+1);

            Bucket bucketAux = new Bucket(this.bucket.getPLocal()+1, this.nElementosPorBucket);
            bucketAux.realocaElementosBucket(pGlobal, bucket);
            //this.bucket = bucketAux;
            writeBucket(position);
            bucketAux.realocaElementosBucket(pGlobal, bucket);
            this.bucket = bucketAux;
            this.qtdBucketsTotal++;
            writeBucket(this.qtdBucketsTotal);
            updateCabecalho(arquivoIndice, this.qtdBucketsTotal);
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
        finally{
            arquivoIndice.close();
        }*/
        //return qtdBucketsTotal;
    }

    public int removeElementoBucket(int position, int cpf) throws Exception{
        int end= -1;
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try {
            arquivoIndice.seek(8 + (position * (this.nElementosPorBucket * 10))); //fica no comeco do bucket
            this.bucket.read(arquivoIndice);
            end = this.bucket.apagaElemento(arquivoIndice, cpf);
            if(end != -1){
                arquivoIndice.seek(8 + (position * (this.nElementosPorBucket * 10))); //fica no comeco do bucket
                this.bucket.write(arquivoIndice);
            }
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        } finally{
            arquivoIndice.close();
        }
        return end;
    }

    public int getEnderecoPorCPF(RandomAccessFile file, int position, int cpf)throws Exception{
        try {
            file.seek(8 + (position * (this.nElementosPorBucket * 10))); //fica no comeco do bucket
            this.bucket.read(file);
            this.bucket.intoString(this.nElementosPorBucket);
            int end = this.bucket.comparaElemento(file, cpf); //se retornar -1 é que não achou o cpf em nenhum elemento do bucket
            return end;
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        }
        return -1;
    }

    public void writeBucket(int position) throws Exception {
        RandomAccessFile arquivoIndice = new RandomAccessFile("arquivoindice.db", "rw");
        try {
            arquivoIndice.seek(8 + (position * ((this.nElementosPorBucket * 10)))); // 4 +  and   9->10
            this.bucket.write(arquivoIndice);   
            arquivoIndice.close();                                         
        } catch (Exception error) {
            System.out.println("Ocorreu um erro inesperado. Tente novamente.");
            MainTeste.menu();
        } finally{
            arquivoIndice.close();
        }
    }

}

class Diretorio{
    private int[] elemento;
    private int pGlobal;
    private int TAM_DIR;

    Diretorio(int profundidade){
        this.pGlobal = profundidade;
        this.TAM_DIR = (int)Math.pow(2, this.pGlobal);
        this.elemento = new int [this.TAM_DIR];
        for(int i=0; i<elemento.length; i++){
            this.elemento[i] = i;
        }
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

    public void write()throws Exception{
        RandomAccessFile arquivoDiretorio = new RandomAccessFile("arquivodiretoio.db", "rw");
        try{
            arquivoDiretorio.writeInt(this.pGlobal);
            for(int i=0; i<this.elemento.length; i++){
                arquivoDiretorio.writeInt(this.elemento[i]);
            }
            arquivoDiretorio.close();
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Por favor, tente novamente.");
            MainTeste.menu();
        } finally{
            arquivoDiretorio.close();
        }
    }

    public void read()throws Exception{
        RandomAccessFile arquivoDiretorio = new RandomAccessFile("arquivodiretoio.db", "rw");
        try{
            this.pGlobal = arquivoDiretorio.readInt();
            for(int i=0; i<this.TAM_DIR; i++){
                this.elemento[i] = arquivoDiretorio.readInt();
            }
        }catch(Exception error){
            System.out.println("Ocorreu um erro inesperado. Por favor, tente novamente.");
            MainTeste.menu();
        } finally{
            arquivoDiretorio.close();
        }
    }

    public void dobraDiretorio(){
        int[] temp = new int[this.TAM_DIR * 2];
        for(int i=0; i<this.TAM_DIR; i++){
            temp[i] = this.elemento[i];
            temp[this.TAM_DIR + i] = this.elemento[i];
        }
        this.elemento = temp;
        this.TAM_DIR = this.TAM_DIR * 2;
        this.pGlobal = this.pGlobal + 1;
    }

    public void mudaCorrespondente(int key, int novoEndereco){
        this.elemento[this.TAM_DIR/2 + key] = novoEndereco;
    }

    public int getEnderecoCorrespondente(int cpf){
        int key = hashingKey(cpf);
        return elemento[key];
    }

    public int hashingKey(int cpf){
        return cpf%this.TAM_DIR;
    }


}