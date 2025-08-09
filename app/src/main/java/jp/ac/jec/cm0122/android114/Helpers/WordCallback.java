package jp.ac.jec.cm0122.android114.Helpers;

import java.util.List;

import jp.ac.jec.cm0122.android114.Models.Word;

public interface WordCallback {
    void onSuccess(List<Word> words);
    void onError(Exception e);
}

