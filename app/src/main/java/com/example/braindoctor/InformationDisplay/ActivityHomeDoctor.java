package com.example.braindoctor.InformationDisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.braindoctor.HttpUtils.HttpUtils;
import com.example.braindoctor.PatientCard.PatientCardAdapter;
import com.example.braindoctor.PatientCard.PatientCardClass;
import com.example.braindoctor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityHomeDoctor extends AppCompatActivity {

    private String uid;

    private ListView listView;
    private List<PatientCardClass> patientCardClassList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doctor);

        initActivity();
    }

    private void initActivity() {

        CircleImageView circleImageView = findViewById(R.id.headSculpture);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHomeDoctor.this, ActivityAccount.class);
                intent.putExtra("accountUid", uid);
                intent.putExtra("accountType", "doctor");
                startActivity(intent);
            }
        });

        TextView textView = findViewById(R.id.reloadList);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatientCardAdapter patientCardAdapter = new PatientCardAdapter(patientCardClassList);
                listView.setAdapter(patientCardAdapter);
            }
        });

        listView = findViewById(R.id.patientList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityHomeDoctor.this, ActivityHomePatient.class);
                intent.putExtra("lastStop", "activityHomeDoctor");
                intent.putExtra("patientName", patientCardClassList.get(position).getName());
                intent.putExtra("patientDetail", patientCardClassList.get(position).getDetail());
                intent.putExtra("patientUid", patientCardClassList.get(position).getUid());
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
        uid = sharedPreferences.getString("accountUid", "");

        //信息初始化
        HttpUtils.Query(uid, "doctor", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Connection", "Fail...");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(responseData);
                    boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");
                    if (status) {
                        JSONObject userInfo = jsonObject.getJSONObject("user_info");
                        String account = userInfo.getString("account");
                        String name = userInfo.getString("name");
                        String age = userInfo.getString("age");
                        JSONArray patientArray = userInfo.getJSONArray("patients");
                        for (int i=0; i<patientArray.length(); i++) {
                            JSONObject patientObject = patientArray.getJSONObject(i);
                            String patientName = patientObject.getString("name");
                            String patientAge = patientObject.getString("age");
                            String patientUid = patientObject.getString("uid");
                            boolean segmentTag = patientObject.getBoolean("segemented");
                            String patientStatus =  segmentTag ? getString(R.string.dataStatusSuccess) : getString(R.string.dataStatusFail);
                            int patientStatusImage = segmentTag ? R.drawable.state_success : R.drawable.state_unknown;
                            patientCardClassList.add(new PatientCardClass(R.drawable.picture_male, patientName, patientAge, patientStatus, patientStatusImage, patientUid));
                        }
                    }
                    Log.d("Message", msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Data", responseData);
            }
        });

        patientCardClassList.add(new PatientCardClass(R.drawable.picture_male, "Park", "男 21岁", getString(R.string.dataStatusFail), R.drawable.state_unknown, "p0"));
        patientCardClassList.add(new PatientCardClass(R.drawable.picture_female, "Kate", "女 21岁", getString(R.string.dataStatusSuccess), R.drawable.state_success, "p0"));
        patientCardClassList.add(new PatientCardClass(R.drawable.picture_male, "Peter", "男 21岁", getString(R.string.dataStatusSuccess), R.drawable.state_success, "p0"));

        registerReceiver();
    }

    //销毁Activity广播监听
    BroadcastReceiver destroyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void registerReceiver() {
        registerReceiver(destroyReceiver, new IntentFilter("com.example.braindoctor.destroy"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(destroyReceiver);
    }
}