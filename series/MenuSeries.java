//////////////////////////////////////////////////
// PACOTE
package series;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA
import java.time.format.DateTimeFormatter;
import                java.time.LocalDate;
import                        java.util.*;
import                          java.io.*;

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS
import episodios.*;
import  atuacoes.*;
import    atores.*;
import     aeds3.*;

//////////////////////////////////////////////////
// CLASSE MENUSÉRIE EM SI

public class MenuSeries 
{
    // VARIÁVEIS
    ListaInvertida                                    lista;
    ArquivoSeries           arqSeries = new ArquivoSeries ();
    ArquivoEpisodios  arqEpisodios = new ArquivoEpisodios ();
    private static Scanner console = new Scanner (System.in);
    MenuEpisodios        menuEpisodio = new MenuEpisodios ();
    MenuAtuacao              menuAtores = new MenuAtuacao ();
    ArquivoAtuacao           arqAtuacao = new ArquivoAtuacao ();
    ArquivoAtor               arqAtores = new ArquivoAtor ();

    public MenuSeries () throws Exception 
    {
        arqSeries = new ArquivoSeries ();
        arqEpisodios = new ArquivoEpisodios ();
        File d = new File ("dados");
        if (!d.exists ())
            d.mkdir ();
        lista = new ListaInvertida (100, "dados/series/dicionario_serie.listainv.db", "dados/series/blocos_serie.listainv.db");
    }

