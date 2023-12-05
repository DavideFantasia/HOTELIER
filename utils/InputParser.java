package utils;

import java.util.*;

/**
 * classe che spezza l'input in arrivo dal client
 * ha lo scopo di non dividere in sottostringhe tutte le parole che si trovano fra " ... "
 * Quando trova un carattere virgoletta ", cambia lo stato withinQuotes, 
 * permettendo di mantenere le parole racchiuse tra virgolette come singole entità. 
 * Quando trova uno spazio " " fuori dalle virgolette, 
 * aggiunge la parola corrente alla lista dei token.
 */
public class InputParser {
    public static String[] parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean withinQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '\"') {
                withinQuotes = !withinQuotes;
            } else if (c == ' ' && !withinQuotes){
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(new String[0]);
    }
}