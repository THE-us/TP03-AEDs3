package episodios;

import aeds3.*;
import atores.*;
import atuacoes.*;
import episodios.*;
import series.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//Ideia printar a avaliação da serie, que será a media das avaliações dos seus episodios

public class ArquivoEpisodios extends Arquivo<Episodio> {

  Arquivo<Episodio> arqEpisodio;
  ArvoreBMais <ParIdId> indiceIdEpisodio_IdSerie;
  ArvoreBMais <ParNomeSerieId> indiceNomeEpisodio;
  ListaInvertida lista;


  public ArquivoEpisodios () throws Exception {
    super ("episodio", Episodio.class.getConstructor ());

    indiceIdEpisodio_IdSerie = new ArvoreBMais<> (
      ParIdId.class.getConstructor (),
      5,
      "./dados/"+nomeEntidade+"/indiceIdEpisodios_IdSerie.db"
    );

    indiceNomeEpisodio = new ArvoreBMais<> (
      ParNomeSerieId.class.getConstructor (),
      5,
      "./dados/"+nomeEntidade+"/indiceNomeEpisodios.db"
    );

    lista = new ListaInvertida (100, "dados/episodio/dicionario_episodio.listainv.db", "dados/episodio/blocos_episodio.listainv.db");

  }

  @Override
  public int create (Episodio ep) throws Exception{

    // Metodo para verificar se a serie vinculada ao episodio existe 
    // **A ser implementado na classe serie**

    if (ArquivoSeries.serieExiste (ep.getID_serie ()) == false){
      throw new Exception ("Episodio não pode ser criado pois a serie vinculada não existe");
    }

    int id = incluirEpisodioAutomaticamente (ep);
    
    indiceIdEpisodio_IdSerie.create (new ParIdId (ep.getID_serie () , id));
    indiceNomeEpisodio.create (new ParNomeSerieId (ep.getNome (), id));

    return id;
  }

  public Episodio[] readNomeEpisodio (String nome) throws Exception{

        String[] termos = nome.toLowerCase ().split ("\\W+");
        List<String> termosFiltrados = new ArrayList<> ();
        List<Integer> frequencias = new ArrayList<> ();

        gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

        List<Integer> ids = new ArrayList<> ();
        List<Float> tfidfs = new ArrayList<> ();

        for (String s : termosFiltrados) {
            ElementoLista[] resultados = lista.read (s);
            if (resultados.length == 0) {
                System.out.println ("Nenhuma série encontrada com o nome '" + nome + "'.");
            } else {
                // calcular idf
                float idf = calcularIDF (resultados);

                for (ElementoLista el : resultados) {
                    float tf = el.getFrequencia ();
                    float tfidf = tf * idf;

                    int id = el.getId ();
                    int index = ids.indexOf (id);

                    if (index != -1) {
                        // Se ja existe, soma
                        tfidfs.set (index, tfidfs.get (index) + tfidf);
                    } else {
                        // Se nao existe, adiciona
                        ids.add (id);
                        tfidfs.add (tfidf);
                    }
                }
            }
        }
        List<Integer> indices = new ArrayList<> ();
        for (int i = 0; i < ids.size (); i++) {
            indices.add (i);
        }

        // Ordenar os índices de acordo com os tfidfs (ordem decrescente)
        indices.sort ( (i1, i2) -> Float.compare (tfidfs.get (i2), tfidfs.get (i1)));


        ArrayList<Episodio> episodios = new ArrayList<> ();
        for (int i : indices) {
            Episodio ep = super.read (ids.get (i));
            if (ep != null) {
                episodios.add (ep);
                // System.out.printf ("TF-IDF Total: %.3f\n\n", tfidfs.get (i));
            }
        }

        if (episodios.size () > 0)
            return episodios.toArray (new Episodio[episodios.size ()]);
        else
          return null;
  }

