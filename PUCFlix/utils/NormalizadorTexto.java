package utils;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.*;

public class NormalizadorTexto {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "à", "o", "os", "as", "de", "do", "da", "das", "dos", "e", "em", "um", "uma", 
        "para", "por", "com", "no", "na", "nos", "nas", "que", "se", "ao", "aos", "às", "é", 
        "foi", "são", "ser", "ter", "há", "também"
    ));

    public static List<String> normalizar(String texto) {
        // 1. Remove acentos
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD);
        semAcento = semAcento.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // 2. Lowercase
        String lower = semAcento.toLowerCase();

        // 3. Remove pontuação
        String semPontuacao = lower.replaceAll("[^a-z0-9\\s]", " ");

        // 4. Tokenização
        String[] tokens = semPontuacao.split("\\s+");

        // 5. Remove stop words e palavras vazias
        List<String> palavras = new ArrayList<>();
        for (String token : tokens) {
            if (!STOP_WORDS.contains(token) && token.length() > 1) {
                palavras.add(token);
            }
        }

        return palavras;
    }
}
