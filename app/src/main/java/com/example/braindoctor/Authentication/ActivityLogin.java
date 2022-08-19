package com.example.braindoctor.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.braindoctor.HttpUtils.HttpUtils;
import com.example.braindoctor.InformationDisplay.ActivityHomeDoctor;
import com.example.braindoctor.InformationDisplay.ActivityHomePatient;
import com.example.braindoctor.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityLogin extends AppCompatActivity {

    private ImageView exitLogin;
    private TextInputEditText loginAccountEdit;
    private TextInputEditText loginPasswordEdit;
    private CheckBox passwordRememberCheckbox;
    private RadioButton choosePatient;
    private RadioButton chooseDoctor;
    private TextView entranceRegister;
    private Button entranceHome;
    private CheckBox loginProtocolCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initActivity();
    }

    private void initActivity() {
        exitLogin = findViewById(R.id.exitLogin);
        loginAccountEdit = findViewById(R.id.loginAccountEdit);
        loginPasswordEdit = findViewById(R.id.loginPasswordEdit);
        passwordRememberCheckbox = findViewById(R.id.passwordRememberCheckbox);
        choosePatient = findViewById(R.id.loginChoosePatient);
        chooseDoctor = findViewById(R.id.loginChooseDoctor);
        entranceRegister = findViewById(R.id.entranceRegister);
        entranceHome = findViewById(R.id.entranceHome);
        loginProtocolCheckbox = findViewById(R.id.loginProtocolCheckbox);

        exitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        entranceRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });

        entranceHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = Objects.requireNonNull(loginAccountEdit.getText()).toString();
                final String password = Objects.requireNonNull(loginPasswordEdit.getText()).toString();
                final String accountType = choosePatient.isChecked() ? "patient" : (chooseDoctor.isChecked() ? "doctor" : "noType");
                if (account.length() == 0) {
                    Toast.makeText(ActivityLogin.this, "请输入账号", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(ActivityLogin.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (accountType.equals("noType")) {
                    Toast.makeText(ActivityLogin.this, "请选择账号类型", Toast.LENGTH_SHORT).show();
                } else {
                    //登录接口调用
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpUtils.Login(account, password, accountType, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(responseData);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    assert jsonObject != null;
                                    try {
                                        final boolean status = jsonObject.getBoolean("status");
                                        final String msg = jsonObject.getString("msg");
                                        if (status) {
                                            final String uid = jsonObject.getString("uid");
                                            SharedPreferences sharedPreferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("accountUid", uid);
                                            editor.putString("accountType", accountType);
                                            if (passwordRememberCheckbox.isChecked()) {
                                                editor.putString("account", account);
                                                editor.putString("accountPassword", password);
                                            }
                                            editor.apply();
                                            if (accountType.equals("patient")) {
                                                Intent intent = new Intent(ActivityLogin.this, ActivityHomePatient.class);
                                                intent.putExtra("lastStop", "activityLogin");
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Intent intent = new Intent(ActivityLogin.this, ActivityHomeDoctor.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ActivityLogin.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }).start();

                }
            }
        });

        loginProtocolCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entranceHome.setEnabled(true);
                }else {
                    entranceHome.setEnabled(false);
                }
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
        String account = sharedPreferences.getString("account", "");
        String accountPassword = sharedPreferences.getString("accountPassword", "");
        String accountType = sharedPreferences.getString("accountType", "");
        assert account != null;
        if (!account.equals("")) {
            loginAccountEdit.setText(account);
            loginPasswordEdit.setText(accountPassword);
            passwordRememberCheckbox.setChecked(true);
            assert accountType != null;
            if (accountType.equals("patient")) {
                choosePatient.setChecked(true);
            } else {
                chooseDoctor.setChecked(true);
            }
        }
    }
}