  public Episodio[] readNomeEpisodioPorSerie (String nome, int id_serie) throws Exception{

        String[] termos = nome.toLowerCase ().split ("\\W+");
        List<String> termosFiltrados = new ArrayList<> ();
        List<Integer> frequencias = new ArrayList<> ();

        gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

        List<Integer> ids = new ArrayList<> ();
        List<Float> tfidfs = new ArrayList<> ();

        for (String s : termosFiltrados) {
            ElementoLista[] resultados = lista.read (s);
            if (resultados.length == 0) {
                System.out.println ("Nenhuma série encontrada com o nome '" + nome + "'.");
            } else {
                // calcular idf
                float idf = calcularIDF (resultados);

                for (ElementoLista el : resultados) {
                    float tf = el.getFrequencia ();
                    float tfidf = tf * idf;

                    int id = el.getId ();
                    int index = ids.indexOf (id);

                    if (index != -1) {
                        // Se ja existe, soma
                        tfidfs.set (index, tfidfs.get (index) + tfidf);
                    } else {
                        // Se nao existe, adiciona
                        ids.add (id);
                        tfidfs.add (tfidf);
                    }
                }
            }
        }
        List<Integer> indices = new ArrayList<> ();
        for (int i = 0; i < ids.size (); i++) {
            indices.add (i);
        }

        // Ordenar os índices de acordo com os tfidfs (ordem decrescente)
        indices.sort ( (i1, i2) -> Float.compare (tfidfs.get (i2), tfidfs.get (i1)));


        ArrayList<Episodio> episodios = new ArrayList<> ();
        for (int i : indices) {
            Episodio ep = super.read (ids.get (i));
            if (ep != null && ep.getID_serie () == id_serie) {
                episodios.add (ep);
                // System.out.printf ("TF-IDF Total: %.3f\n\n", tfidfs.get (i));
            }
        }

        if (episodios.size () > 0)
            return episodios.toArray (new Episodio[episodios.size ()]);
        else
          return null;
  }
  
  public Episodio[] readEpisodiosSerie (int id_serie) throws Exception{
    
    // Metodo para verificar se a serie vinculada ao episodio existe ordem id_serie -1 é ao contrario?
    ArrayList<ParIdId> pIds = indiceIdEpisodio_IdSerie.read (new ParIdId (id_serie, -1));
  
    if (pIds.size () > 0){
      Episodio[] episodios = new Episodio[pIds.size ()];
      int i = 0;

      // Tive que criar um metodo para pegar o id do episodio
      // pois o metodo read () da superclasse Arquivo não aceita perguntar kutova
      for (ParIdId pID : pIds)
        episodios[i++] = read (pID.getId_agregado ());
      return episodios;
    }else
      return null;
  }

  public boolean delete (String nome, int id) throws Exception{
    Episodio e = read (id);
    if (e != null){
      if (super.delete (id))

        try {
            String[] termos = nome.toLowerCase ().split ("\\W+");
            List<String> termosFiltrados = new ArrayList<> ();
            List<Integer> frequencias = new ArrayList<> ();
            gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

            for (String termo : termosFiltrados) {
                lista.delete (termo,id);
          }
          lista.decrementaEntidades ();

        } catch (Exception ex) {
            ex.printStackTrace ();
        }

        return indiceIdEpisodio_IdSerie.delete (new ParIdId (e.getID_serie (), id)) 
            && indiceNomeEpisodio.delete (new ParNomeSerieId (e.getNome (), id));
    }
    return false;
  }

  public boolean deleteEpisodioSerie (int id_serie) throws Exception{

    // Metodo para verificar se a serie vinculada ao episodio existe ordem id_serie -1 é ao contrario?
    ArrayList<ParIdId> pIds = indiceIdEpisodio_IdSerie.read (new ParIdId (id_serie, -1));

    System.out.println ("Quantidade de episódios da serie deletados: " + pIds.size ());

    if (pIds.size () > 0){
      for (ParIdId pID : pIds){

          try {

            Episodio ep = read (pID.getId_agregado ());
            String nome = ep.getNome ();
            String[] termos = nome.toLowerCase ().split ("\\W+");
            List<String> termosFiltrados = new ArrayList<> ();
            List<Integer> frequencias = new ArrayList<> ();
            gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

            for (String termo : termosFiltrados) {
                lista.delete (termo,pID.getId_agregado ());
            }
            lista.decrementaEntidades ();

          } catch (Exception ex) {
              ex.printStackTrace ();
          }


          delete (pID.getId_agregado ());

      }
      return true;
    } 
    return false;
  }

