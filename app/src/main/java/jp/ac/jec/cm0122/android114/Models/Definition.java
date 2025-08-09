package jp.ac.jec.cm0122.android114.Models;

public class Definition {
    private String definition;
    private Object[] synonyms;
    private Object[] antonyms;
    private String example;

    public String getDefinition() { return definition; }
    public void setDefinition(String value) { this.definition = value; }

    public Object[] getSynonyms() { return synonyms; }
    public void setSynonyms(Object[] value) { this.synonyms = value; }

    public Object[] getAntonyms() { return antonyms; }
    public void setAntonyms(Object[] value) { this.antonyms = value; }

    public String getExample() { return example; }
    public void setExample(String value) { this.example = value; }
}
