///////////////////O-Pear-ation///////////////////
// Bibliotecas do sistema

import java.util.Scanner;

//////////////////////////////////////////////////
// Bibliotecas próprias

import episodios.*;
import  atuacoes.*;
import    series.*;

//////////////////////////////////////////////////
// Classe Principal

public class Principal 
{

    public static void main (String args []) 
    {
        // VARIÁVEIS
        String servico  = "PUCFLIX 3.0";
        Scanner console =      null;
        
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
                System.out.println ("3 - Atuacao");
                System.out.println ("4 - Povoar");
                System.out.println ("0 - Sair");
                
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
                        (new MenuSeries ()).menu (servico); // erro do bem
                    break;

                    case 2:
                        (new MenuEpisodios ()).menu (servico); // erro do bem
                    break;

                    case 3:
                        (new MenuAtuacao ()).menu (servico); // erro do bem
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
        catch (Exception e) 
        {
            e.printStackTrace ();
        }
    }

}

//////////////////////////////////////////////////z