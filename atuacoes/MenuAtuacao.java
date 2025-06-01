//////////////////////////////////////////////////
// PACOTE
package atuacoes;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA

import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS
import aeds3.ElementoLista;
import aeds3.ListaInvertida;
import aeds3.*;
import atores.*;
import series.*;
import episodios.*;


//////////////////////////////////////////////////
// CLASSE MENUATUACAO EM SI
public class MenuAtuacao 
{
     ListaInvertida lista;

    ArquivoAtor arqAtores = new ArquivoAtor ();
    ArquivoAtuacao arqAtuacao = new ArquivoAtuacao ();
    
    ArquivoSeries arqSeries = new ArquivoSeries ();

    private static Scanner console = new Scanner (System.in);

    public MenuAtuacao () throws Exception 
    {
        arqSeries = new ArquivoSeries ();
        arqAtores = new ArquivoAtor ();
          File d = new File ("dados");
        if (!d.exists ())
            d.mkdir ();
        lista = new ListaInvertida (100, "dados/ator/dicionario_ator.listainv.db", "dados/ator/blocos_ator.listainv.db");
    }

  public void menu (String servico) throws Exception 
  {
    int opcao;
    do 
    {
        System.out.println ("\n\n" + servico);
        System.out.println ("-----------");
        System.out.println ("> Início > Atores");
        System.out.println ("1) Buscar");
        System.out.println ("2) Alterar");
        System.out.println ("3) Excluir");
        System.out.println ("4) Mostrar todos as series de um ator");
        System.out.println ("0) Retornar ao menu anterior");

        System.out.print ("\nOpção: ");
        try 
        {
            opcao = Integer.valueOf (console.nextLine ());
        } 
        catch (NumberFormatException e) 
        {
            opcao = -1;
        }

        switch (opcao) 
        {
            case 1:
                buscarAtor ();
                break;
            case 2:
                alterarAtor ();
                break;
            case 3:
                excluirAtor ();
                break;
            case 4:
                mostrarSeriesDoAtores ();
                break;
            case 0:
                break;
            default:
                System.out.println ("Opção inválida!");
                break;
        }
    } while (opcao != 0);
}


    public void incluirAtores (int idSerie) throws Exception 
    {
        System.out.println ("\nInclusão de Atores");
        System.out.println ();
        boolean dadosCorretos = false;

        String nome = "", papel = "";
        int tempoTela = 0;


        LocalDate dataNasc = LocalDate.now ();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("dd/MM/yyyy");

        // Nome
        do 
        {
            System.out.print ("Nome do Ator: ");
            nome = console.nextLine ();
        } while (nome.length () < 2);


        dadosCorretos = false;
        do 
        {
            System.out.print ("Data de nascimento (DD/MM/AAAA): ");
            String dataStr = console.nextLine ();
            try 
            {
                dataNasc = LocalDate.parse (dataStr, formatter);
                dadosCorretos = true;
            } catch (Exception e) {

                System.err.println ("Data inválida! Use o formato DD/MM/AAAA.");
            }
        } while (!dadosCorretos);

        // Papel
        do 
        {
            System.out.print ("Qual papel do Ator na Serie (min. 2 letras): ");
            papel = console.nextLine ();
        } while (nome.length () < 2);

        // Tempo de tela
        dadosCorretos = false;
        do 
        {
            System.out.print ("Qual o tempo de tela do ator na serie em Minutos (0-999): ");
            if (console.hasNextInt ()) {
    
                tempoTela = console.nextInt ();
                dadosCorretos = true;
            }
            console.nextLine ();
        } while (!dadosCorretos);
        

        System.out.print ("\nConfirma a inclusão do Ator na série? (S/N) ");
        char resp = console.nextLine ().charAt (0);
        if (resp == 'S' || resp == 's') 
        {
           
       try 
       {
    Ator at = new Ator (nome, dataNasc);
    int idAtor = arqAtores.create (at);

    Atuacao atuacao = new Atuacao (papel, tempoTela, idSerie, idAtor);
    int idAtuacao = arqAtuacao.create (atuacao);

    // Processar termos do nome do ator
    String[] termosNome = nome.toLowerCase ().split ("\\W+");
    List<String> termosFiltradosNome = new ArrayList<> ();
    List<Integer> frequenciasNome = new ArrayList<> ();

    gerarTermosComFrequencia (termosNome, termosFiltradosNome, frequenciasNome);
    List<Float> tfNome = calcularFrequencia (frequenciasNome);

    for (int i = 0; i < termosFiltradosNome.size (); i++) 
    {
        String 
        termo = termosFiltradosNome.get (i);
        float freqRelativa = tfNome.get (i);
        lista.incrementaEntidades ();
        lista.create (termo, new ElementoLista (idAtor, freqRelativa));
    }

    // Processar termos do papel do atuacao
    String[] termosPapel = papel.toLowerCase ().split ("\\W+");
    List<String> termosFiltradosPapel = new ArrayList<> ();
    List<Integer> frequenciasPapel = new ArrayList<> ();

    gerarTermosComFrequencia (termosPapel, termosFiltradosPapel, frequenciasPapel);
    List<Float> tfPapel = calcularFrequencia (frequenciasPapel);

    for (int i = 0; i < termosFiltradosPapel.size (); i++) 
    {
        String termo = termosFiltradosPapel.get (i);
        float freqRelativa = tfPapel.get (i);
        lista.incrementaEntidades ();
        lista.create (termo, new ElementoLista (idAtor, freqRelativa));
    }

        System.out.println ("Ator incluído com sucesso.");
    } catch (Exception e) 
    {
        System.out.println ("Erro ao incluir Ator. " + e.getMessage ());
    }

        }
    }


