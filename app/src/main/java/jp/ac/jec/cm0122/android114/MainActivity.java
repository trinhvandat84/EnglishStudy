package jp.ac.jec.cm0122.android114;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    public boolean checkRandom;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        Intent userIntent = getIntent();
        TextView displayName = findViewById(R.id.displayName);
        displayName.setText("こんにちは " + userIntent.getStringExtra("username"));

        findViewById(R.id.logOut).setOnClickListener(v -> {

            mAuth.signOut();
            updateUI();
        });

      //  CheckBox cbxRandom = (CheckBox) findViewById(R.id.cbxRandom);

        // TODO: 2023/12/04  Checkbox: カードをランダムするかどうか?
//        cbxRandom.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            checkRandom = isChecked;
//            Log.d(TAG, "checkRandom: " + checkRandom);
//        });

        findViewById(R.id.btnStart).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CardActivity.class);
            // チェックボクスの結果をCardActivityに送る
            intent.putExtra(CardActivity.RND, checkRandom);
            startActivity(intent);
        });

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.showPracticeSpeakView).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PracticeSpeak.class);
            startActivity(intent);
        });

        findViewById(R.id.btnMyWords).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyWords.class);
            startActivity(intent);
        });

    }


    public static void start(Context context, FirebaseUser user) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra("username", user.getDisplayName());
        context.startActivity(starter);
    }

    private void updateUI() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}