package jp.ac.jec.cm0122.android114;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText txtEmail, txtPassword;
    String email, password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    email = s.toString();
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    password = s.toString();
            }
        });



        findViewById(R.id.signUp).setOnClickListener(v -> {
            SignUpActivity.start(this, email, password);
        });


        // TODO: 2025/02/02 LOGIN ACTION 
        findViewById(R.id.signIn).setOnClickListener(v -> {
            if (txtEmail.getText().toString().isEmpty()) {
                Toast.makeText(this, "メールを入力して下さい", Toast.LENGTH_SHORT).show();
                return;
            } else if (txtPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, "パスワードを入力して下さい", Toast.LENGTH_SHORT).show();
                return;
            }
                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    updateUI(currentUser);
                                } else {
                                    Exception exception = task.getException();
                                    String errorMessage = "ログインに失敗しました。もう一度お試しください。";
                                    if (exception instanceof FirebaseAuthInvalidUserException) {
                                        errorMessage = "このメールアドレスは登録されていません。";
                                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                        errorMessage = "メールアドレスまたはパスワードが間違っています。";
                                    } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                        errorMessage = "このメールアドレスは既に使用されています。";
                                    } else if (exception instanceof FirebaseAuthWeakPasswordException) {
                                        errorMessage = "パスワードが脆弱すぎます。より強力なパスワードを設定してください。";
                                    }

                                    Log.e(TAG, "ログインエラー: " + exception.getMessage());
                                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            MainActivity.start(this, currentUser);
        }
     }

     private void updateUI(FirebaseUser user) {
        MainActivity.start(this, user);
     }

}