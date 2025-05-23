//////////////////////////////////////////////////
// PACOTE

package atores;

//////////////////////////////////////////////////
// BIBLIOTECAS DO SISTEMA


import java.util.ArrayList;

//////////////////////////////////////////////////
// BIBLIOTECAS PRÓPRIAS

import aeds3.*;
import series.*;
import atuacoes.*;
//////////////////////////////////////////////////
// IMPLEMENTAÇÃO DE ATOR


public class ArquivoAtor extends Arquivo<Ator> {

    ArquivoSeries arqSerie = new ArquivoSeries();
    
    ArvoreBMais<ParTituloId> indiceNomeAtor;

    public ArquivoAtor() throws Exception {
        super("ator", Ator.class.getConstructor());

        indiceNomeAtor = new ArvoreBMais<>(
        ParTituloId.class.getConstructor(),
        5,
        "./dados/" + nomeEntidade + "/indiceAtor.db");
    }

    @Override
    public int create(Ator a) throws Exception {
        int id = super.create(a);
        indiceNomeAtor.create(new ParTituloId(a.getNome(), id));

        return id;
    }


    public Ator[] readNome(String nome) throws Exception {
        if (nome.length() == 0)
            return null;

        ArrayList<ParTituloId> ptis = indiceNomeAtor.read(new ParTituloId(nome, -1));
        if (ptis.size() > 0) {
            Ator[] atores = new Ator[ptis.size()];
            int i = 0;

            for (ParTituloId pti : ptis)
                atores[i++] = read(pti.getId());
            return atores;
        } else
            return null;
    }

    public Ator[] readAtoresDaSerie(int id_Serie) throws Exception {
        if (id_Serie < 0)
            return null;

        ArquivoAtuacao arqAtuacao = new ArquivoAtuacao();

        
        Atuacao[] atuacao = arqAtuacao.readAtuacaoPorSerie(id_Serie);

        if (atuacao != null) {
            Ator[] atores = new Ator[atuacao.length];
            int i = 0;

            for (Atuacao at : atuacao)
                atores[i++] = read(at.getID_ator ());
            return atores;
        } else
            return null;
    }

    public Serie[] readSerieDoAtor(int id_ator) throws Exception {
        if (id_ator < 0)
            return null;

        ArquivoAtuacao arqAtuacao = new ArquivoAtuacao();

        
        Atuacao[] atores = arqAtuacao.readAtuacaoPorAtor(id_ator);

        if (atores != null) {
            Serie[] series = new Serie[atores.length];
            int i = 0;

            for (Atuacao e : atores)
                series[i++] = arqSerie.read(e.getID_serie());
            return series;
        } else
            return null;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Ator ator = read(id);
        if (ator != null) {
            if (super.delete(id))
                return indiceNomeAtor.delete(new ParTituloId(ator.getNome(), id));
        }
        return false;
    }

    public boolean delete(String nome, int id) throws Exception {
        // Verifica se o ator está vinculado a algum Atuacao
        ArquivoAtuacao ArquivoAtuacao = new ArquivoAtuacao();
        ArrayList<ParIdId> Atuacao = ArquivoAtuacao.indiceIdAtor_IdAtuacao.read(new ParIdId(id, -1));

        if (Atuacao != null && Atuacao.size() > 0) {
            throw new Exception("Não é possível excluir o ator. Ele está vinculado a uma ou mais séries.");
        }

        return super.delete(id) && indiceNomeAtor.delete(new ParTituloId(nome, id));
    }

    @Override
    public boolean update(Ator novoAtor) throws Exception {
        Ator ator = read(novoAtor.getID());
        if (ator != null) {
            if (super.update(novoAtor)) {
                if (!ator.getNome().equals(novoAtor.getNome())) {
                    indiceNomeAtor.delete(new ParTituloId(ator.getNome(), ator.getID()));
                    indiceNomeAtor.create(new ParTituloId(novoAtor.getNome(), ator.getID()));
                }
                return true;
            }
        }
        return false;
    }

    public boolean atorExiste(int id_ator) throws Exception{
        Ator a = read(id_ator);   // na superclasse
        if(a != null) {
            return true;
        }
        return false;
    }
    
}

//////////////////////////////////////////////////