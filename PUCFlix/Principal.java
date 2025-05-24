///////////////////O-Pear-ation///////////////////
// Bibliotecas do sistema

import java.util.List;
import java.util.Scanner;

import aeds3.ElementoLista;
import aeds3.ListaInvertida;

//////////////////////////////////////////////////
// Bibliotecas próprias

import episodios.*;
import series.*;
import utils.NormalizadorTexto;
import atuacoes.*;

//////////////////////////////////////////////////
// Classe Principal

public class Principal 
{

    public static void main (String args []) 
    {
        // VARIÁVEIS
        String servico  = "PUCFLIX";
        Scanner console =      null;

        //ambiente de teste------------------------------------------------
        String titulo = "Programação básica: uma introdução à programação";
        List<String> termos = NormalizadorTexto.normalizar(titulo);

        System.out.println("Palavras normalizadas:");
        for (String termo : termos) {
            System.out.println("- " + termo);
        }

        try{
            ListaInvertida li = new ListaInvertida(3, "dic.db", "blk.db");
            li.create("teste", new ElementoLista(1, 0.5f));
            li.create("teste", new ElementoLista(2, 0.7f));
            ElementoLista[] resultados = li.read("teste");
            for (ElementoLista el : resultados) {
            System.out.println(el);
            }
        }catch(Exception e){
            System.out.println("Algo deu errado"); 
        }   

        //-----------------------------------------------------------------
        
        try 
        {

            console = new Scanner (System.in);
            int opcao;
            do {

                System.out.println (servico);
                System.out.println ( "-----------");
                System.out.println ("> Início");
                System.out.println ("1 - Series");
                System.out.println ("2 - Episodios");
                System.out.println("3 - Atores");
                System.out.println ("4 - Povoar");
                System.out.println ("0 - Sair");
                
                System.out.print("\nOpção: ");
                try 
                {
                    opcao = Integer.valueOf(console.nextLine ());
                } 
                catch(NumberFormatException e) 
                {
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
                        (new MenuSeries ()).menu (servico);
                    break;

                    case 2:
                        (new MenuEpisodios ()).menu (servico);
                    break;

                    case 3:
                        (new MenuAtuacao()).menu (servico);
                    break;

                    case 4:
                        (new MenuSeries ()).povoar ();
                        (new MenuEpisodios ()).povoar ();
                        (new MenuAtuacao ()).povoar ();
                    break;

                    case 0:
                    break;

                    default:
                        System.out.println ("Opção inválida!");
                    break;
                }

            } 
            while (opcao != 0);

        } 
        catch(Exception e) 
        {
            e.printStackTrace ();
        }
    }

}

//////////////////////////////////////////////////z