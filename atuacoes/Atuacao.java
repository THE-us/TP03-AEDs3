package atuacoes;

import aeds3.*;
import atores.*;
import series.*;
import episodios.*;

import aeds3.EntidadeArquivo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Atuacao implements EntidadeArquivo {

    private int id;  
    private String papel;
    private int tempoTela;
    private int id_serie;    
    private int id_ator;

    public Atuacao () throws Exception  {
        this (-1, "", -1, -1, -1);
    }

    public Atuacao (String papel, int tempoTela, int id_serie, int id_ator) throws Exception {
        this (-1, papel, tempoTela, id_serie, id_ator);
    }

    public Atuacao (int id, String papel, int tempoTela, int id_serie, int id_ator) throws Exception {
        this.id = id;
        this.papel = papel;
        this.tempoTela = tempoTela;
        this.id_serie = id_serie;
        this.id_ator = id_ator;
    } 

    public int getID () {
        return id;
    }

    public void setID (int id) {
        this.id = id;
    }

    public String getPapel () {
        return papel;
    }

    public void setPapel (String papel) {
        this.papel = papel;
    }

    public int getTempoTela () {
        return tempoTela;
    }

    public void setTempoTela (int tempoTela) {
        this.tempoTela = tempoTela;
    }

    public int getIdSerie () {
        return id_serie;
    }

    public void setIdAtor (int id_ator) {
        this.id_ator = id_ator;
    }

    public int getIdAtor (){
        return id_ator;
    }

    public byte[] toByteArray () throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        DataOutputStream dos = new DataOutputStream (baos);
        
        dos.writeInt (id);
        dos.writeUTF (papel);
        dos.writeInt (tempoTela);
        dos.writeInt (id_serie);
        dos.writeInt (id_ator);
        
        return baos.toByteArray ();
    }

    public void fromByteArray (byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream (vb);
        DataInputStream dis = new DataInputStream (bais);
    
        id = dis.readInt ();
        papel = dis.readUTF ();
        tempoTela = dis.readInt ();
        id_serie = dis.readInt ();
        id_ator = dis.readInt ();
    }

    public String toString (){
        return "Atuacao = [Papel: " + papel +
                "\nTempo de Tela: " + tempoTela + 
                "\nid_serie: " + id_serie +
                "\nid_ator: " + id_ator + "]";
    }

    @Override
 	public boolean equals (Object obj){
 		return (this.getID () == ( (Atuacao) obj).getID ());
 	}

     public int compareTo (Atuacao atuacao) {
        return Integer.compare (this.id, atuacao.id);
    }
}
