//////////////////////////////////////////////////
// PACOTE

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS


//////////////////////////////////////////////////
// CLASSE MENUSEPISODIOS EM SI

package atores;

import aeds3.*;
import atuacoes.*;
import series.*;
import episodios.*;

import aeds3.EntidadeArquivo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;

public class Ator implements EntidadeArquivo {

    private int id;  
    private String nome;
    private LocalDate dataNasc;


    public Ator () throws Exception  {
        this (-1, "", LocalDate.now ());
    }

    public Ator (String nome, LocalDate dataNasc) throws Exception {
        this (-1, nome, dataNasc);
    }

    public Ator (int id, String nome, LocalDate datasNasc) throws Exception {
        this.id = id;
        this.nome = nome;
        this.dataNasc = datasNasc;
    } 

    public int getID () {
        return id;
    }

    public void setID (int id) {
        this.id = id;
    }

    public String getNome () {
        return nome;
    }

    public void setNome (String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNasc () {
        return dataNasc;
    }

    public void setDataNasc (LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }


    public byte[] toByteArray () throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        DataOutputStream dos = new DataOutputStream (baos);
        
        dos.writeInt (id);
        dos.writeUTF (nome);
        dos.writeInt ( (int)dataNasc.toEpochDay ());
        
        return baos.toByteArray ();
    }

    public void fromByteArray (byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream (vb);
        DataInputStream dis = new DataInputStream (bais);
    
        id = dis.readInt ();
        nome = dis.readUTF ();
        dataNasc = LocalDate.ofEpochDay (dis.readInt ());

    }

    public String toString (){
        return "Ator = [ID: " + id +
                "\nNome: " + nome +
                "\nData de Nascimento: " + dataNasc + 
                "]";
    }

    @Override
 	public boolean equals (Object obj){
 		return (this.getID () == ( (Atuacao) obj).getID ());
 	}

     public int compareTo (Ator ator) {
        return Integer.compare (this.id, ator.id);
    }
}
