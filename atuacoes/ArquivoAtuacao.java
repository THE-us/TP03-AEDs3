package atuacoes;

import aeds3.*;
import atores.*;
import series.*;
import episodios.*;

import aeds3.*;
import java.util.ArrayList;

public class ArquivoAtuacao extends Arquivo<Atuacao> {

  public Arquivo<Atuacao> arqAtuacao;
  public ArvoreBMais<ParIdId> indiceIdAtor_IdAtuacao;
  public ArvoreBMais<ParIdId> indiceIdSerie_IdAtuacao;
  public ArvoreBMais<ParNomeSerieId> indiceNomeAtor;

  private ArquivoAtor arqAtor = new ArquivoAtor ();
  private ArquivoSeries arqSerie = new ArquivoSeries ();


  public ArquivoAtuacao () throws Exception {
    super ("atuacao", Atuacao.class.getConstructor ());

    //arvore b+ para o par ator, atuacao
    indiceIdAtor_IdAtuacao = new ArvoreBMais<> (
        ParIdId.class.getConstructor (),
        5,
        "./dados/" + nomeEntidade + "/indiceIdAtor_IdAtuacao.db");

    //arvore b+ para o par serie, atuacao
    indiceIdSerie_IdAtuacao = new ArvoreBMais<> (
        ParIdId.class.getConstructor (),
        5,
        "./dados/" + nomeEntidade + "/indiceIdSerie_IdAtuacao.db");
  }

  @Override
  public int create (Atuacao e) throws Exception {

    // verificar se a serie existe
    if (ArquivoSeries.serieExiste (e.getIdSerie ()) == false) {
      throw new Exception ("Atuacao não pode ser criado pois a serie vinculada não existe");
    }

    // verificar se o ator existe
    if (arqAtor.atorExiste (e.getIdAtor ()) == false) {
      throw new Exception ("Atuacao não pode ser criado pois esse ator não existe");
    }

    int id = super.create (e);

    indiceIdAtor_IdAtuacao.create (new ParIdId (e.getIdAtor (), id));
    indiceIdSerie_IdAtuacao.create (new ParIdId (e.getIdSerie (), id));

    return id;
  }

  // Metodo para buscar atuacao pelo ator
  public Atuacao[] readAtuacaoPorAtor (int id_ator) throws Exception{

    Ator ator = arqAtor.read (id_ator);
    if (ator == null)
      throw new Exception ("Ator nao encontrado");

    ArrayList<ParIdId> ptis = indiceIdAtor_IdAtuacao.read (new ParIdId (id_ator, -1));
    
    if (ptis.size () > 0){
      Atuacao[] atuacao = new Atuacao[ptis.size ()];
      int i = 0;

      for (ParIdId pti: ptis)
        atuacao[i++] = read (pti.getId_agregador ());

      return atuacao;
    }else
      return null;
  }


    // Metodo para buscar atuacao pelo ator
    public Atuacao[] read (int id_ator, int id_serie) throws Exception {
      Ator ator = arqAtor.read (id_ator);
      if (ator == null)
          throw new Exception ("Ator não encontrado");
  
      Serie serie = arqSerie.read (id_serie);
      if (serie == null)
          throw new Exception ("Série não encontrada");
  
      ArrayList<ParIdId> listaAtor = indiceIdAtor_IdAtuacao.read (new ParIdId (id_ator, -1));
      ArrayList<ParIdId> listaSerie = indiceIdSerie_IdAtuacao.read (new ParIdId (id_serie, -1));
  
      // Interseção: achar apenas os Atuacoes que estão em ambas as listas
      ArrayList<Atuacao> atuacoesEncontrados = new ArrayList<> ();
      for (ParIdId p1 : listaAtor) {
          for (ParIdId p2 : listaSerie) {
              if (p1.getId_agregado () == p2.getId_agregado ()) {
                  atuacoesEncontrados.add (read (p1.getId_agregado ()));
              }
          }
      }
  
      if (atuacoesEncontrados.size () > 0)
          return atuacoesEncontrados.toArray (new Atuacao[0]);
      else
          return null;
  }

  public Atuacao[] readAtuacaoPorSerie (int id_serie) throws Exception{

    Serie serie = arqSerie.read (id_serie);
    if (serie == null)
      throw new Exception ("serie nao encontrado");

    ArrayList<ParIdId> ptis = indiceIdSerie_IdAtuacao.read (new ParIdId (id_serie, -1));
    
    if (ptis.size () > 0){
      Atuacao[] atuacao = new Atuacao[ptis.size ()];
      int i = 0;

      for (ParIdId pti: ptis)
        atuacao[i++] = read (pti.getId_agregado ());

      return atuacao;
    }else
      return null;
  }

  @Override
  public boolean delete (int id) throws Exception {
    Atuacao e = read (id);
    if (e != null) {
      if (super.delete (id))
        return indiceIdAtor_IdAtuacao.delete (new ParIdId (e.getIdAtor (), id))
            && indiceIdSerie_IdAtuacao.delete (new ParIdId (e.getIdSerie (), id));
    }
    return false;
  }

  public boolean deleteAtorAtuacao (int id_ator) throws Exception {

    // Metodo para verificar se a serie vinculada ao atuacao existe
    ArrayList<ParIdId> pIds = indiceIdAtor_IdAtuacao.read (new ParIdId (id_ator, -1));

    System.out.println ("Quantidade de atuacoes deletados: " + pIds.size ());

    if (pIds.size () > 0) {
      for (ParIdId pID : pIds)
        delete (pID.getId_agregado ());
      return true;
    }
    return false;
  }

  @Override
  public boolean update (Atuacao novoAtuacao) throws Exception {
    Atuacao e = read (novoAtuacao.getID ());
    if (e != null) {
      if (super.update (novoAtuacao)) {
        if (e.getIdAtor () != novoAtuacao.getIdAtor ()) {
          indiceIdAtor_IdAtuacao.delete (new ParIdId (e.getIdAtor (), e.getID ()));
          indiceIdAtor_IdAtuacao.create (new ParIdId (novoAtuacao.getIdAtor (), e.getID ()));
        }

        return true;
      }
    }
    return false;
  }

}