    public void createAtores (int Idserie, String nome, LocalDate dataNasc) throws Exception 
    {
     
       try 
       {
    Ator at = new Ator (nome, dataNasc);
    int idAtor = arqAtores.create (at);

    // Processar termos do nome do ator
    String[] termosNome = nome.toLowerCase ().split ("\\W+");
    List<String> termosFiltradosNome = new ArrayList<> ();
    List<Integer> frequenciasNome = new ArrayList<> ();

    gerarTermosComFrequencia (termosNome, termosFiltradosNome, frequenciasNome);
    List<Float> tfNome = calcularFrequencia (frequenciasNome);

    for (int i = 0; i < termosFiltradosNome.size (); i++) {
        String 
        termo = termosFiltradosNome.get (i);
        float freqRelativa = tfNome.get (i);
        lista.incrementaEntidades ();
        lista.create (termo, new ElementoLista (idAtor, freqRelativa));
    }

    } catch (Exception e) 
    {
        System.out.println ("Erro ao incluir Ator. " + e.getMessage ());
    }

     }
    
      public void createAtuacao (String papel, int tempoTela, int idSerie, int idAtor) throws Exception {
     
          try 
          {

    // Processar termos do papel do atuacao
    String[] termosPapel = papel.toLowerCase ().split ("\\W+");
    List<String> termosFiltradosPapel = new ArrayList<> ();
    List<Integer> frequenciasPapel = new ArrayList<> ();

    gerarTermosComFrequencia (termosPapel, termosFiltradosPapel, frequenciasPapel);
    List<Float> tfPapel = calcularFrequencia (frequenciasPapel);

    for (int i = 0; i < termosFiltradosPapel.size (); i++) 
    {
        String termo = termosFiltradosPapel.get (i);
        float freqRelativa = tfPapel.get (i);
        lista.incrementaEntidades ();
        lista.create (termo, new ElementoLista (idAtor, freqRelativa));
    }

        System.out.println ("Ator incluído com sucesso.");
    
          }
        catch (Exception e) 
        {
        System.out.println ("Erro ao incluir Ator. " + e.getMessage ());
    }

    }
    

