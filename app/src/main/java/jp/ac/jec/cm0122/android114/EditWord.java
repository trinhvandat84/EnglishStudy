package jp.ac.jec.cm0122.android114;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.ac.jec.cm0122.android114.API.NetworkManager;
import jp.ac.jec.cm0122.android114.Helpers.UpdateWordCallback;
import jp.ac.jec.cm0122.android114.Models.Word;

public class EditWord extends AppCompatActivity {

    TextView txtEnglish, txtPronunciation;
    EditText edtMeaning;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_word);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        Word word = (Word) intent.getSerializableExtra("word");

        txtEnglish = findViewById(R.id.txtEnglish);
        txtPronunciation = findViewById(R.id.txtPronunciation);
        edtMeaning = findViewById(R.id.edtMeaning);
        btnUpdate = findViewById(R.id.btnUpdate);

        txtEnglish.setText(word.getEnglish());
        txtPronunciation.setText(word.getPronunciation());
        edtMeaning.setText(word.getMeaning());

        btnUpdate.setOnClickListener(v -> {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                NetworkManager.updateWord(txtEnglish.getText().toString(), edtMeaning.getText().toString(), new UpdateWordCallback() {
                    @Override
                    public void onSuccess(boolean result) {
                        runOnUiThread(() -> {
                            Toast.makeText(EditWord.this, txtEnglish.getText().toString() + "の意味を更新しました。", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            });
        });

    }


    public static void start(Context context, Word word) {
        Intent starter = new Intent(context, EditWord.class);
        starter.putExtra("word", word);
        context.startActivity(starter);
    }
}