package jp.ac.jec.cm0122.android114.Models;

public class Meaning {
    private String partOfSpeech;
    private Definition[] definitions;
    private String[] synonyms;
    private String[] antonyms;

    public String getPartOfSpeech() { return partOfSpeech; }
    public void setPartOfSpeech(String value) { this.partOfSpeech = value; }

    public Definition[] getDefinitions() { return definitions; }
    public void setDefinitions(Definition[] value) { this.definitions = value; }

    public String[] getSynonyms() { return synonyms; }
    public void setSynonyms(String[] value) { this.synonyms = value; }

    public String[] getAntonyms() { return antonyms; }
    public void setAntonyms(String[] value) { this.antonyms = value; }
}
