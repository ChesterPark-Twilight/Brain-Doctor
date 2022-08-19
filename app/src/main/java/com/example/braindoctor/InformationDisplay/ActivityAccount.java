package com.example.braindoctor.InformationDisplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.braindoctor.Authentication.ActivityLogin;
import com.example.braindoctor.HttpUtils.HttpUtils;
import com.example.braindoctor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityAccount extends AppCompatActivity {
    private static final int MODIFY_NAME = 1;
    private static final int MODIFY_AGE = 2;
    private static final int MODIFY_SEX = 3;

    private ConstraintLayout modifyHeadSculpture;
    private ConstraintLayout modifyName;
    private ConstraintLayout modifyAge;
    private ConstraintLayout modifySex;
    private ConstraintLayout logout;
    private ConstraintLayout logoff;

    private TextView accountName;
    private TextView accountAge;
    private TextView accountSex;

    private String uid;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initActivity();
    }

    private void initActivity() {
        modifyHeadSculpture = findViewById(R.id.modifyHeadSculpture);
        modifyName = findViewById(R.id.modifyName);
        modifyAge = findViewById(R.id.modifyAge);
        modifySex = findViewById(R.id.modifySex);
        logout = findViewById(R.id.logout);
        logoff = findViewById(R.id.logoff);

        accountName = findViewById(R.id.accountName);
        accountAge = findViewById(R.id.accountAge);
        accountSex = findViewById(R.id.accountSex);

        modifyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyWindow(MODIFY_NAME);
            }
        });

        modifyAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyWindow(MODIFY_AGE);
            }
        });

        modifySex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyWindow(MODIFY_SEX);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除用户信息
                SharedPreferences sharedPreferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                //跳转登录页面
                Intent intent = new Intent(ActivityAccount.this, ActivityLogin.class);
                startActivity(intent);
                finish();
                //发送销毁广播
                Intent intentDestroy = new Intent().setAction("com.example.braindoctor.destroy");
                sendBroadcast(intentDestroy);
            }
        });

        logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageView imageView = findViewById(R.id.toolbarQuit);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        uid = intent.getStringExtra("accountUid");
        accountType = intent.getStringExtra("accountType");

        HttpUtils.Query(uid, accountType, new Callback() {
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
                    boolean status = jsonObject.getBoolean("status");
                    final String msg = jsonObject.getString("msg");
                    if (status) {
                        JSONObject patientInfo = jsonObject.getJSONObject("user_info");
                        final String name = patientInfo.getString("name");
                        final String age = patientInfo.getString("age");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                accountName.setText(name);
                                accountAge.setText(age);
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityAccount.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showModifyWindow(final int MODIFY_TAG) {
        View view = View.inflate(ActivityAccount.this, R.layout.account_modify_window, null);

        //定义弹出窗口，绑定视图并显示
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityAccount.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);

        //绑定控件
        final ImageButton cancel = view.findViewById(R.id.cancelModify);
        final ImageButton confirm = view.findViewById(R.id.confirmModify);
        final TextView modifyTitle = view.findViewById(R.id.modifyTitle);
        final EditText editAccountName = view.findViewById(R.id.editAccountName);
        final RadioGroup chooseAccountSex = view.findViewById(R.id.chooseAccountSex);
        final RadioButton chooseMan = view.findViewById(R.id.chooseMan);
        final RadioButton chooseWoman = view.findViewById(R.id.chooseWoman);
        final LinearLayout pickAccountAge = view.findViewById(R.id.pickAccountAge);
        final NumberPicker pickAge = view.findViewById(R.id.pickAge);

        final List<String> ageArrayList = new ArrayList<>();

        //弹窗复用
        switch (MODIFY_TAG) {
            case MODIFY_NAME:
                modifyTitle.setText(R.string.modifyName);
                editAccountName.setVisibility(View.VISIBLE);
                break;
            case MODIFY_AGE:
                modifyTitle.setText(R.string.modifyAge);
                pickAccountAge.setVisibility(View.VISIBLE);
                for (int i=1; i<99; i++) {
                    ageArrayList.add(String.valueOf(i));
                }
                String[] ageList = ageArrayList.toArray(new String[0]);
                pickAge.setDisplayedValues(ageList);
                pickAge.setMinValue(1);
                pickAge.setMaxValue(ageList.length);
                break;
            case MODIFY_SEX:
                modifyTitle.setText(R.string.modifySex);
                chooseAccountSex.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        bottomSheetDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (MODIFY_TAG) {
                    case MODIFY_NAME:
                        final String newName = editAccountName.getText().toString();
                        if (newName.isEmpty()) {
                            Toast.makeText(ActivityAccount.this, "请输入新的姓名...", Toast.LENGTH_SHORT).show();
                        }else {
                            HttpUtils.Update(uid, accountType, newName, "", "", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ActivityAccount.this, "修改失败...", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(responseData);
                                        boolean status = jsonObject.getBoolean("status");
                                        final String msg = jsonObject.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ActivityAccount.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        if (status) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    accountName.setText(newName);
                                                }
                                            });
                                            bottomSheetDialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        break;
                    case MODIFY_AGE:
                        final String newAge = ageArrayList.get(pickAge.getValue()-1);
                        HttpUtils.Update(uid, accountType, "", newAge, "", new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ActivityAccount.this, "修改失败...", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(responseData);
                                    boolean status = jsonObject.getBoolean("status");
                                    final String msg = jsonObject.getString("msg");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ActivityAccount.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    if (status) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                accountAge.setText(newAge);
                                            }
                                        });
                                        bottomSheetDialog.dismiss();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    case MODIFY_SEX:
                        final String newSex = chooseMan.isChecked() ? getString(R.string.man) : (chooseWoman.isChecked() ? getString(R.string.woman) : "noType");
                        if (newSex.equals("noType")) {
                            Toast.makeText(ActivityAccount.this, "请选择性别", Toast.LENGTH_SHORT).show();
                        }else {
                            HttpUtils.Update(uid, accountType, "", "", newSex, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ActivityAccount.this, "修改失败...", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(responseData);
                                        boolean status = jsonObject.getBoolean("status");
                                        final String msg = jsonObject.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ActivityAccount.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        if (status) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    accountSex.setText(newSex);
                                                }
                                            });
                                            bottomSheetDialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }
}