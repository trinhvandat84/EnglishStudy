package jp.ac.jec.cm0122.android114.Models;

public class DictionaryResponse {
    private String word;
    private Phonetic[] phonetics;
    private Meaning[] meanings;
    private License license;
    private String[] sourceUrls;

    public String getWord() { return word; }
    public void setWord(String value) { this.word = value; }

    public Phonetic[] getPhonetics() { return phonetics; }
    public void setPhonetics(Phonetic[] value) { this.phonetics = value; }

    public Meaning[] getMeanings() { return meanings; }
    public void setMeanings(Meaning[] value) { this.meanings = value; }

    public License getLicense() { return license; }
    public void setLicense(License value) { this.license = value; }

    public String[] getSourceUrls() { return sourceUrls; }
    public void setSourceUrls(String[] value) { this.sourceUrls = value; }
}
