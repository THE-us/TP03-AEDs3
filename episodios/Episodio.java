package episodios;

import aeds3.*;
import atores.*;
import atuacoes.*;
import episodios.*;


import aeds3.EntidadeArquivo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;

public class Episodio implements EntidadeArquivo {

  private int id;                   // ID da série
  private String nome;              // Nome da série
  private short temporada;          // Número da temporada
  private LocalDate dataLancamento; // Data de lançamento
  private int duracaoMinutos;       // Duração em minutos
  private float avaliacao;          // Avaliação do episodio
  private boolean especial;         // Episodio especial?
  private String descricao;         // Descrição do episodio
  private int id_serie;             // ID da série
  
  public Episodio () throws Exception{
    this ("", 0, LocalDate.now (), 0, 0F, false, "", -1);
  }
  
  public Episodio (String nome, int temporada, LocalDate dataLancamento, int duracaoMinutos, float avaliacao, boolean especial, String descricao, int id_serie) throws Exception{
    this (-1 , nome, temporada, dataLancamento, duracaoMinutos, avaliacao, especial, descricao, id_serie);
  }

  public Episodio (int id, String nome, int temporada, LocalDate dataLancamento, int duracaoMinutos, float avaliacao, boolean especial, String descricao, int id_serie) throws Exception{
    this.id = id;
    this.nome = nome;
    this.temporada = (short) temporada;
    this.dataLancamento = dataLancamento;
    this.duracaoMinutos = duracaoMinutos; 
    this.avaliacao = avaliacao;
    this.especial = especial;
    this.descricao = descricao;
    this.id_serie = id_serie; 
  }

  public int getID (){
    return this.id;
  }

  public void setID (int id){
    this.id = id;
  }

  public int getID_serie (){
    return this.id_serie;
  }

  public void setID_serie (int id_serie){
    this.id_serie = id_serie;
  }

  public String getNome () {
    return nome;
  }

  public void setNome (String nome) {
    this.nome = nome;
  }

  public int getTemporada () {
    return temporada;
  }

  public void setTemporada (int temporada) {
    this.temporada = (short) temporada;
  }

  public LocalDate getDataLancamento () {
    return dataLancamento;
  }

  public void setDataLancamento (LocalDate dataLancamento) {
    this.dataLancamento = dataLancamento;
  }

  public int getDuracaoMinutos () {
    return duracaoMinutos;
  }

  public void setDuracaoMinutos (int duracaoMinutos) {
    this.duracaoMinutos = duracaoMinutos;
  }

  public float getAvaliacao () {
    return avaliacao;
  }

  public void setAvaliacao (float avaliacao) {
    this.avaliacao = avaliacao;
  }

  public boolean isEspecial () {
    return especial;
  }

  public void setEspecial (boolean especial) {
    this.especial = especial;
  }

  public String getDescricao () {
    return descricao;
  }

  public void setDescricao (String descricao) {
    this.descricao = descricao;
  }

  public byte[] toByteArray () throws Exception{
    ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    DataOutputStream dos = new DataOutputStream (baos);

    dos.writeInt (id);
    dos.writeUTF (nome);
    dos.writeShort (temporada);
    dos.writeInt ( (int) dataLancamento.toEpochDay ());
    dos.writeInt (duracaoMinutos);
    dos.writeFloat (avaliacao);
    dos.writeBoolean (especial);
    dos.writeUTF (descricao);
    dos.writeInt (id_serie);

    return baos.toByteArray ();
  }

  public void fromByteArray (byte[] vb) throws Exception{
    ByteArrayInputStream bais = new ByteArrayInputStream (vb);
    DataInputStream dis = new DataInputStream (bais);

    id = dis.readInt ();
    nome = dis.readUTF ();
    temporada = dis.readShort ();
    dataLancamento = LocalDate.ofEpochDay (dis.readInt ());
    duracaoMinutos = dis.readInt ();
    avaliacao = dis.readFloat ();
    especial = dis.readBoolean ();
    descricao = dis.readUTF ();
    id_serie = dis.readInt ();
  }

  public String toString (){
    return "Episodio = [ID: " + id +
            "\nNome: " + nome +
            "\nTemporada: " + temporada +
            "\nData de lançamento: " + dataLancamento +
            "\nDuração: " + duracaoMinutos + 
            "\nAvaliação: " + avaliacao + 
            "\nEspecial: " + especial + 
            "\nDescrição: " + descricao + 
            "\nSerie: " + id_serie +  "]";
  }


  @Override
	public boolean equals (Object obj){
		return (this.getID () == ( (Episodio) obj).getID ());
	}

  // @Override
  public int compareTo (Episodio outro) {
      return Integer.compare (this.id, outro.id);
  }
}
