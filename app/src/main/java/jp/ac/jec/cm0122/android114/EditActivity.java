package jp.ac.jec.cm0122.android114;

import static jp.ac.jec.cm0122.android114.CardSQLiteOpenHelper.TABLE_NAME;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.ac.jec.cm0122.android114.API.NetworkManager;
import jp.ac.jec.cm0122.android114.API.TranslatorText;
import jp.ac.jec.cm0122.android114.Models.Definition;
import jp.ac.jec.cm0122.android114.Models.DictionaryResponse;
import jp.ac.jec.cm0122.android114.Models.Meaning;
import jp.ac.jec.cm0122.android114.Models.Phonetic;
import jp.ac.jec.cm0122.android114.Models.TranslationResponse;
import jp.ac.jec.cm0122.android114.Models.Word;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";
    EditText editTextEnglish, editTextJapanese;
    String english, japanese, pronunciation, meaning, soundUrl;
    StringBuilder definition;
    MediaPlayer mediaPlayer;
    TextView txtPronunciation;
    EditText txtMeaning;
    TranslatorText translatorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        translatorText = new TranslatorText();
        editTextEnglish = findViewById(R.id.edtEnglish);
        editTextJapanese = findViewById(R.id.edtJapanese);

        txtPronunciation = findViewById(R.id.txtPronunciation);
        txtMeaning = findViewById(R.id.txtMeaning);

        txtMeaning.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    meaning = editTextJapanese.getText().toString() + "\n" + s.toString();
            }
        });


        editTextEnglish.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    runOnUiThread(() -> editTextJapanese.setText(""));
                }

                english = s.toString();

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        String response = translatorText.Post(s.toString());
                        Gson gson = new Gson();
                        List<TranslationResponse> translationResponses = gson.fromJson(response, new TypeToken<List<TranslationResponse>>() { }.getType());
                        TranslationResponse dictionaryResponse = translationResponses.get(0);
                        japanese = dictionaryResponse.getTranslations()[0].getText();
                        runOnUiThread(() -> editTextJapanese.setText(japanese));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            mediaPlayer = null;
            english = editTextEnglish.getText().toString();
            if (!english.isEmpty()) {
                Executor executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        List<DictionaryResponse> wordData = NetworkManager.getJson(english);
                        if (wordData != null) {
                            runOnUiThread(() -> {
                                if (wordData.get(0).getPhonetics()[0].getText() == null) {
                                    Toast.makeText(EditActivity.this, "検索結果がありません", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!wordData.get(0).getPhonetics()[0].getText().isEmpty()) {
                                    txtPronunciation.setText(wordData.get(0).getPhonetics()[0].getText());
                                    pronunciation = wordData.get(0).getPhonetics()[0].getText();
                                }
                                Meaning[] meanings = wordData.get(0).getMeanings();
                                int count = 0; // Biến đếm số lượng nghĩa đã lấy
                                for (Meaning meaning : meanings) {
                                    for (Definition definition : meaning.getDefinitions()) {
                                        if (count < 2) { // Chỉ lấy hai nghĩa đầu tiên
                                            if (this.definition == null) {
                                                this.definition = new StringBuilder();
                                                this.definition.append(definition.getDefinition());
                                            } else {
                                                this.definition.append("\n").append(definition.getDefinition());
                                            }
                                            count++; // Tăng biến đếm sau mỗi nghĩa được thêm
                                        } else {
                                            break; // Dừng vòng lặp nếu đã lấy đủ hai nghĩa
                                        }
                                    }
                                    if (count >= 2) break; // Thoát khỏi vòng lặp ngoài nếu đã đủ hai nghĩa
                                }

                                String englishMeaning = this.definition.toString();
                            //    txtMeaning.setText(this.definition);
//                                this.meaning = this.definition.toString();
                                Executor executor = Executors.newSingleThreadExecutor();
                                executor.execute(() -> {
                                    try {
                                        String response = translatorText.Post(englishMeaning);
                                        Gson gson = new Gson();
                                        List<TranslationResponse> translationResponses = gson.fromJson(response, new TypeToken<List<TranslationResponse>>() { }.getType());
                                        TranslationResponse dictionaryResponse = translationResponses.get(0);
                                        String japaneseMeaning = dictionaryResponse.getTranslations()[0].getText();
                                        this.meaning = japaneseMeaning;
                                        runOnUiThread(() -> txtMeaning.setText(japaneseMeaning));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                                editTextJapanese.setText(japanese);
                            });

                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .build()
                            );

                            Phonetic[] phonetics = wordData.get(0).getPhonetics();
                            for (Phonetic phonetic : phonetics) {
                                if (phonetic.getAudio() != null && !phonetic.getAudio().isEmpty()) {
                                    soundUrl = phonetic.getAudio();
                                    mediaPlayer.setDataSource(soundUrl);
                                    Log.d(TAG, "soundUrl: " + soundUrl);
                                    break;
                                }
                            }
                        }
                    } catch (IOException e) {
                       e.printStackTrace();
                    }
                });

            }
        });


        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            if (soundUrl != null) {
                playSound(soundUrl);
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(v -> {

            if (editTextEnglish.getText().toString().isEmpty() || editTextJapanese.getText().toString().isEmpty() || txtMeaning.getText().toString().isEmpty()) {
                Toast.makeText(this, "入力欄に文字を埋めてください", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                Word word = new Word();
                word.setEnglish(english);
                word.setMeaning(meaning);
                if (pronunciation != null) {
                    word.setPronunciation(pronunciation);
                } else {
                    word.setPronunciation("");
                }

                if (soundUrl == null) {
                    soundUrl = "";
                } else {
                    word.setAudioUrl(soundUrl);
                }
                NetworkManager.addWord(currentUser, word);
                Toast.makeText(this, word.getEnglish() +"を追加しました", Toast.LENGTH_SHORT).show();
                english = "";
                japanese = "";
                pronunciation = "";
                meaning = "";
                soundUrl = "";
                editTextEnglish.setText("");
                txtMeaning.setText("");
                txtPronunciation.setText("");
            }

            //空入力をチェックする
//            if (!TextUtils.isEmpty(editTextEnglish.getText().toString()) && !TextUtils.isEmpty(editTextJapanese.getText().toString())) {
//
//                String english = editTextEnglish.getText().toString();
//                String japanese = editTextJapanese.getText().toString();
//                // TODO: 2023/12/04 登録しようデータがデータベースにすでに存在したかどうかをチェックする
//                if (isExistWord(english, japanese)) {
//                    Toast.makeText(EditActivity.this, "すでに存在している単語です", Toast.LENGTH_SHORT).show();
//                } else {
//                    insert(english, japanese);
//                }
//
//            } else {
//                Toast.makeText(EditActivity.this, "入力欄に文字を埋めてください", Toast.LENGTH_SHORT).show();
//            }
        });

        //Main画面に戻る
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
    }

    /**
     * データをデータベースに登録するメソッド
     *
     * @param english  英単語
     * @param meaning 意味
     */
    public void insert(String english, String meaning) {

    }

    /**
     * 同じ単語をチェックするメソッド
     *
     * @param english  英語
     * @param japanese 日本語
     * @return true or false
     */
    public boolean isExistWord(String english, String japanese) {
        CardSQLiteOpenHelper helper = new CardSQLiteOpenHelper(EditActivity.this);
        try {
            final SQLiteDatabase database = helper.getReadableDatabase();
            String[] column = new String[]{"english", "japanese"};
            Cursor cursor = database.query(TABLE_NAME, column, null, null, null, null, null);
            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals(english) || cursor.getString(1).equals(japanese)) {
                    cursor.close();
                    return true;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "isExistWord: ", e);
            e.printStackTrace();
        }
        return false;
    }

    private void playSound(@NonNull final String url) {
        Uri uri = Uri.parse(url);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(this,uri);
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);

        mediaPlayer.setOnCompletionListener(mp -> {

        });

    }


}