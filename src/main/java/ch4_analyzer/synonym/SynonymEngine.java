package ch4_analyzer.synonym;

import java.io.IOException;

public interface SynonymEngine {
    String[] getSynonyms(String s) throws IOException;
}
