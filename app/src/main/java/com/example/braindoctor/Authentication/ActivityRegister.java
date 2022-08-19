package com.example.braindoctor.Authentication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.braindoctor.HttpUtils.HttpUtils;
import com.example.braindoctor.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityRegister extends AppCompatActivity {

    private ImageView exitRegister;
    private TextInputEditText registerAccountEdit;
    private TextInputEditText registerPasswordEdit;
    private TextInputEditText registerPasswordConfirmEdit;
    private TextInputEditText registerNameEdit;
    private RadioButton choosePatient;
    private RadioButton chooseDoctor;
    private EditText bondDoctorEdit;
    private Button entranceRegister;
    private CheckBox registerProtocolCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initActivity();
    }

    private void initActivity() {
        exitRegister = findViewById(R.id.exitRegister);
        registerAccountEdit = findViewById(R.id.registerAccountEdit);
        registerPasswordEdit = findViewById(R.id.registerPasswordEdit);
        registerPasswordConfirmEdit = findViewById(R.id.registerPasswordConfirmEdit);
        registerNameEdit = findViewById(R.id.registerNameEdit);
        choosePatient = findViewById(R.id.registerChoosePatient);
        chooseDoctor = findViewById(R.id.registerChooseDoctor);
        bondDoctorEdit = findViewById(R.id.bondDoctorEdit);
        entranceRegister = findViewById(R.id.entranceRegister);
        registerProtocolCheckbox = findViewById(R.id.registerProtocolCheckbox);

        exitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        entranceRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = Objects.requireNonNull(registerAccountEdit.getText()).toString();
                final String password = Objects.requireNonNull(registerPasswordEdit.getText()).toString();
                final String passwordConfirm = Objects.requireNonNull(registerPasswordConfirmEdit.getText()).toString();
                final String name = Objects.requireNonNull(registerNameEdit.getText()).toString();
                final String accountType = choosePatient.isChecked() ? "patient" : (chooseDoctor.isChecked() ? "doctor" : "noType");
                if (account.length() == 0) {
                    Toast.makeText(ActivityRegister.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }else if (account.length() != 11) {
                    Toast.makeText(ActivityRegister.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }else if (password.length() == 0) {
                    Toast.makeText(ActivityRegister.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(passwordConfirm)) {
                    Toast.makeText(ActivityRegister.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                }else if (name.length() == 0) {
                    Toast.makeText(ActivityRegister.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                }else if (accountType.equals("noType")) {
                    Toast.makeText(ActivityRegister.this, "请选择账号类型", Toast.LENGTH_SHORT).show();
                }else {
                    //注册接口调用
                    if (accountType.equals("doctor")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpUtils.Register(account, password, accountType, name, "", new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ActivityRegister.this, "请求超时,请稍后重试...", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ActivityRegister.this, msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            if (status) {
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }).start();
                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String doctorName = bondDoctorEdit.getText().toString();
                                if (doctorName.equals("")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ActivityRegister.this, "请输入主治医师姓名", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    HttpUtils.Register(account, password, accountType, name, doctorName, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.d("Connection", "Fail...");
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
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(ActivityRegister.this, msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                if (status) {
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                            }
                        }).start();
                    }
                }
            }
        });

        choosePatient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bondDoctorEdit.setHint(R.string.bondHint);
                    bondDoctorEdit.setEnabled(true);
                }else {
                    bondDoctorEdit.setHint("");
                    bondDoctorEdit.setEnabled(false);
                }
            }
        });

        registerProtocolCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entranceRegister.setEnabled(true);
                }else {
                    entranceRegister.setEnabled(false);
                }
            }
        });
    }
}