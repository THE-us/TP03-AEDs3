//////////////////////////////////////////////////
// PACOTE

package series;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA

import java.io.BufferedReader;
import    java.io.IOException;
import    java.util.ArrayList;
import     java.io.FileReader;
import         java.util.List;

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS

import episodios.*;
import  atuacoes.*;
import    atores.*;
import     aeds3.*;

//////////////////////////////////////////////////
// CLASSE ARQUICOSERIES EM SI
public class ArquivoSeries extends Arquivo<Serie> 
{
    // VARIÁVEIS
    ArvoreBMais<ParNomeSerieId> indiceNomeSerie;
    ArvoreBMais<ParIdId>             indices;
    Arquivo<Serie>                 arqSeries;
    ListaInvertida                     lista;

    public ArquivoSeries () throws Exception 
    {
        super ("series", Serie.class.getConstructor ());

        indiceNomeSerie = new ArvoreBMais<> (
                ParNomeSerieId.class.getConstructor (),
                5,
                "./dados/" + nomeEntidade + "/indiceNomeSerie.d.db");
                
        lista = new ListaInvertida (100, "dados/series/dicionario_serie.listainv.db", "dados/series/blocos_serie.listainv.db");

    }

    @Override
    public int create (Serie s) throws Exception 
    {
        int id = super.create (s);
        indiceNomeSerie.create (new ParNomeSerieId (s.getNome (), id));
        return id;
    }

    public Serie[] readNome (String nome) throws Exception 
    {
        String[] termos = nome.toLowerCase ().split ("\\W+");
        List<String> termosFiltrados = new ArrayList<> ();
        List<Integer> frequencias = new ArrayList<> ();

        gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

        List<Integer> ids = new ArrayList<> ();
        List<Float> tfidfs = new ArrayList<> ();

        for (String s : termosFiltrados) 
        {
            ElementoLista[] resultados = lista.read (s);
            if (resultados.length == 0) 
            {
                System.out.println ("Nenhuma série encontrada com o nome '" + nome + "'.");
            } else 
            {
                // calcular idf
                float idf = calcularIDF (resultados);

                for (ElementoLista el : resultados) 
                {
                    float tf = el.getFrequencia ();
                    float tfidf = tf * idf;

                    int id = el.getId ();
                    int index = ids.indexOf (id);

                    if (index != -1) 
                    {
                        // Se ja existe, soma
                        tfidfs.set (index, tfidfs.get (index) + tfidf);
                    } else 
                    {
                        // Se nao existe, adiciona
                        ids.add (id);
                        tfidfs.add (tfidf);
                    }
                }
            }
        }
        List<Integer> indices = new ArrayList<> ();
        for (int i = 0; i < ids.size (); i++) 
        {
            indices.add (i);
        }

        // Ordenar os índices de acordo com os tfidfs (ordem decrescente)
        indices.sort ( (i1, i2) -> Float.compare (tfidfs.get (i2), tfidfs.get (i1)));


        ArrayList<Serie> series = new ArrayList<> ();
        for (int i : indices) 
        {
            Serie se = super.read (ids.get (i));
            if (se != null) 
            {
                series.add (se);
                // System.out.printf ("TF-IDF Total: %.3f\n\n", tfidfs.get (i));
            }
        }

        if (series.size () > 0)
            return series.toArray (new Serie[series.size ()]);
        else
          return null;
    }

    @Override
    public boolean delete (int id) throws Exception 
    {
        Serie s = read (id); // na superclasse
        if (s != null) 
        {
            if (super.delete (id))
                return indiceNomeSerie.delete (new ParNomeSerieId (s.getNome (), id));
        }
        return false;
    }

    public boolean delete (String nome, int id) throws Exception 
    {
        ArquivoEpisodios arqEpisodios = new ArquivoEpisodios ();

        Serie s = read (id);
        // verificar se a serie existe
        if (s != null && s.getID () == id && s.getNome ().equals (nome)) 
        {

            System.out.println (s);

            // verificar episodios vinculados a serie
            Episodio[] ids = arqEpisodios.readEpisodiosSerie (id);

            if (ids != null) 
            {
                // excluir todos os episodios
                boolean deletados = arqEpisodios.deleteEpisodioSerie (id);
                // deletar a serie
                if (deletados) 
                {
                    return delete (id);
                }
            }

            return delete (id);

        }
        return false;
    }

    @Override
    public boolean update (Serie novaSerie) throws Exception 
    {
        Serie s = read (novaSerie.getID ()); // na superclasse
        if (s != null) 
        {
            if (super.update (novaSerie)) 
            {
                if (!s.getNome ().equals (novaSerie.getNome ())) 
                {
                    indiceNomeSerie.delete (new ParNomeSerieId (s.getNome (), s.getID ()));
                    indiceNomeSerie.create (new ParNomeSerieId (novaSerie.getNome (), s.getID ()));
                }
                return true;
            }
        }
        return false;
    }

    // metodo para testar se ha serie vinculada ao id buscado
    public static boolean serieExiste (int id) throws Exception 
    {
        ArquivoSeries arqSeries = new ArquivoSeries ();
        Serie s = arqSeries.read (id); // na superclasse
        
        return s != null;
    }


    //////////////////////////////////////////////////
    // METODOS LISTA INVERTIDA


      // Metodo para carregar stopords do arquivo
    public static List<String> carregarStopwords (String caminhoArquivo) throws IOException 
    {
        List<String> stopwords = new ArrayList<> ();
        try (BufferedReader br = new BufferedReader (new FileReader (caminhoArquivo))) 
        {
            String linha;
            while ( (linha = br.readLine ()) != null) 
            {
                stopwords.add (linha.trim ().toLowerCase ());
            }
        }
        return stopwords;
    }

    // Metodo para filtrar termos e frequencia
    public static void gerarTermosComFrequencia (String[] termos, List<String> termosFiltrados,
            List<Integer> frequencias) throws IOException 
            {
        // arquivo de stopwords
        List<String> stopwords = carregarStopwords ("stopwords.txt");
        // percorre cada termo
        for (String termo : termos) 
        {
            // se nao for stopword
            if (!stopwords.contains (termo)) 
            {
                int index = termosFiltrados.indexOf (termo);
                // Verifica se o termo ja esta na lista termosFiltrados
                if (index == -1) 
                {
                    // se nao tiver adiciona e a frequencia
                    termosFiltrados.add (termo);
                    frequencias.add (1);
                } else 
                {
                    // Se ja estiver, incrementa a frequencia
                    frequencias.set (index, frequencias.get (index) + 1);
                }
            }
        }
    }

    // Metodo para calcular frequencia TF
    public static List<Float> calcularFrequencia (List<Integer> frequencias) 
    {
        List<Float> tf = new ArrayList<> ();
        int total = 0;

        for (int freq : frequencias) 
        {
            total += freq;
        }

        for (int freq : frequencias) 
        {
            tf.add ( (float) freq / total);
        }

        return tf;
    }

    // Metodo para calcular idF
    public float calcularIDF (ElementoLista[] elementos) throws Exception 
    {
        // quantidade de termos
        int total = lista.numeroEntidades ();
        if (elementos == null)
            return 0;
        // quantidade de elementos para um termo especifico
        int docFreq = elementos.length;
        return (float) (Math.log ( (float) total / docFreq) + 1);
    }
}
