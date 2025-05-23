package utils;

import java.util.*;
import series.*;
import episodios.*;
import aeds3.*;
import utils.*;

public class IndexadorSeries {

    public static void indexarSerie(Serie serie, ListaInvertida indice) throws Exception {
        if (serie == null || indice == null) return;

        List<String> palavras = NormalizadorTexto.normalizar(serie.getNome());

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                ElementoLista elemento = new ElementoLista(serie.getID(), 1.0f);
                indice.create(palavra, elemento);
            }
        }
    }
}