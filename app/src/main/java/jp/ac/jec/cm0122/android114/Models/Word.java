package jp.ac.jec.cm0122.android114.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Word implements Serializable {
    private int id;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("word")
    private String english;
    private String meaning;
    private String pronunciation;
    @SerializedName("audio_url")
    private String audioUrl;



    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getEnglish() {
        return english;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getPronunciation() {
        return pronunciation;
    }


    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", english='" + english + '\'' +
                ", meaning='" + meaning + '\'' +
                ", pronunciation='" + pronunciation + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return id == word.id && Objects.equals(userId, word.userId) && Objects.equals(english, word.english) && Objects.equals(meaning, word.meaning) && Objects.equals(pronunciation, word.pronunciation) && Objects.equals(audioUrl, word.audioUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, english, meaning, pronunciation, audioUrl);
    }
}
