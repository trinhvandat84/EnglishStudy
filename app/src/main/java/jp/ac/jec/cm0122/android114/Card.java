package jp.ac.jec.cm0122.android114;

public class Card {
    private String japanese;
    private String english;
    private int id;

    public Card(String japanese, String english, int id) {
        this.japanese = japanese;
        this.english = english;
        this.id = id;
    }

    public String getJapanese() {
        return japanese;
    }

    public void setJapanese(String japanese) {
        this.japanese = japanese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
