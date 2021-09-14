package com.example.budgetbuddy.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.helper.Utils;
import com.example.budgetbuddy.model.SecQuestion;
import com.example.budgetbuddy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
public class ActSecurityQuestion extends ActBase {
    Spinner spinQuestion;
    EditText edtAnswer;
    Button btnDone;
    ArrayAdapter<String> adapterQuestion;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String uid, question, answer;
    Utils utils = new Utils();
    User user;
    private SecQuestion secQuestion;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_security_question);
        initialization();
        setSpinner();
        clickListener();
        getData();
    }
    private void getData() {
        user = gson.fromJson((String) getIntent().getSerializableExtra("user_detail"), User.class);
    }
    private void clickListener() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer = edtAnswer.getText().toString();
                if (answerValidation(answer)) {
                    showLoader();
                    secQuestion.setAnswer(answer);
                    secQuestion.setQuestion(question);
                    user.setUser_answer(answer);
                    user.setUser_sec_question(question);
                    mAuth.createUserWithEmailAndPassword(user.getUser_email_id(), user.getUser_password()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                closeLoader();
                                final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                if (current_user != null) {
                                    uid = current_user.getUid();
                                    mDatabase.child(uid).child("user_details").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Prefs.putString(getResources().getString(R.string.user_id), uid);
                                                Prefs.putString(getResources().getString(R.string.user), gson.toJson(user));
                                                mDatabase.child("sec_question").child(user.getUser_email_id().replace(".", "")).setValue(secQuestion);
                                                Intent mainIntent = new Intent(ActSecurityQuestion.this, ActMain.class);
                                                startActivity(mainIntent);
                                                ActivityCompat.finishAffinity(ActSecurityQuestion.this);
                                                finish();
                                                Log.e("final_data", gson.toJson(user));
                                                Toast.makeText(ActSecurityQuestion.this, "Account created", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            closeLoader();
                                            Log.e("DatabaseError", e.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            closeLoader();
                            Toast.makeText(ActSecurityQuestion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Sign up error", e.getMessage().toString());
                        }
                    });
                }
            }
        });
    }
    private void setSpinner() {
        spinQuestion.setAdapter(adapterQuestion);
        spinQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("selected_question", adapterQuestion.getItem(i));
                question = adapterQuestion.getItem(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                question = adapterQuestion.getItem(0);
            }
        });
    }
    private void initialization() {
        //view
        spinQuestion = findViewById(R.id.spin_questions);
        edtAnswer = findViewById(R.id.edt_answer);
        btnDone = findViewById(R.id.btn_done);
        //adapter
        adapterQuestion = new ArrayAdapter<String>(ActSecurityQuestion.this, android.R.layout.simple_list_item_1
                , getResources().getStringArray(R.array.questions));
        adapterQuestion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Gson
        gson = new Gson();
        //model class
        secQuestion = new SecQuestion();
    }
    private boolean answerValidation(String answer) {
        if (!utils.isStringValidate(answer)) {
            utils.yoyoAnimation(edtAnswer);
            return false;
        }
        return true;
    }
}