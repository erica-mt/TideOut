package com.example.tideout;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {

    private Button logoutButton, deleteAccountButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        logoutButton = findViewById(R.id.btnLogout);
        deleteAccountButton = findViewById(R.id.btnDeleteAccount);

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(AccountActivity.this, MainActivity.class));
            finish();
        });

        deleteAccountButton.setOnClickListener(v -> confirmDeleteAccount());
        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void confirmDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar conta");

        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Insira a sua password.");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setView(passwordInput);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            if (!password.isEmpty()) {
                deleteAccount(password);
            } else {
                Toast.makeText(AccountActivity.this, "A password é obrigatória.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void deleteAccount(String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        String userId = user.getUid();
                        databaseReference.child(userId).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(AccountActivity.this, "Conta eliminada.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AccountActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(AccountActivity.this, "Falha ao eliminar conta.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(AccountActivity.this, "Falha ao remover dados do utilizador.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(AccountActivity.this, "Falha na reautenticação. Password incorrecta?", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(AccountActivity.this, "Erro ao obter email.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
