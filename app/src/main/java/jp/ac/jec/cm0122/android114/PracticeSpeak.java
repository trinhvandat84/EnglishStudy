package jp.ac.jec.cm0122.android114;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import jp.ac.jec.cm0122.android114.API.GeminiAPI;
import jp.ac.jec.cm0122.android114.Helpers.GeminiCallback;
import jp.ac.jec.cm0122.android114.Services.SpeechSynthesis;

public class PracticeSpeak extends AppCompatActivity {

    private static final String TAG = "PracticeSpeak";
    TextView responseUser, messageAI;
    GeminiAPI geminiAPI;
    SpeechSynthesis speechSynthesis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practice_speak);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        geminiAPI = new GeminiAPI();
        speechSynthesis = new SpeechSynthesis();
        responseUser = findViewById(R.id.responseUser);
        messageAI = findViewById(R.id.responseAI);

        findViewById(R.id.btnSpeak).setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            ArrayList<String> text = null;
            if (data != null) {
                text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            }
            if (text != null) {
                String responseUserTxt = text.get(0);
                responseUser.setText(responseUserTxt);
                geminiAPI.speakWithAI(responseUserTxt, new GeminiCallback() {
                    @Override
                    public void onResponse(String response) {
                        runOnUiThread(() -> messageAI.setText(response));
                            speechSynthesis.textToSpeech(response);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechSynthesis.onStopButtonClicked();
    }
}