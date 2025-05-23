//////////////////////////////////////////////////
// PACOTE

package atuacoes;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
//import java.time.LocalDate;

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS

import aeds3.EntidadeArquivo;

//////////////////////////////////////////////////
// IMPLEMENTAÇÃO DE ATUACAO

public class Atuacao implements EntidadeArquivo 
{
    // VARIÁVEIS
    private int             id;
    private int        id_ator;
    private int       id_serie;

    // CONSTRUTORES
    public Atuacao ()
    {
        this (-1, -1);
    }

    public Atuacao (int id_ator, int id_serie)
    {
        this (-1, id_ator, id_serie);
    }

    public Atuacao (int id, int id_ator, int id_serie)
    {
        this.id_serie = id_serie;
        this.id_ator  =  id_ator;
        this.id       =       id;
    }

    // [getters]
    public int      getID       () {return    id      ;}
    public int      getID_ator  () {return    id_ator ;}
    public int      getID_serie () {return    id_serie;}

    // [setters]
    public void     setID       (int        id) {this.id   =       id;}
    public void     setID_ator  (int   id_ator) {this.id   =  id_ator;}
    public void     setID_serie (int  id_serie) {this.id   = id_serie;}

    // to & from byte array
    public byte [] toByteArray () throws Exception
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream ();
      DataOutputStream      dos  = new DataOutputStream  (baos);
  
      dos.writeInt       (id);
      dos.writeInt  (id_ator);
      dos.writeInt (id_serie);
  
      return baos.toByteArray ();
    }

    public void fromByteArray (byte vb []) throws Exception
    {
      ByteArrayInputStream bais = new ByteArrayInputStream (vb);
      DataInputStream      dis  = new DataInputStream    (bais);
  
      id       = dis.readInt ();
      id_ator  = dis.readInt ();
      id_serie = dis.readInt ();
    }

    // Strings e comparações
    public String toString ()
    {
        return "(" + id + ":" + id_ator + "@" + id_serie + ")";
    }

    @Override
    public boolean equals (Object obj)
    {
            return (this.getID () == ((Atuacao) obj).getID ());
    }

    // @Override
    public int compareTo (Atuacao outro) 
    {
        return Integer.compare (this.id, outro.getID ());
    }
}

//////////////////////////////////////////////////