package jp.ac.jec.cm0122.android114;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText username, email, password;
    String strUserName, strEmail, strPassword;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    strUserName = s.toString();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strEmail = s.toString();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strPassword = s.toString();
            }
        });

        findViewById(R.id.signUp).setOnClickListener(v -> {

            if (username.getText().toString().isEmpty()) {
                Toast.makeText(this, "ユーザー名を入力して下さい", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.getText().toString().isEmpty()) {
                Toast.makeText(this, "メールを入力して下さい", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, "パスワードを入力して下さい", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!strEmail.isEmpty() && !strPassword.isEmpty() && !strUserName.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileChangeRequest =
                                        new UserProfileChangeRequest.Builder()
                                                .setDisplayName(strUserName)
                                                .build();

                                if (user != null) {
                                    user.updateProfile(profileChangeRequest);
                                    updateUI(user);
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        });
            }
        });

        findViewById(R.id.signIn).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }


    public static void start(Context context, String email, String password) {
        Intent starter = new Intent(context, SignUpActivity.class);
        starter.putExtra("email", email);
        starter.putExtra("password", password);
        context.startActivity(starter);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            MainActivity.start(this, user);
        } else {
            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}