//////////////////////////////////////////////////
// PACOTE

package series;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import aeds3.ElementoLista;
import aeds3.ListaInvertida;

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS

//import aeds3.*;
import episodios.*;
//import series.*;

//////////////////////////////////////////////////
// CLASSE MENUS

public class MenuSeries 
{
    ListaInvertida list;
    ArquivoSeries arqSeries = new ArquivoSeries();
    ArquivoEpisodios arqEpisodios = new ArquivoEpisodios();
    private static Scanner console = new Scanner(System.in);
    MenuEpisodios menuEpisodio = new MenuEpisodios(); 

    public MenuSeries() throws Exception 
    {
        arqSeries = new ArquivoSeries();
        arqEpisodios = new ArquivoEpisodios();
        // criar lista invetida
        // list = new ListaInvertida(); 
    }
    // ==================== métodos da macieira ========================================

    public float calcularIDF(ElementoLista[] element) throws Exception {
        int res = list.numeroEntidades();
        if (element == null)
            return 0;
        int freq = element.length;
        return (float) (Math.log((float) res / freq) + 1);
    }
    public static List<Float> calcularFrequencia(List<Integer> frequencias) {
        List<Float> tf = new ArrayList<>();
        int total = 0;

        for (int freq : frequencias) {
            total += freq;
        }

        for (int freq : frequencias) {
            tf.add((float) freq / total);
        }

        return tf;
    }
    public static List<String> carregarStopwords(String caminhoArquivo) throws IOException {
        List<String> stopwords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                stopwords.add(linha.trim().toLowerCase());
            }
        }
        return stopwords;
    }

    public static void gerarTermosComFrequencia(String[] termos, List<String> termosFiltrados,
            List<Integer> frequencias) throws IOException {
        // arquivo de stopwords
        List<String> stopwords = carregarStopwords("stopwords.txt");
        // percorre cada termo
        for (String termo : termos) {
            // se nao for stopword
            if (!stopwords.contains(termo)) {
                int index = termosFiltrados.indexOf(termo);
                // Verifica se o termo ja esta na lista termosFiltrados
                if (index == -1) {
                    // se nao tiver adiciona e a frequencia
                    termosFiltrados.add(termo);
                    frequencias.add(1);
                } else {
                    // Se ja estiver, incrementa a frequencia
                    frequencias.set(index, frequencias.get(index) + 1);
                }
            }
        }
    }

    public Serie buscarIdf(String nome) throws Exception {
        String[] termos = nome.split("\\W+");
        List<String> termosFiltrados = new ArrayList<>();
        List<Integer> frequencias = new ArrayList<>();
        // filtrar termos que nao sao stopwords e frequencia absoluta
        gerarTermosComFrequencia(termos, termosFiltrados, frequencias);

        System.out.println();

        try {
            List<Integer> ids = new ArrayList<>();
            List<Float> tfidfs = new ArrayList<>();

            for (String s : termosFiltrados) {
                ElementoLista[] resultados = list.read(s);
                if (resultados.length == 0) {
                    System.out.println("Nenhuma série encontrada com o nome '" + nome + "'.");
                } else {
                    // calcular idf
                    float idf = calcularIDF(resultados);

                    for (ElementoLista el : resultados) {
                        float tf = el.getFrequencia();
                        float tfidf = tf * idf;

                        int id = el.getId();
                        int index = ids.indexOf(id);

                        if (index != -1) {
                            // Se ja existe, soma
                            tfidfs.set(index, tfidfs.get(index) + tfidf);
                        } else {
                            // Se nao existe, adiciona
                            ids.add(id);
                            tfidfs.add(tfidf);
                        }
                    }
                }
            }
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                indices.add(i);
            }

            // Ordenar os índices de acordo com os tfidfs (ordem decrescente)
            indices.sort((i1, i2) -> Float.compare(tfidfs.get(i2), tfidfs.get(i1)));

            System.out.println("Séries encontradas:");
            List<Serie> seriesEncontradas = new ArrayList<>();
            int contador = 0;
            for (int i : indices) {
                Serie serie = arqSeries.read(ids.get(i));
                if (serie != null) {
                    System.out.println("\t[" + contador + "]");
                    mostraSerie(serie);
                    seriesEncontradas.add(serie);
                    contador++;
                    // System.out.printf("TF-IDF Total: %.3f\n\n", tfidfs.get(i));
                }
            }
            System.out.print("Digite o número da série a ser atualizada: ");
            int num = console.nextInt();
            console.nextLine();

            if (num < 0 || num >= seriesEncontradas.size()) {
                System.out.println("Número inválido.");
                return null;
            }

            return seriesEncontradas.get(num);

        } catch (Exception e) {
            System.out.println("Erro ao buscar série.");
        }
        return null;
    }

    // ==================== métodos da macieira ========================================
    
    public void menu(String servico) throws Exception 
    {
        int opcao;
        do 
        {
            System.out.println("\n\n" + servico);
            System.out.println("-----------");
            System.out.println("> Início > Séries");
            System.out.println("\n1) Incluir");
            System.out.println("2) Buscar");
            System.out.println("3) Alterar");
            System.out.println("4) Excluir");
            System.out.println("5) Mostrar todos os episódios de uma série");
            System.out.println("0) Retornar ao menu anterior");

            System.out.print("\nOpção: ");
            try 
            {
                opcao = Integer.valueOf(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
            catch (Exception e)
            {
                System.out.print("\n\n\tPrograma Finalizado Com Sucesso.\n\n");
                opcao = -404;
                System.exit(0);
            }

            switch (opcao) 
            {
                case 1:
                    incluirSerie();
                    break;
                case 2:
                    ListaInvertida indiceSeries = new ListaInvertida(5, "indiceSerie.dic", "indiceSerie.blc");
                    buscarSerie();
                    break;
                case 3:
                    alterarSerie();
                    break;
                case 4:
                    excluirSerie();
                    break;
                    case 5:
                    mostrarEpSerie();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }

    public void incluirSerie() 
    {
        System.out.println("\nInclusão de Série");

        String nome, sinopse, streaming, genero;
        short classificacaoIndicativa;
        LocalDate ano = null;
        int anoAtual = LocalDate.now().getYear();

        do 
        {
            System.out.print("Nome da série (min. 4 letras): ");
            nome = console.nextLine();
        } while (nome.length() < 4);
        do 
        {
            System.out.print("Gênero da série (min. 4 letras): ");
            genero = console.nextLine();
        } while (genero.length() < 4);

        System.out.print("Classificação indicativa da série: ");
        classificacaoIndicativa = console.nextShort();

        boolean anoValido = false;
        do 
        {
            System.out.print("Ano de lançamento (entre 1900 e " + anoAtual + "): ");
            try 
            {
                int anoDigitado = Integer.parseInt(console.nextLine());
                if (anoDigitado >= 1900 && anoDigitado <= anoAtual) 
                {
                    ano = LocalDate.of(anoDigitado, 1, 1);
                    anoValido = true;
                } else 
                {
                    System.err.println("Ano inválido! Insira um ano entre 1900 e " + anoAtual + ".");
                }
            } catch (NumberFormatException e) 
            {
                System.err.println("Ano inválido! Insira um valor numérico.");
            }
        } while (!anoValido);

        
        do 
        {
            System.out.print("Sinopse (min. 10 letras): ");
            sinopse = console.nextLine();
        } while (sinopse.length() < 10);

        
        do 
        {
            System.out.print("Streaming (min. 3 letras): ");
            streaming = console.nextLine();
        } while (streaming.length() < 3);

        System.out.print("\nConfirma a inclusão da série? (S/N) ");
        char resp = console.nextLine().charAt(0);
        if (resp == 'S' || resp == 's') 
        {
            try 
            {
                Serie s = new Serie(nome, ano, sinopse, streaming, genero, classificacaoIndicativa);
                arqSeries.create(s);
                System.out.println("Série incluída com sucesso.");
            } catch (Exception e) 
            {
                System.out.println("Erro ao incluir série.");
            }
        }
    }

    
    public void buscarSerie() 
    {
        System.out.println("\nBusca de Série");

        System.out.print("Nome da Série: ");
        String nome = console.nextLine();

        System.out.println();
        try 
        {
            Serie[] series = arqSeries.readNome(nome);
            if (series != null && series.length > 0) 
            {
                System.out.println("Séries encontradas:");
                for (Serie s : series) 
                {
                    System.out.println (s);
                }
            } else 
            {
                System.out.println("Nenhuma série encontrada com esse nome.");
            }
            
        } catch (Exception e) 
        {
            System.out.println("Erro ao buscar série.");
        }
    }


    public void alterarSerie() throws Exception 
    {
        System.out.println("\nAlteração de Série");

        System.out.print("Nome da Série: ");
        String nome = console.nextLine();
        System.out.println();

        try 
        {
            Serie[] serie = arqSeries.readNome(nome);
            if (serie != null) 
            {
                
                for (int i=0; i < serie.length; i++) 
                {
                    System.out.println("\t[" + i + "]");
                    System.out.println (serie[i]);
                }

                System.out.print("Digite o número da série a ser atualizada: ");
                int num = console.nextInt();
                console.nextLine();

                
                if (num >= 0 && serie[num] != null) 
                {
                    System.out.print("Novo nome (ou Enter para manter): ");
                    String novoNome = console.nextLine();
                    if (!novoNome.isEmpty()) 
                    {
                        serie[num].setNome(novoNome);
                    }

                    System.out.print("Novo ano de lançamento (ou Enter para manter): ");
                    String ano = console.nextLine();
                    if (!ano.isEmpty()) 
                    {
                        LocalDate anoS = LocalDate.parse(ano + "-01-01"); 
                        serie[num].setAnoLancamento(anoS);
                    }

                    System.out.print("Nova sinopse (ou Enter para manter): ");
                    String novaSinopse = console.nextLine();
                    if (!novaSinopse.isEmpty()) 
                    {
                        serie[num].setSinopse(novaSinopse);
                    }

                    System.out.print("Novo streaming (ou Enter para manter): ");
                    String novoStreaming = console.nextLine();
                    if (!novoStreaming.isEmpty()) 
                    {
                        serie[num].setStreaming(novoStreaming);
                    }

                    System.out.print("Novo genero (ou Enter para manter): ");
                    String novogenero = console.nextLine();
                    if (!novoStreaming.isEmpty()) 
                    {
                        serie[num].setGenero(novogenero);
                    }

                    System.out.print("Nova classificação indicada (ou Enter para manter): ");
                    short novoclassificacaoIndicativa = console.nextShort();
                    serie[num].setClassificacaoIndicativa(novoclassificacaoIndicativa);
        
                    System.out.print("\nConfirma as alterações? (S/N) ");
                    char resp = console.nextLine().charAt(0);

                    if (resp == 'S' || resp == 's') 
                    {
                        boolean alterado = arqSeries.update(serie[num]);
                        if (alterado) 
                        {
                            System.out.println("Série alterada com sucesso!");
                        } else 
                        {
                            System.out.println("Erro ao alterar a série.");
                        }
                    } else 
                    {
                        System.out.println("Alterações canceladas.");
                    }
                } else 
                {
                    System.out.println("Não há serie associada a esse número.");
                }
            } else 
            {
                System.out.println("Série não encontrada.");
            }
        } catch (Exception e) 
        {
            System.out.println("Erro ao alterar série.");
        }
    }

    
    public void excluirSerie() throws Exception 
    {
        System.out.println("\nExclusão de Série");
        
        System.out.print("Nome da Série: ");
        String nome = console.nextLine();
        System.out.println();

        try 
        {
            Serie[] serie = arqSeries.readNome(nome);
            if (serie != null && serie.length > 0) 
            {
                for (int i=0; i < serie.length; i++) 
                {
                    System.out.println("\t[" + i + "]");
                    System.out.println (serie[i]);
                }

                System.out.print("Digite o número da série a ser excluída: ");
                int num = console.nextInt();
                console.nextLine();

            
                if (num >= 0 && serie[num] != null) 
                {
                    Episodio[] episodios = arqEpisodios.readEpisodiosSerie(serie[num].getID());
                    if (episodios != null) 
                    {
                        System.out.print("Essa série possui episódios vinculados, deseja excluir mesmo assim? (S/N) ");
                    } else 
                    {
                        System.out.print("Tem certeza que deseja excluir essa série? (S/N) ");
                    }
                    char resposta = console.nextLine().charAt(0);
                    if (resposta != 'S' && resposta != 's') 
                    {
                        System.out.println("A série não foi excluída.");
                        return;
                    }
                    
                    boolean excluido = arqSeries.delete(serie[num].getID());
                    if (excluido) 
                    {
                        System.out.println("Série excluída com sucesso!.");
                    } else 
                    {
                        System.out.println("Erro ao excluir a série.");
                    }
                } else 
                {
                    System.out.println("Não há serie associada a esse número.");
                }
            } else 
            {
                System.out.println("Série não encontrada.");
            }
        } catch (Exception e) 
        {
            System.out.println("Erro ao excluir série.");
        }
    }

    public void mostrarEpSerie() 
    {
        System.out.println("\nBusca de episódio:");
        System.out.print("De qual série deseja buscar o episódio? (Nome da série): ");
        
        String nomeSerieVinculada = console.nextLine();
        System.out.println();
        boolean dadosCorretos = false;
        
        do 
        {
            try 
            {
                Serie[] series = arqSeries.readNome(nomeSerieVinculada);
                
                if (series != null && series.length > 0) 
                {
                    System.out.println("Séries encontradas:");
                    for (int i = 0; i < series.length; i++) 
                    {
                        System.out.print("[" + i + "] ");
                        System.out.println (series[i]);
                    }
                    
                    System.out.print("\nDigite o número da série escolhida: ");
                    if (console.hasNextInt()) 
                    {
                        int num = console.nextInt();
                        console.nextLine();
                        
                        if (num < 0 || num >= series.length) 
                        {
                            System.err.println("Número inválido!");
                        } else 
                        {
                            System.out.println("Episódios da série " + series[num].getNome() + ":");
                            Episodio[] episodios = arqEpisodios.readEpisodiosSerie(series[num].getID());
                            
                            if (episodios != null && episodios.length > 0) 
                            {
                                int temporadaAtual = -1;
                                for (Episodio ep : episodios) {
                                    if (ep.getTemporada() != temporadaAtual) 
                                    {
                                        temporadaAtual = ep.getTemporada();
                                        System.out.println("\nTemporada " + temporadaAtual + ":");
                                    }
                                    System.out.println (ep);
                                }
                            } else 
                            {
                                System.out.println("Nenhum episódio encontrado para esta série.");
                            }
                            dadosCorretos = true;
                        }
                    } else 
                    {
                        System.err.println("Entrada inválida! Digite um número válido.");
                        console.nextLine(); // Limpar buffer
                    }
                } else 
                {
                    System.out.println("Nenhuma série encontrada com esse nome.");
                    dadosCorretos = true;
                }
            } catch (Exception e) 
            {
                System.out.println("Erro ao buscar a série: " + e.getMessage());
                dadosCorretos = true;
            }
        } while (!dadosCorretos);
    }

    public void povoar () throws Exception 
    {
        arqSeries.create(new Serie("Fullmetal Alchemist: Brotherhood", LocalDate.of(2009, 4, 5), "Dois irmãos usam alquimia para tentar recuperar o que perderam, enfrentando consequências sombrias.", "Crunchyroll", "Ação/Fantasia", (short) 14));
        arqSeries.create(new Serie("Stranger Things", LocalDate.of(2017, 1, 10), "Crianças enfrentam forças sobrenaturais em uma pequena cidade.", "Netflix", "Sobrenatural/Mistério", (short) 14));
        arqSeries.create(new Serie("Shingeki no Kyojin (Attack on Titan)", LocalDate.of(2013, 4, 6), "Humanos lutam pela sobrevivência contra gigantes devoradores em um mundo brutal.", "Crunchyroll", "Ação/Fantasia", (short) 16));
        arqSeries.create(new Serie("Kimetsu no Yaiba (Demon Slayer)", LocalDate.of(2019, 4, 6), "Tanjiro se torna caçador de demônios para salvar sua irmã e vingar sua família.", "Crunchyroll", "Ação/Sobrenatural", (short) 14));
        arqSeries.create(new Serie("Boku no Hero Academia (My Hero Academia)", LocalDate.of(2016, 4, 3), "Em um mundo de heróis com superpoderes, um garoto sem poderes sonha em se tornar símbolo da paz.", "Crunchyroll", "Ação/Super-heróis", (short) 12));
        arqSeries.create(new Serie("Steins;Gate", LocalDate.of(2011, 4, 6), "Cientistas acidentais descobrem uma maneira de enviar mensagens ao passado, afetando o futuro.", "Funimation", "Ficção Científica/Thriller", (short) 14));
        arqSeries.create(new Serie("Tokyo Revengers", LocalDate.of(2021, 4, 11), "Um jovem volta no tempo para impedir a morte de sua ex-namorada e mudar o rumo da sua vida.", "Crunchyroll", "Ação/Drama", (short) 16));
        arqSeries.create(new Serie("Jujutsu Kaisen", LocalDate.of(2020, 10, 3), "Estudantes enfrentam maldições perigosas para proteger a humanidade.", "Crunchyroll", "Ação/Sobrenatural", (short) 16));
        arqSeries.create(new Serie("Psycho-Pass", LocalDate.of(2012, 10, 12), "Em um futuro distópico, a justiça é decidida por um sistema que mede a mente humana.", "Crunchyroll", "Ficção Científica/Policial", (short) 18));
        arqSeries.create(new Serie("Black Mirror", LocalDate.of(2015, 10, 25), "Cada episódio apresenta uma história distópica sobre o impacto da tecnologia.", "Netflix", "Ficção Científica/Drama", (short) 18));
    } 

}

//////////////////////////////////////////////////