package com.example.healthyplus.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthyplus.Activity.BasicInfo.GenderActivity;
import com.example.healthyplus.DAO.AccountDAO;
import com.example.healthyplus.DAO.UserDAO;
import com.example.healthyplus.Database.PasswordHasher;
import com.example.healthyplus.Model.Account;
import com.example.healthyplus.Model.User;
import com.example.healthyplus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    EditText email, username, password, password2;
    TextView txvDangNhap;
    Button btnDangKy;
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        btnDangKy = findViewById(R.id.btnDangKy);
        email = findViewById(R.id.edtEmail);
        username = findViewById(R.id.edtName);
        password = findViewById(R.id.edtPassword);
        password2 = findViewById(R.id.edtPassword2);
        txvDangNhap = findViewById(R.id.txvDangNhap);

        txvDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEmpty(email))
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                else if (isEmpty(username))
                    Toast.makeText(RegisterActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                else if (isEmpty(password))
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                else if (isEmpty(password2))
                    Toast.makeText(RegisterActivity.this, "Nhập mật khẩu xác nhận", Toast.LENGTH_SHORT).show();
                else if (!password.getText().toString().trim().equals(password2.getText().toString().trim()))
                    Toast.makeText(RegisterActivity.this, "Mật khẩu trùng khớp", Toast.LENGTH_SHORT).show();
                else {
                    String user_email = email.getText().toString().trim();
                    String user_name = username.getText().toString().trim();
                    String user_password = password.getText().toString().trim();


                    auth.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");

                                        FirebaseUser currentUser = auth.getCurrentUser();

                                        // Create new User to use in BasicInfo activity package
                                        // We will need these user's info: id, username
                                        User user = new User();
                                        user.setId(currentUser.getUid());
                                        user.setName(user_name);

//                                    // Add new User to collection account in firestore, each document is
//                                       auto-generated userID
//                                    UserDAO userDAO = new UserDAO();
//                                    userDAO.addUser(userBase);

                                        // Add new Account to collection account in firestore,
                                        // each document is auto-generated accountID
                                        Account account = new Account();
                                        PasswordHasher passwordHasher = new PasswordHasher();
                                        String hashPassword = passwordHasher.hashPassword(user_password);
                                        account.setEmail(user_email);
                                        account.setPassword(hashPassword);
                                        account.setUserName(user_name);
                                        account.setUserID(currentUser.getUid());

                                        AccountDAO accountDAO = new AccountDAO();
                                        accountDAO.addAccount(account);
                                        account = null; // remove the object

                                        Intent intent = new Intent(getApplicationContext(), GenderActivity.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                    } else {
                                        // User registration failed
                                        Exception exception = task.getException();
                                        if (exception instanceof FirebaseAuthException) {
                                            String errorCode = ((FirebaseAuthException) exception).getErrorCode();

                                            if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(RegisterActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                                            } else if (errorCode.equals("ERROR_WEAK_PASSWORD")) {
                                                // Weak password (less than 6 characters)
                                                Toast.makeText(RegisterActivity.this, "Mật khẩu phải tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
                                            } else if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                                // Email address is already associated with another account
                                                Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                                            } else if (errorCode.equals("ERROR_INVALID_USER_TOKEN")) {
                                                // Invalid user token
                                                Toast.makeText(RegisterActivity.this, "Hard bug!", Toast.LENGTH_SHORT).show();
                                            } else if (errorCode.equals("ERROR_OPERATION_NOT_ALLOWED")) {
                                                // Account creation is disabled in the Firebase project
                                                Toast.makeText(RegisterActivity.this, "Hard bug!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Handle other exceptions
                                                exception.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
