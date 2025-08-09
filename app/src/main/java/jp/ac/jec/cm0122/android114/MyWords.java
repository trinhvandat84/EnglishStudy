package jp.ac.jec.cm0122.android114;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.ac.jec.cm0122.android114.API.NetworkManager;
import jp.ac.jec.cm0122.android114.Helpers.WordCallback;
import jp.ac.jec.cm0122.android114.Models.Word;

public class MyWords extends AppCompatActivity {

    List<Word> words;
    private RecyclerView recyclerView;
    private WordAdapter wordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_words);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        words = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        wordAdapter = new WordAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getWords(new WordCallback() {
            @Override
            public void onSuccess(List<Word> words) {
                runOnUiThread(() -> {
                    wordAdapter.setData(words);
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("getWords", "Lỗi: " + e.getMessage());
            }
        });

        recyclerView.setAdapter(wordAdapter);
    }

    private void getWords(WordCallback callback) {

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                words = NetworkManager.getAllWords(auth.getUid());
                callback.onSuccess(words);
            } catch (IOException e) {
                callback.onError(e);
                throw new RuntimeException(e);
            }
        });
    }
}