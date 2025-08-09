package jp.ac.jec.cm0122.android114;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.ac.jec.cm0122.android114.API.NetworkManager;
import jp.ac.jec.cm0122.android114.Helpers.MediaPlayerService;
import jp.ac.jec.cm0122.android114.Models.Word;

public class CardActivity extends AppCompatActivity {
    private static final String TAG = "CardActivity";
    public static final String RND = "RANDOM_CARD";

    private List<Word> words = new ArrayList<>(); // 複数毎のカード情報
    private int pos; // 現在表示しているカードの位置
    Button btnPrev, btnNext;
    ImageButton btnListen;
    ToggleButton btnAnswer;
    TextView txtQuestionNumber, txtMeaning, txtPronunciation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnAnswer = findViewById(R.id.btnAnswer);
        txtQuestionNumber = findViewById(R.id.txtQuestion);
        txtMeaning = findViewById(R.id.txtMeaning);
        txtPronunciation = findViewById(R.id.txtPronunciation);
        btnListen = findViewById(R.id.btnPlaySound);
        btnPrev.setEnabled(pos != 0);  // 初期設定 PREVボタンを押せない状態にする

        Log.d(TAG, "pos: " + pos);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                words = NetworkManager.getAllWords(auth.getUid());
                if (words != null) {
                    Word word = words.get(pos);
                    runOnUiThread(() -> {
                        dispOneCard(word);
                        btnNext.setEnabled(words.size() - 1 > pos);
                    });
                } else {
                    runOnUiThread(() -> {
                        btnNext.setEnabled(false);
                        btnListen.setEnabled(false);
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });





        // TODO: 2023/12/04 ステップアップ 2: カードをランダムする
//        if (getIntent().getBooleanExtra(RND, false)) {
//            Collections.shuffle(ary);
//        }

        //画面更新を表示する

        btnNext.setOnClickListener(v -> {
            if (words.size() > pos) {
                pos++;
            } else {
                btnNext.setEnabled(false);
            }
            //     txtQuestionNumber.setText(getString(R.string.question_number, pos, ary.size()));
            btnPrev.setEnabled(true);
            btnAnswer.setChecked(false);

            if (pos == words.size()) {
                btnNext.setEnabled(false);
            } else {
                dispOneCard(words.get(pos)); //画面更新
            }
        });


        btnPrev.setOnClickListener(v -> {
            pos--;
         //   txtQuestionNumber.setText(getString(R.string.question_number, pos, words.size()));
            btnNext.setEnabled(true);
            btnPrev.setEnabled(pos != 0);
            btnAnswer.setChecked(false);
            dispOneCard(words.get(pos)); //画面更新

        });


        btnAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtMeaning.setVisibility(View.VISIBLE);
                txtPronunciation.setVisibility(View.VISIBLE);
            } else {
                txtMeaning.setVisibility(View.INVISIBLE);
                txtPronunciation.setVisibility(View.INVISIBLE);
            }
        });

        btnListen.setOnClickListener(v -> {
                if (words.get(pos).getAudioUrl().isEmpty()) {
                    Toast.makeText(this, "音声ファイルがありません", Toast.LENGTH_SHORT).show();
                    return;
                }
                MediaPlayerService.playSound(this, words.get(pos).getAudioUrl());
        });


    }

    //Viewを設定するメソッド
    private void dispOneCard(Word word) {
        //  Card temp = ary.get(pos); //現在位置のカード情報を取得する
        //  Word temp = words.get(0); //現在位置のカード情報を取得する
        //     txtQuestionNumber.setText(getString(R.string.question_number, temp.getId(), ary.size()));
        TextView txtEnglish = findViewById(R.id.txtEnglish);
        txtEnglish.setText(word.getEnglish()); //英語を表示させる

        txtMeaning.setText(word.getMeaning()); //日本語を表示させる
        txtMeaning.setVisibility(View.INVISIBLE); //初期状態では、日本語を非表示に

        txtPronunciation.setText(word.getPronunciation());
        txtPronunciation.setVisibility(View.INVISIBLE);
        btnNext.setEnabled(words.size() - 1 > pos);
    }
}