  @Override
  public boolean update (Episodio novoEpisodio) throws Exception{
    Episodio e = read (novoEpisodio.getID ());
    if (e != null){

      try {

          Episodio ep = read (novoEpisodio.getID ());
          String nome = ep.getNome ();
          String[] termos = nome.toLowerCase ().split ("\\W+");
          List<String> termosFiltrados = new ArrayList<> ();
          List<Integer> frequencias = new ArrayList<> ();
          gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

          for (String termo : termosFiltrados) {
              lista.delete (termo, novoEpisodio.getID ());
          }
          lista.decrementaEntidades ();

      } catch (Exception ex) {
          ex.printStackTrace ();
      }

      incluirEpisodioAutomaticamente (novoEpisodio);
      
      if (super.update (novoEpisodio)){
        if (!e.getNome ().equals (novoEpisodio.getNome ())){
          indiceNomeEpisodio.delete (new ParNomeSerieId (e.getNome (), e.getID ()));
          indiceNomeEpisodio.create (new ParNomeSerieId (novoEpisodio.getNome (), e.getID ()));
        }

        if (e.getID_serie () != novoEpisodio.getID_serie ()){
          indiceIdEpisodio_IdSerie.delete (new ParIdId (e.getID_serie (), e.getID ()));
          indiceIdEpisodio_IdSerie.create (new ParIdId (novoEpisodio.getID_serie (), e.getID ()));
        }

        return true;
      }
    }
    return false;
  }


 
  public float avaliacaoMediaSerie (int id_serie) throws Exception{
    
    float soma = 0;
    
    // Metodo para verificar se a serie vinculada ao episodio existe ordem id_serie -1 é ao contrario?
    ArrayList<ParIdId> pIds = indiceIdEpisodio_IdSerie.read (new ParIdId (id_serie, -1));
    if (pIds.size () > 0){
    Episodio[] episodios = new Episodio[pIds.size ()];
    int i = 0;

    // Tive que criar um metodo para pegar o id do episodio
    // pois o metodo read () da superclasse Arquivo não aceita perguntar kutova
    for (ParIdId pID : pIds){
        episodios[i++] = read (pID.getId_agregado ());
        soma += episodios[i-1].getAvaliacao ();
    }
        return soma / episodios.length;
    }else
      return 0;
}


/*----------------------------------------------Lista Invertida logica ---------------------------------------------------------------*/


  // Incluir serie para metodo povoar
  public int incluirEpisodioAutomaticamente (Episodio ep) {
      try {
          int idEpisodio = super.create (ep);

          // Separar termos
          String[] termos = ep.getNome ().toLowerCase ().split ("\\W+");
          List<String> termosFiltrados = new ArrayList<> ();
          List<Integer> frequencias = new ArrayList<> ();

          gerarTermosComFrequencia (termos, termosFiltrados, frequencias);
          List<Float> tf = calcularFrequencia (frequencias);

          lista.incrementaEntidades ();
          for (int i = 0; i < termosFiltrados.size (); i++) {
              String termo = termosFiltrados.get (i);
              float freqRelativa = tf.get (i);
              lista.create (termo, new ElementoLista (idEpisodio, freqRelativa));
          }

          System.out.println ("Episodio \"" + ep.getNome () + "\" incluída automaticamente com sucesso.");

          return idEpisodio;

      } catch (Exception e) {
          System.out.println ("Erro ao incluir série automaticamente.");
          e.printStackTrace ();
      }

      return -1;
  }


  // Metodo para carregar stopords do arquivo
  public static List<String> carregarStopwords (String caminhoArquivo) throws IOException {
      List<String> stopwords = new ArrayList<> ();
      try (BufferedReader br = new BufferedReader (new FileReader (caminhoArquivo))) {
          String linha;
          while ( (linha = br.readLine ()) != null) {
              stopwords.add (linha.trim ().toLowerCase ());
          }
      }
      return stopwords;
  }

  // Metodo para filtrar termos e frequencia
  public static void gerarTermosComFrequencia (String[] termos, List<String> termosFiltrados,
          List<Integer> frequencias) throws IOException {
      // arquivo de stopwords
      List<String> stopwords = carregarStopwords ("stopwords.txt");
      // percorre cada termo
      for (String termo : termos) {
          // se nao for stopword
          if (!stopwords.contains (termo)) {
              int index = termosFiltrados.indexOf (termo);
              // Verifica se o termo ja esta na lista termosFiltrados
              if (index == -1) {
                  // se nao tiver adiciona e a frequencia
                  termosFiltrados.add (termo);
                  frequencias.add (1);
              } else {
                  // Se ja estiver, incrementa a frequencia
                  frequencias.set (index, frequencias.get (index) + 1);
              }
          }
      }
  }

  // Metodo para calcular frequencia TF
  public static List<Float> calcularFrequencia (List<Integer> frequencias) {
      List<Float> tf = new ArrayList<> ();
      int total = 0;

      for (int freq : frequencias) {
          total += freq;
      }

      for (int freq : frequencias) {
          tf.add ( (float) freq / total);
      }

      return tf;
  }

  // Metodo para calcular idF
  public float calcularIDF (ElementoLista[] elementos) throws Exception {
      // quantidade de termos
      int total = lista.numeroEntidades ();
      if (elementos == null)
          return 0;
      // quantidade de elementos para um termo especifico
      int docFreq = elementos.length;
      return (float) (Math.log ( (float) total / docFreq) + 1);
  }


}


// Duvidas:

// Verificar se o id serie deve realmente ser em uma arvore?
// Verificar se o nome do episodio deve realmente ser em uma arvore?
// Metodo para verificar se a serie vinculada ao episodio existe ordem id_serie -1 é ao contrario?

// Tive que criar um metodo para pegar o id do episodio na class parIdId
// pois o metodo read () da superclasse Arquivo não aceita perguntar kutova

//Ideia printar a avaliação da serie, que será a media das avaliações dos seus episodios



