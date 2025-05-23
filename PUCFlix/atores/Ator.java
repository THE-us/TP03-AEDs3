//////////////////////////////////////////////////
// PACOTE

package atores;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
//import java.time.LocalDate;

//import episodios.*;

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS

import aeds3.EntidadeArquivo;

//////////////////////////////////////////////////
// IMPLEMENTAÇÃO DE ATOR

public class Ator implements EntidadeArquivo 
{
    // VARIÁVEIS
    private String        nome;
    private int             id;

    // CONSTRUTORES
    public Ator ()
    {
        this ("");
    }

    public Ator (String nome)
    {
        this (-1, nome);
    }

    public Ator (int id, String nome)
    {
        this.nome = nome;
        this.id   =   id;
    }

    // [getters]
    public String   getNome () {return  nome;}
    public int      getID   () {return    id;}

    // [setters]
    public void     setNome (String   nome) {this.nome = nome;}
    public void     setID   (int        id) {this.id   =   id;}

    // to & from byte array
    public byte [] toByteArray () throws Exception
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream ();
      DataOutputStream      dos  = new DataOutputStream  (baos);
  
      dos.writeInt(id);
      dos.writeUTF(nome);
  
      return baos.toByteArray();
    }

    public void fromByteArray (byte vb []) throws Exception
    {
      ByteArrayInputStream bais = new ByteArrayInputStream (vb);
      DataInputStream      dis  = new DataInputStream    (bais);
  
      id   = dis.readInt ();
      nome = dis.readUTF ();
    }

    // Strings e comparações
    @Override
    public String toString ()
    {
        return "\n| >\""  + nome + "\"" +
               "\n| Ator" +
               "\n| ID: " + id;
    }

    @Override
    public boolean equals (Object obj)
    {
            return (this.getID () == ((Ator) obj).getID ());
        }

    // @Override
    public int compareTo (Ator outro) 
    {
        return Integer.compare (this.id, outro.getID ());
    }
}

//////////////////////////////////////////////////