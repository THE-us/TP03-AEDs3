//////////////////////////////////////////////////
// PACOTE

package atores;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS

import aeds3.*;
import atores.*;
import atuacoes.*;
import episodios.*;
import series.*;

//////////////////////////////////////////////////
// CLASSE MENUSEPISODIOS EM SI

import java.util.ArrayList;

public class ArquivoAtor extends Arquivo<Ator> {

    ArquivoSeries arqSerie = new ArquivoSeries ();
    
    ArvoreBMais<ParNomeSerieId> indiceNomeAtor;

    public ArquivoAtor () throws Exception {
        super ("ator", Ator.class.getConstructor ());

        indiceNomeAtor = new ArvoreBMais<> (
        ParNomeSerieId.class.getConstructor (),
        5,
        "./dados/" + nomeEntidade + "/indiceAtor.db");
    }

    @Override
    public int create (Ator a) throws Exception {
        int id = super.create (a);
        indiceNomeAtor.create (new ParNomeSerieId (a.getNome (), id));

        return id;
    }


    public Ator[] readNome (String nome) throws Exception {
        if (nome.length () == 0)
            return null;

        ArrayList<ParNomeSerieId> ptis = indiceNomeAtor.read (new ParNomeSerieId (nome, -1));
        if (ptis.size () > 0) {
            Ator[] atores = new Ator[ptis.size ()];
            int i = 0;

            for (ParNomeSerieId pti : ptis)
                atores[i++] = read (pti.getId ());
            return atores;
        } else
            return null;
    }

    public Ator[] readAtoresDaSerie (int id_Serie) throws Exception {
        if (id_Serie < 0)
            return null;

        ArquivoAtuacao arqAtuacao = new ArquivoAtuacao ();

        
        Atuacao[] atuacoes = arqAtuacao.readAtuacaoPorSerie (id_Serie);

        if (atuacoes != null) {
            Ator[] atores = new Ator[atuacoes.length];
            int i = 0;

            for (Atuacao e : atuacoes)
                atores[i++] = read (e.getIdAtor ());
            return atores;
        } else
            return null;
    }

    public Serie[] readSerieDoAtor (int id_ator) throws Exception {
        if (id_ator < 0)
            return null;

        ArquivoAtuacao arqAtuacao = new ArquivoAtuacao ();

        
        Atuacao[] atuacoes = arqAtuacao.readAtuacaoPorAtor (id_ator);

        if (atuacoes != null) {
            Serie[] series = new Serie[atuacoes.length];
            int i = 0;

            for (Atuacao e : atuacoes)
                series[i++] = arqSerie.read (e.getIdSerie ());
            return series;
        } else
            return null;
    }

    @Override
    public boolean delete (int id) throws Exception {
        Ator ator = read (id);
        if (ator != null) {
            if (super.delete (id))
                return indiceNomeAtor.delete (new ParNomeSerieId (ator.getNome (), id));
        }
        return false;
    }

    public boolean delete (String nome, int id) throws Exception {
        // Verifica se o ator está vinculado a algum atuacao
        ArquivoAtuacao arquivoAtuacao = new ArquivoAtuacao ();
        ArrayList<ParIdId> atuacao = arquivoAtuacao.indiceIdAtor_IdAtuacao.read (new ParIdId (id, -1));

        if (atuacao != null && atuacao.size () > 0) {
            throw new Exception ("Não é possível excluir o ator. Ele está vinculado a uma ou mais séries.");
        }

        return super.delete (id) && indiceNomeAtor.delete (new ParNomeSerieId (nome, id));
    }

    @Override
    public boolean update (Ator novoAtor) throws Exception {
        Ator ator = read (novoAtor.getID ());
        if (ator != null) {
            if (super.update (novoAtor)) {
                if (!ator.getNome ().equals (novoAtor.getNome ())) {
                    indiceNomeAtor.delete (new ParNomeSerieId (ator.getNome (), ator.getID ()));
                    indiceNomeAtor.create (new ParNomeSerieId (novoAtor.getNome (), ator.getID ()));
                }
                return true;
            }
        }
        return false;
    }

    public boolean atorExiste (int id_ator) throws Exception{
        Ator a = read (id_ator);   // na superclasse
        if (a != null) {
            return true;
        }
        return false;
    }
    
}