    public void buscarAtor () throws IOException {
    System.out.println ("\nBusca de Atores");

    System.out.print ("Nome do Ator: ");
    String nome = console.nextLine ().toLowerCase ();

    String[] termos = nome.split ("\\W+");
    List<String> termosFiltrados = new ArrayList<> ();
    List<Integer> frequencias = new ArrayList<> ();

    // Filtrar termos que não são stopwords e calcular frequência absoluta
    gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

    System.out.println ();

    try 
    {
        List<Integer> ids = new ArrayList<> ();
        List<Float> tfidfs = new ArrayList<> ();

        for (String s : termosFiltrados) {
       
            ElementoLista[] resultados = lista.read (s);
            if (resultados.length == 0) {
      
                System.out.println ("Nenhum ator encontrado com o termo '" + s + "'.");
            } else 
            {
                // Calcular IDF
                float idf = calcularIDF (resultados);

                for (ElementoLista el : resultados) {
               
                    float tf = el.getFrequencia ();
                    float tfidf = tf * idf;

                    int id = el.getId ();
                    int index = ids.indexOf (id);

                    if (index != -1) {
   
                        // Se já existe, soma
                        tfidfs.set (index, tfidfs.get (index) + tfidf);
                    } else 
                    {
                        // Se não existe, adiciona
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

        // Ordenar os índices de acordo com os TF-IDFs (ordem decrescente)
        indices.sort ( (i1, i2) -> Float.compare (tfidfs.get (i2), tfidfs.get (i1)));

        System.out.println ("Atores encontrados:");
        for (int i : indices) 
        {
            Ator ator = arqAtores.read (ids.get (i));
            if (ator != null) 
            {
                System.out.println (ator);
                
            }
        }

    } catch (Exception e) 
    {
        System.out.println ("Erro ao buscar ator: " + e.getMessage ());
    }
}

public Ator buscarIdfAtor (String nome) throws Exception 
{
    String[] termos 
    = nome.toLowerCase ().split ("\\W+");
    List<String> termosFiltrados = new ArrayList<> ();
    List<Integer> frequencias = new ArrayList<> ();

    // Gera termos filtrados e frequência absoluta
    gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

    if (termosFiltrados.isEmpty ()) {
 
        System.out.println ("Nenhum termo relevante encontrado.");
        return null;
    }

    List<Integer> ids = new ArrayList<> ();
    List<Float> tfidfs = new ArrayList<> ();

    for (String termo : termosFiltrados) 
    {
       
        ElementoLista[] resultados = lista.read (termo);

        if (resultados.length == 0) 
        {
  
            System.out.println ("Nenhum ator encontrado com o termo: " + termo);
            continue;
        }

        float idf = calcularIDF (resultados);

        for (ElementoLista el : resultados) 
        {
            float tf = el.getFrequencia ();
            float tfidf = tf * idf;
            int id = el.getId ();

            int idx = ids.indexOf (id);
            if (idx != -1) 
            {
                tfidfs.set (idx, tfidfs.get (idx) + tfidf);
            } else 
            {
                ids.add (id);
                tfidfs.add (tfidf);
            }
        }
    }

    if (ids.isEmpty ()) 
    {
        System.out.println ("Nenhum ator corresponde aos termos fornecidos.");
        return null;
    }

    // Ordena os índices pelo maior TF-IDF
    List<Integer> indices = new ArrayList<> ();
    for (int i = 0; i < ids.size (); i++) 
    {
        indices.add (i);
    }
    indices.sort ( (i1, i2) -> Float.compare (tfidfs.get (i2), tfidfs.get (i1)));

    System.out.println ("Atores encontrados:");
    for (int i : indices) 
    {
        Ator ator = arqAtores.read (ids.get (i));
        if (ator != null) 
        {
            System.out.println (ator);
        }
    }

    // Retorna o ator mais relevante
    return arqAtores.read (ids.get (indices.get (0)));
}

public void alterarAtor () throws Exception {
    System.out.println ("\nAlteração de Ator");

    System.out.print ("Nome do Ator: ");
    String nome = console.nextLine ();
    System.out.println ();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("dd/MM/yyyy");

    try 
    {
        // buscar o ator mais relevante com lista invertida e TF-IDF
        System.out.println (nome);
        Ator ator = buscarIdfAtor (nome);  


        if (ator != null) 
        {
            System.out.println ("Ator Encontrado: " + ator.getNome ());

            // ------------- Dados a serem atualizados ----------------//
            System.out.print ("Novo nome (ou Enter para manter): ");
            String novoNome = console.nextLine ();
            if (!novoNome.isEmpty ()) {
   
                ator.setNome (novoNome);
            }

            do 
            {
                System.out.print ("Nova data de nascimento (DD/MM/AAAA) (ou Enter para manter): ");
                String novaData = console.nextLine ();
                if (novaData.isEmpty ()) 
                {
                    break;
                }
                try 
                {
                    ator.setDataNasc (LocalDate.parse (novaData, formatter));
                    break;
                } catch (Exception e) {
    
                    System.err.println ("Data inválida! Use o formato DD/MM/AAAA.");
                }
            } while (true);


            System.out.print ("\nConfirma as alterações? (S/N) ");
            char resp = console.nextLine ().charAt (0);

            if (resp == 'S' || resp == 's') {
          
                boolean alterado = arqAtores.update (ator);
                if (alterado) 
                {
                    System.out.println ("O Ator foi atualizado com sucesso!");

                    // se o nome foi alterado, atualiza a lista invertida
                    if (!novoNome.isEmpty () && !nome.equals (novoNome)) {
        
                        // atualizar termos
                        String[] novosTermos = novoNome.toLowerCase ().split ("\\W+");
                        List<String> termosFiltrados = new ArrayList<> ();
                        List<Integer> frequencias = new ArrayList<> ();

                        gerarTermosComFrequencia (novosTermos, termosFiltrados, frequencias);
                        List<Float> tf = calcularFrequencia (frequencias);

                        for (int i = 0; i < termosFiltrados.size (); i++) {
                        
                            float freqRelativa = tf.get (i);

                            if (lista.update (termosFiltrados.get (i),
                                    new ElementoLista (ator.getID (), freqRelativa))) {
                         
                                lista.incrementaEntidades ();
                            }
                        }
                    }
                } else 
                {
                    System.out.println ("Erro ao alterar o Ator.");
                }
            } else 
            {
                System.out.println ("Alterações canceladas.");
            }
        } else 
        {
            System.out.println ("Não foi possível alterar o Ator.");
        }
    } catch (Exception e) 
    {
        System.out.println ("Erro ao alterar Ator: " + e.getMessage ());
    }
}


 // Excluir Ator pelo nome
public void excluirAtor () throws Exception 
{
    System.out.println ("\nExclusão de Ator");

    System.out.print ("Nome do Ator: ");
    String nome = console.nextLine ();
    System.out.println ();

    try 
    {
        String[] termos = nome.toLowerCase ().split ("\\W+");
        List<String> termosFiltrados = new ArrayList<> ();
        List<Integer> frequencias = new ArrayList<> ();

        gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

        List<Integer> ids = new ArrayList<> ();
        List<Float> tfidfs = new ArrayList<> ();

        for (String termo : termosFiltrados) 
        {
           
            ElementoLista[] resultados = lista.read (termo);

            if (resultados.length == 0) continue;

            float idf = calcularIDF (resultados);

            for (ElementoLista el : resultados) 
            {
           
                float tf = el.getFrequencia ();
                float tfidf = tf * idf;
                int id = el.getId ();

                int idx = ids.indexOf (id);
                if (idx != -1) 
                {
                    tfidfs.set (idx, tfidfs.get (idx) + tfidf);
                } else 
                {
                    ids.add (id);
                    tfidfs.add (tfidf);
                }
            }
        }

        if (ids.isEmpty ()) 
        {
            System.out.println ("Nenhum ator encontrado.");
            return;
        }

        // Ordenar por TF-IDF
        List<Integer> indices = new ArrayList<> ();
        for (int i = 0; i < ids.size (); i++) indices.add (i);
        indices.sort ( (i1, i2) -> Float.compare (tfidfs.get (i2), tfidfs.get (i1)));

        // Criar array de atores ordenado por relevância
        Ator[] atores = new Ator[indices.size ()];
        for (int i = 0; i < indices.size (); i++) {
  
            int pos = indices.get (i);
            atores[i] = arqAtores.read (ids.get (pos));
        }

        // Exibir lista
        System.out.println ("Atores encontrados:");
        for (int i = 0; i < atores.length; i++) {
            if (atores[i] != null) {
 
                System.out.print ("[" + i + "] ");
                System.out.println (atores[i]);
            }
        }

        System.out.println ("Digite o numero do autor que você deseja excluir");
    
        int escolha = console.nextInt ();
        console.nextLine (); // limpa buffer

        if (escolha < 0 || escolha >= atores.length || atores[escolha] == null) 
        {
            System.err.println ("Número inválido!");
            return;
        }

        Ator ator = atores[escolha];

        System.out.print ("Tem certeza que deseja excluir esse ator? (S/N) ");
        String confirm = console.nextLine ().trim ();
        if (!confirm.equalsIgnoreCase ("S")) 
        {
          
            System.out.println ("Exclusão cancelada.");
            return;
        }

        if (arqAtores.delete (ator.getID ())) 
        {
          
            String[] termosAtor = ator.getNome ().toLowerCase ().split ("\\W+");
            List<String> termosRemover = new ArrayList<> ();
            List<Integer> freqs = new ArrayList<> ();
            gerarTermosComFrequencia (termosAtor, termosRemover, freqs);

            for (String termo : termosRemover) 
            {
             
                lista.delete (termo, ator.getID ());
                lista.decrementaEntidades ();
            }

            System.out.println ("Ator excluído com sucesso.");
        } else 
        {
            System.out.println ("Erro ao excluir o ator.");
        }

    } catch (Exception e) 
    {
        System.out.println ("Erro ao excluir ator: " + e.getMessage ());
    }
}



    public void mostrarSeriesDoAtores ()
    {
        System.out.println ("\nBusca de Series de um ator:");
        System.out.print ("De qual ator deseja buscar as Series? (Nome do ator): ");
        
        String nomeAtorVinculado = console.nextLine ();
        System.out.println ();
        boolean dadosCorretos = false;
        
        do 
        {
            try 
            {
                Ator[] atores = arqAtores.readNome (nomeAtorVinculado);
                
                if (atores != null && atores.length > 0) 
                {
                    System.out.println ("Séries encontradas:");
                    for (int i = 0; i < atores.length; i++) 
                    {
                      
                        System.out.print ("[" + i + "] ");
                        System.out.println (atores[i]);
                    }
                    
                    System.out.print ("\nDigite o número do ator escolhida: ");
                    if (console.hasNextInt ()) 
                    {
            
                        int num = console.nextInt ();
                        console.nextLine (); // Limpar buffer
                        
                        if (num < 0 || num >= atores.length || atores[num] == null) 
                        {
                            System.err.println ("Número inválido!");
                        } else 
                        {
                            System.out.println ("\nSeries do ator " + atores[num].getNome () + ":");
                            Serie[] series = arqAtores.readSerieDoAtor (atores[num].getID ());
                            
                            if (series != null && series.length > 0) 
                            {
                                for (Serie se : series) 
                                {
                                    System.out.println ();
                                    System.out.println (se);

                                    Atuacao[] atuacao = arqAtuacao.read (atores[num].getID (), se.getID ());
                                    if (atuacao != null && atuacao.length > 0) 
                                    {
                                        System.out.println ("Fazendo o papel de: ");
                                        for (Atuacao el : atuacao) 
                                        {
                                            mostraAtuacao (el);
                                        }
                                    }
                                }
                            } else 
                            {
                                System.out.println ("Nenhum série encontrado para esta ator.");
                            }
                            dadosCorretos = true;
                        }
                    } else 
                    {
                        System.err.println ("Entrada inválida! Digite um número válido.");
                        console.nextLine (); // Limpar buffer
                    }
                } else 
                {
                    System.out.println ("Nenhum série encontrada para esse ator.");
                    dadosCorretos = true;
                }
            } catch (Exception e) {

                System.out.println ("Erro ao buscar séries de um ator: " + e.getMessage ());
                dadosCorretos = true;
            }
        } while (!dadosCorretos);
    }

    //Mostrar Papel
    public void mostraAtuacao (Atuacao atuacao) 
    {
        if (atuacao != null) 
        {
            System.out.println ("[----------------------]");
            System.out.printf ("Papel.....: %s%n", atuacao.getPapel ());
            System.out.printf ("Com um tempo de tela de: %d%n", atuacao.getTempoTela ());
            System.out.println ("[----------------------]");
        }

    }

    public void povoar () throws Exception 
    {

        int IdAtor1 = arqAtores.create (new Ator ("Edward Elric", LocalDate.of (2040, 3, 7)));
        int IdAtor2 = arqAtores.create (new Ator ("Alphonse Elric", LocalDate.of (2030, 3, 7)));
        int IdAtor3 = arqAtores.create (new Ator ("Eleven", LocalDate.of (2000, 3, 7)));
        int IdAtor4 = arqAtores.create (new Ator ("Eren Yeager", LocalDate.of (1956, 3, 7)));
        int IdAtor5 = arqAtores.create (new Ator ("Tanjiro Kamado", LocalDate.of (1946, 12, 15)));
        int IdAtor6 = arqAtores.create (new Ator ("Izuku Midoriya", LocalDate.of (2015, 3, 7)));
        int IdAtor7 = arqAtores.create (new Ator ("Rintarou Okabe", LocalDate.of (2015, 3, 7)));
        int IdAtor8 = arqAtores.create (new Ator ("Takemichi Hanagaki", LocalDate.of (2015, 3, 7)));
        int IdAtor9 = arqAtores.create (new Ator ("Yuji Itadori", LocalDate.of (2015, 3, 7)));
        int IdAtor10 = arqAtores.create (new Ator ("Akane Tsunemori", LocalDate.of (2015, 3, 7)));


        // Substituição com atores de anime nos métodos createAtores
        createAtores (IdAtor1, "Edward Elric", LocalDate.of (2040, 3, 7));
        createAtores (IdAtor1, "Alphonse Elric", LocalDate.of (2030, 3, 7));
        createAtores (IdAtor3, "Eleven", LocalDate.of (2000, 3, 7));
        createAtores (IdAtor4, "Eren Yeager", LocalDate.of (1956, 3, 7));
        createAtores (IdAtor5, "Tanjiro Kamado", LocalDate.of (1946, 12, 15));
        createAtores (IdAtor6, "Izuku Midoriya", LocalDate.of (2015, 3, 7));
        createAtores (IdAtor7, "Rintarou Okabe", LocalDate.of (2015, 3, 7));
        createAtores (IdAtor8, "Takemichi Hanagaki", LocalDate.of (2015, 3, 7));
        createAtores (IdAtor9, "Yuji Itadori", LocalDate.of (2015, 3, 7));
        createAtores (IdAtor10, "Akane Tsunemori", LocalDate.of (2015, 3, 7));

       

        // Gravação no arquivo
        arqAtuacao.create (new Atuacao ("Edward Elric", 95, 1, IdAtor1));
        arqAtuacao.create (new Atuacao ("Alphonse Elric", 92, 1, IdAtor2));

        arqAtuacao.create (new Atuacao ("Eleven", 88, 2, IdAtor3));
          // Mantido pois já é nome do personagem
        arqAtuacao.create (new Atuacao ("Eren Yeager", 85, 3, IdAtor4));

        arqAtuacao.create (new Atuacao ("Tanjiro Kamado", 84, 4, IdAtor5));

        arqAtuacao.create (new Atuacao ("Izuku Midoriya", 86, 5, IdAtor6));

        arqAtuacao.create (new Atuacao ("Rintarou Okabe", 91, 6, IdAtor7));

        arqAtuacao.create (new Atuacao ("Takemichi Hanagaki", 82, 7, IdAtor8));

        arqAtuacao.create (new Atuacao ("Yuji Itadori", 80, 8, IdAtor9));

        arqAtuacao.create (new Atuacao ("Akane Tsunemori", 78, 9, IdAtor10));


        // Lista invertida
        createAtuacao ("Edward Elric", 95, 1, IdAtor1);
        createAtuacao ("Alphonse Elric", 92, 1, IdAtor2);

        createAtuacao ("Eleven", 88, 2, IdAtor3);
        createAtuacao ("Eren Yeager", 85, 2, IdAtor4);

        createAtuacao ("Tanjiro Kamado", 84, 3, IdAtor5);
        createAtuacao ("Izuku Midoriya", 86, 3, IdAtor6);

        createAtuacao ("Rintarou Okabe", 91, 4, IdAtor7);
        createAtuacao ("Takemichi Hanagaki", 82, 4, IdAtor8);

        createAtuacao ("Yuji Itadori", 80, 5, IdAtor9);
        createAtuacao ("Akane Tsunemori", 78, 5, IdAtor10);

    }

      // Metodo para carregar stopwords do arquivo
    public static List<String> carregarStopwords (String caminhoArquivo) throws IOException 
    {
        List<String> stopwords = new ArrayList<> ();
        try (BufferedReader br = new BufferedReader (new FileReader (caminhoArquivo))) 
        {
            String linha;
            while ( (linha = br.readLine ()) != null) {
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
        for (String termo : termos) {

            // se nao for stopword
            if (!stopwords.contains (termo)) {
          
                int index = termosFiltrados.indexOf (termo);
                // Verifica se o termo ja esta na lista termosFiltrados
                if (index == -1) {

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

        for (int freq : frequencias) {
   
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