    public void menu (String servico) throws Exception 
    {
        int opcao;
        do 
        {
            System.out.println ("\n\n" + servico);
            System.out.println ("-----------");
            System.out.println ("> Início > Séries");
            System.out.println ("\n1) Incluir");
            System.out.println ("2) Buscar");
            System.out.println ("3) Alterar");
            System.out.println ("4) Excluir");
            //System.out.println ("5) Mostrar todos os episódios de uma série");
            //System.out.println ("6) Mostrar todos os atores de uma série");
            System.out.println ("0) Retornar ao menu anterior");

            System.out.print ("\nOpção: ");
            try 
            {
                opcao = Integer.valueOf (console.nextLine ());
            } catch (NumberFormatException e) 
            {
                opcao = -1;
            }

            switch (opcao) 
            {
                case 1:
                    incluirSerie ();
                    break;
                case 2:
                    buscarSerie ();
                    break;
                case 3:
                    alterarSerie ();
                    break;
                case 4:
                    excluirSerie ();
                    break;
                /*
                case 5:
                    mostrarEpSerie ();
                    break;
                case 6:
                    mostrarAtoresDaSerie ();
                    break;
                */
                case 0:
                    break;
                default:
                    System.out.println ("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }

    public void incluirSerie () 
    {
        System.out.println ("\nInclusão de Série");

        String nome, sinopse, streaming, genero;
        
        short classind = 0;
        int idSerie;
        LocalDate ano = null;
        int anoAtual = LocalDate.now ().getYear ();

        // Nome
        do 
        {
            System.out.print ("Nome da série (min. 4 letras): ");
            nome = console.nextLine ();
        } while (nome.length () < 4);

        // Gênero
        do 
        {
            System.out.print ("Gênero da série (min. 4 letras): ");
            genero = console.nextLine ();
        } while (genero.length () < 4);

        // Classificação indicativa
        boolean CIValido = false;
        do 
        {
            System.out.print ("Digite a classificação indicativa: ");
            try 
            {
                classind = (short) Integer.parseInt (console.nextLine ());
                if (classind >= 0) 
                {
                    CIValido = true;
                } else 
                {
                    System.err.println ("Inválido! Tem que ser maior ou igual a menos um.");
                }
            } catch (NumberFormatException e) 
            {
                System.err.println ("Inválido! Insira um valor numérico.");
            }
        } while (!CIValido);

        // Ano de lançamento
        boolean anoValido = false;
        do 
        {
            System.out.print ("Ano de lançamento (entre 1900 e " + anoAtual + "): ");
            try 
            {
                int anoDigitado = Integer.parseInt (console.nextLine ());
                if (anoDigitado >= 1900 && anoDigitado <= anoAtual) 
                {
                    ano = LocalDate.of (anoDigitado, 1, 1);
                    anoValido = true;
                } else 
                {
                    System.err.println ("Ano inválido! Insira um ano entre 1900 e " + anoAtual + ".");
                }
            } catch (NumberFormatException e) 
            {
                System.err.println ("Ano inválido! Insira um valor numérico.");
            }
        } while (!anoValido);

        // Sinopse
        do 
        {
            System.out.print ("Sinopse (min. 10 letras): ");
            sinopse = console.nextLine ();
        } while (sinopse.length () < 10);

        // Streaming
        do 
        {
            System.out.print ("Streaming (min. 3 letras): ");
            streaming = console.nextLine ();
        } while (streaming.length () < 3);

        System.out.print ("\nConfirma a inclusão da série? (S/N) ");
        char resp = console.nextLine ().charAt (0);
        if (resp == 'S' || resp == 's') 
        {
            try 
            {
                Serie s = new Serie (nome, ano, sinopse, streaming, genero, classind);
                idSerie = arqSeries.create (s);

                // separar termos em vetor de palvras
                String[] termos = nome.toLowerCase ().split ("\\W+");

                List<String> termosFiltrados = new ArrayList<> ();
                List<Integer> frequencias = new ArrayList<> ();

                // filtrar termos que nao sao stopwords e frequencia absoluta
                gerarTermosComFrequencia (termos, termosFiltrados, frequencias);
                // calcular frequencia relativa aos termos
                List<Float> tf = calcularFrequencia (frequencias);
                for (int i = 0; i < termosFiltrados.size (); i++) 
                {
                    String termo = termosFiltrados.get (i);
                    float freqRelativa = tf.get (i);

                    lista.create (termo, new ElementoLista (idSerie, freqRelativa));
                }
                // lista.print ();
                lista.incrementaEntidades ();
                System.out.println ("Série incluída com sucesso.");

                boolean dadosCorretos = false;
                // Atores
                do 
                {
                    System.out.print ("Deseja incluir atores? (S/N): ");
                    char resposta = console.nextLine ().charAt (0);
                    if (resposta == 'S' || resposta == 's') 
                    {
                        int qtd = 0;
                        System.out.print ("Quantos atores deseja incluir: ");

                        do 
                        {
                            try 
                            {
                                qtd = Integer.parseInt (console.nextLine ());
                            } catch (NumberFormatException e) 
                            {
                                System.err.println ("Quantidade inválida!");
                            }
                        } while (qtd <= 0);

                        for (int i = 0; i < qtd; i++) 
                        {
                            System.out.println ("\tAtor " + i);
                            try 
                            {
                                menuAtores.incluirAtores (idSerie);
                            } catch (Exception e) 
                            {
                                System.out.println ("Erro ao incluir atores: " + e.getMessage ());
                            }
                        }
                        dadosCorretos = true;
                    } else if (resposta == 'N' || resposta == 'n') 
                    {
                        dadosCorretos = true;
                    } else 
                    {
                        System.out.println ("Resposta inválida. Digite S ou N.");
                    }
                } while (dadosCorretos == false);
            } catch (Exception e) 
            {
                System.out.println ("Erro ao incluir série.");
                e.printStackTrace ();
            }
        }
    }

    // Buscar Série pelo nome
    public void buscarSerie () throws Exception 
    {
        System.out.println ("\nBusca de Série");

        System.out.print ("Nome da Série: ");
        String nome = console.nextLine ().toLowerCase ();

        String[] termos = nome.split ("\\W+");
        List<String> termosFiltrados = new ArrayList<> ();
        List<Integer> frequencias = new ArrayList<> ();
        // filtrar termos que nao sao stopwords e frequencia absoluta
        gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

        System.out.println ();

        try 
        {
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

            System.out.println ("Séries encontradas:");
            for (int i : indices) 
            {
                Serie serie = arqSeries.read (ids.get (i));
                if (serie != null) 
                {
                    System.out.println (serie);
                    // System.out.printf ("TF-IDF Total: %.3f\n\n", tfidfs.get (i));
                }
            }

        } catch (Exception e) 
        {
            System.out.println ("Erro ao buscar série.");
        }
    }

    // metodo para escolha da serie
    public Serie buscarIdf (String nome) throws Exception 
    {
        String[] termos = nome.split ("\\W+");
        List<String> termosFiltrados = new ArrayList<> ();
        List<Integer> frequencias = new ArrayList<> ();
        // filtrar termos que nao sao stopwords e frequencia absoluta
        gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

        System.out.println ();

        try 
        {
            List<Integer> ids = new ArrayList<> ();
            List<Float> tfidfs = new ArrayList<> ();

            for (String s : termosFiltrados) 
            {
                ElementoLista[] resultados = lista.read (s);
                if (resultados.length == 0) 
                {
                    System.out.println ("Nenhuma série encontrada com o nome '" + nome + "'.");
                    return null;
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

            System.out.println ("Séries encontradas:");
            List<Serie> seriesEncontradas = new ArrayList<> ();
            int contador = 0;
            for (int i : indices) 
            {
                Serie serie = arqSeries.read (ids.get (i));
                if (serie != null) 
                {
                    System.out.println ("\t[" + contador + "]");
                    System.out.println (serie);
                    seriesEncontradas.add (serie);
                    contador++;
                    // System.out.printf ("TF-IDF Total: %.3f\n\n", tfidfs.get (i));
                }
            }
            System.out.print ("Digite o número da série a ser atualizada: ");
            int num = console.nextInt ();
            console.nextLine ();

            if (num < 0 || num >= seriesEncontradas.size ()) 
            {
                System.out.println ("Número inválido.");
                return null;
            }

            return seriesEncontradas.get (num);

        } catch (Exception e) 
        {
            System.out.println ("Erro ao buscar série.");
        }
        return null;
    }

    // Atualizar Série pelo nome
    public void alterarSerie () throws Exception 
    {
        System.out.println ("\nAlteração de Série");

        System.out.print ("Nome da Série: ");
        String nome = console.nextLine ();
        System.out.println ();

        try 
        {
            // serie a ser atualizada, com lista invertida
            Serie serie = buscarIdf (nome);

            if (serie != null) 
            {

                // ------------- Dados a serem atualizados ----------------//
                System.out.print ("Novo nome (ou Enter para manter): ");
                String novoNome = console.nextLine ();
                if (!novoNome.isEmpty ()) 
                {
                    serie.setNome (novoNome);
                }

                System.out.print ("Novo ano de lançamento (ou Enter para manter): ");
                String ano = console.nextLine ();
                if (!ano.isEmpty ()) 
                {
                    LocalDate anoS = LocalDate.parse (ano + "-01-01"); // Apenas o ano
                    serie.setAnoLancamento (anoS);
                }

                System.out.print ("Nova sinopse (ou Enter para manter): ");
                String novaSinopse = console.nextLine ();
                if (!novaSinopse.isEmpty ()) 
                {
                    serie.setSinopse (novaSinopse);
                }

                System.out.print ("Novo streaming (ou Enter para manter): ");
                String novoStreaming = console.nextLine ();
                if (!novoStreaming.isEmpty ()) 
                {
                    serie.setStreaming (novoStreaming);
                }

                System.out.print ("Novo genero (ou Enter para manter): ");
                String novogenero = console.nextLine ();
                if (!novogenero.isEmpty ()) 
                {
                    serie.setGenero (novogenero);
                }

                System.out.print ("Nova classificação indicada (ou Enter para manter): ");
                short novoclassind = -1;
                if (console.hasNextShort ()) 
                {
                    novoclassind = console.nextShort ();
                    serie.setClassificacaoIndicativa (novoclassind);
                }

                System.out.print ("\nConfirma as alterações? (S/N) ");
                char resp = console.nextLine ().charAt (0);

                if (resp == 'S' || resp == 's') 
                {
                    boolean alterado = arqSeries.update (serie);
                    if (alterado) 
                    {
                        System.out.println ("A Série foi atualizada com sucesso!");
                        // se o nome foi alterado, excluir os termos antigos
                        String[] termosAntigos = nome.toLowerCase ().split ("\\W+");
                        for (String termo : termosAntigos) 
                        {
                            if (lista.delete (termo, serie.getID ())) 
                            {
                                lista.decrementaEntidades ();
                            }
                        }

                        if (!novoNome.isEmpty () && !nome.equals (novoNome)) 
                        {
                            // atualizar termos
                            String[] novosTermos = novoNome.toLowerCase ().split ("\\W+");
                            List<String> termosFiltrados = new ArrayList<> ();
                            List<Integer> frequencias = new ArrayList<> ();

                            gerarTermosComFrequencia (novosTermos, termosFiltrados, frequencias);
                            List<Float> tf = calcularFrequencia (frequencias);

                            for (int i = 0; i < termosFiltrados.size (); i++) 
                            {
                                float freqRelativa = tf.get (i);

                                if (lista.update (termosFiltrados.get (i),
                                        new ElementoLista (serie.getID (), freqRelativa))) 
                                        {
                                    lista.incrementaEntidades ();
                                }
                            }
                        }
                    } else 
                    {
                        System.out.println ("Erro ao alterar a série.");
                    }
                } else 
                {
                    System.out.println ("Alterações canceladas.");
                }
            } else 
            {
                System.out.println ("Não foi possível alterar a série");
            }
        } catch (Exception e) 
        {
            System.out.println ("Erro ao alterar série.");
        }
    }

    // Excluir Série pelo nome
    public void excluirSerie () throws Exception 
    {
        System.out.println ("\nExclusão de Série");

        System.out.print ("Nome da Série: ");
        String nome = console.nextLine ();
        System.out.println ();

        try 
        {
            Serie serie = buscarIdf (nome);

            if (serie != null) 
            {
                System.out.println ("Série Encontrada " + serie.getNome ());

                // testar se o numero digitado e' valido

                Episodio[] episodios = arqEpisodios.readEpisodiosSerie (serie.getID ());
                if (episodios != null) 
                {
                    System.out.print ("Essa série possui episódios vinculados, deseja excluir mesmo assim? (S/N) ");
                } else 
                {
                    System.out.print ("Tem certeza que deseja excluir essa série? (S/N) ");
                }
                char resposta = console.nextLine ().charAt (0);
                if (resposta != 'S' && resposta != 's') 
                {
                    System.out.println ("A série não foi excluída.");
                    return;
                }

                boolean excluido = arqSeries.delete (serie.getID ());
                if (excluido) 
                {
                    String[] termos = nome.toLowerCase ().split ("\\W+");
                    List<String> termosFiltrados = new ArrayList<> ();
                    List<Integer> frequencias = new ArrayList<> ();
                    gerarTermosComFrequencia (termos, termosFiltrados, frequencias);

                    for (String termo : termosFiltrados) 
                    {
                        lista.delete (termo, serie.getID ());
                    }
                    lista.decrementaEntidades ();
                    System.out.println ("Série excluída com sucesso.");
                } else 
                {
                    System.out.println ("Erro ao excluir a série.");
                }
            }

        } catch (Exception e) 
        {
            System.out.println ("Erro ao excluir série.");
        }
    }

    public void mostrarEpSerie () 
    {
        System.out.println ("\nBusca de episódio de uma série:");
        System.out.print ("De qual série deseja buscar o episódio? (Nome da série): ");

        String nomeSerieVinculada = console.nextLine ();
        System.out.println ();
        boolean dadosCorretos = false;

        do 
        {
            try 
            {
                Serie serie = buscarIdf (nomeSerieVinculada);

                if (serie != null) 
                {
                    System.out.println ("Série Encontrada " + serie.getNome ());
                    System.out.println ("Séries encontradas:");

                    System.out.println ("Episódios da série " + serie.getNome () + ":");
                    Episodio[] episodios = arqEpisodios.readEpisodiosSerie (serie.getID ());

                    if (episodios != null && episodios.length > 0) 
                    {
                        int temporadaAtual = -1;
                        for (Episodio ep : episodios) 
                        {
                            if (ep.getTemporada () != temporadaAtual) 
                            {
                                temporadaAtual = ep.getTemporada ();
                                System.out.println ("\nTemporada " + temporadaAtual + ":");
                            }
                            menuEpisodio.mostraEpisodio (ep);
                        }
                    } else 
                    {
                        System.out.println ("Nenhum episódio encontrado para esta série.");
                    }
                    dadosCorretos = true;

                } else 
                {
                    System.out.println ("Nenhuma série encontrada com esse nome.");
                    dadosCorretos = true;
                }
            } catch (Exception e) 
            {
                System.out.println ("Erro ao buscar a série: " + e.getMessage ());
                dadosCorretos = true;
            }
        } while (!dadosCorretos);
    }

    public void mostrarAtoresDaSerie () 
    {
        System.out.println ("\nBusca de atores de uma série:");
        System.out.print ("De qual série deseja buscar os atores? (Nome da série): ");

        String nomeSerieVinculada = console.nextLine ();
        System.out.println ();
        boolean dadosCorretos = false;

        do 
        {
            try 
            {
                Serie serie = buscarIdf (nomeSerieVinculada);

                if (serie != null) 
                {
                    System.out.println ("Série Encontrada " + serie.getNome ());
                    System.out.println ("\nAtores da série " + serie.getNome () + ":");
                    Ator[] atores = arqAtores.readAtoresDaSerie (serie.getID ());

                    if (atores != null && atores.length > 0) 
                    {
                        for (Ator at : atores) 
                        {
                            System.out.println ();
                            System.out.println (at);

                            Atuacao[] atuacao = arqAtuacao.read (at.getID (), serie.getID ());
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
                        System.out.println ("Nenhum ator encontrado para esta série.");
                    }
                    dadosCorretos = true;

                } else 
                {
                    System.out.println ("Nenhum  ator encontrado com esse nome.");
                    dadosCorretos = true;
                }
            } catch (Exception e) 
            {
                System.out.println ("Erro ao buscar ator de uma série: " + e.getMessage ());
                dadosCorretos = true;
            }
        } while (!dadosCorretos);
    }

    // Mostrar Papel
    public void mostraAtuacao (Atuacao atuacao) 
    {
        if (atuacao != null) 
        {
            System.out.println ("----------------------");
            System.out.printf ("Papel.....: %s%n", atuacao.getPapel ());
            System.out.printf ("Com um tempo de tela de: %d%n", atuacao.getTempoTela ());
            System.out.println ("----------------------");
        }

    }

    // Incluir serie para metodo povoar
    public void incluirSerieAutomaticamente (String nome, String genero, int classind, int anoLancamento, 
            String sinopse, String streaming) 
            {
        try 
        {
            LocalDate ano = LocalDate.of (anoLancamento, 1, 1);
            Serie s = new Serie (nome, ano, sinopse, streaming, genero, (short) classind);
            int idSerie = arqSeries.create (s);

            // Separar termos
            String[] termos = nome.toLowerCase ().split ("\\W+");
            List<String> termosFiltrados = new ArrayList<> ();
            List<Integer> frequencias = new ArrayList<> ();

            gerarTermosComFrequencia (termos, termosFiltrados, frequencias);
            List<Float> tf = calcularFrequencia (frequencias);

            lista.incrementaEntidades ();
            for (int i = 0; i < termosFiltrados.size (); i++) 
            {
                String termo = termosFiltrados.get (i);
                float freqRelativa = tf.get (i);
                lista.create (termo, new ElementoLista (idSerie, freqRelativa));
            }

            System.out.println ("Série \"" + nome + "\" incluída automaticamente com sucesso.");

        } catch (Exception e) 
        {
            System.out.println ("Erro ao incluir série automaticamente.");
            e.printStackTrace ();
        }
    }

    public void povoar () throws Exception 
    {
        incluirSerieAutomaticamente (
            "Fullmetal Alchemist: Brotherhood",
            "Ação/Fantasia", 14,
            2009,
            "Dois irmãos usam alquimia para tentar recuperar o que perderam, enfrentando consequências sombrias.",
            "Crunchyroll");

        incluirSerieAutomaticamente (
            "Stranger Things",
            "Sobrenatural/Mistério", 14,
            2017,
            "Crianças enfrentam forças sobrenaturais em uma pequena cidade.",
            "Netflix");

        incluirSerieAutomaticamente (
            "Shingeki no Kyojin (Attack on Titan)",
            "Ação/Fantasia", 16,
            2013,
            "Humanos lutam pela sobrevivência contra gigantes devoradores em um mundo brutal.",
            "Crunchyroll");

        incluirSerieAutomaticamente (
            "Kimetsu no Yaiba (Demon Slayer)",
            "Ação/Sobrenatural", 14,
            2019,
            "Tanjiro se torna caçador de demônios para salvar sua irmã e vingar sua família.",
            "Crunchyroll");

        incluirSerieAutomaticamente (
            "Boku no Hero Academia (My Hero Academia)",
            "Ação/Super-heróis", 12,
            2016,
            "Em um mundo de heróis com superpoderes, um garoto sem poderes sonha em se tornar símbolo da paz.",
            "Crunchyroll");

        incluirSerieAutomaticamente (
            "Steins;Gate",
            "Ficção Científica/Thriller", 14,
            2011,
            "Cientistas acidentais descobrem uma maneira de enviar mensagens ao passado, afetando o futuro.",
            "Funimation");

        incluirSerieAutomaticamente (
            "Tokyo Revengers",
            "Ação/Drama", 16,
            2021,
            "Um jovem volta no tempo para impedir a morte de sua ex-namorada e mudar o rumo da sua vida.",
            "Crunchyroll");

        incluirSerieAutomaticamente (
            "Jujutsu Kaisen",
            "Ação/Sobrenatural", 16,
            2020,
            "Estudantes enfrentam maldições perigosas para proteger a humanidade.",
            "Crunchyroll");

        incluirSerieAutomaticamente (
            "Psycho-Pass",
            "Ficção Científica/Policial", 18,
            2012,
            "Em um futuro distópico, a justiça é decidida por um sistema que mede a mente humana.",
            "Crunchyroll");

        incluirSerieAutomaticamente (
            "Black Mirror",
            "Ficção Científica/Drama", 18,
            2015,
            "Cada episódio apresenta uma história distópica sobre o impacto da tecnologia.",
            "Netflix");

        }

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
