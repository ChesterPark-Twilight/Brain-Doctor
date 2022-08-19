package com.example.braindoctor.InformationDisplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.braindoctor.HttpUtils.HttpUtils;
import com.example.braindoctor.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityHomePatient extends AppCompatActivity {
    private static final int ACQUIRE_PERMISSION = 1;
    private static final int PICK_FILE = 2;

    private ImageView quitHomePatient;
    private TabLayout pageSwitcher;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    private String patientUid;

    private String lastStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_patient);
        quitHomePatient = findViewById(R.id.quitHomePatient);
        Intent intent = getIntent();
        lastStop = intent.getStringExtra("lastStop");
        assert lastStop != null;
        Log.d("Communication", lastStop);
        if (lastStop.equals("activityHomeDoctor")) {
            quitHomePatient.setVisibility(View.VISIBLE);
            patientUid = intent.getStringExtra("patientUid");
        }else if (lastStop.equals("activityLogin")) {
            quitHomePatient.setVisibility(View.INVISIBLE);
            SharedPreferences sharedPreferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
            patientUid = sharedPreferences.getString("accountUid", "");
        }
        initActivity();
        acquirePermission();
    }

    private void initActivity() {
        pageSwitcher = findViewById(R.id.pageSwitcher);
        viewPager = findViewById(R.id.viewPager);

        //页面切换设置
        Bundle bundle = new Bundle();
        bundle.putString("patientUid", patientUid);
        bundle.putString("lastStop", lastStop);
        FragmentModel fragmentModel = new FragmentModel();
        fragmentModel.setArguments(bundle);
        FragmentReport fragmentReport = new FragmentReport();
        fragmentReport.setArguments(bundle);
        fragmentList.add(fragmentModel);
        fragmentList.add(fragmentReport);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(pageSwitcher));
        pageSwitcher.addTab(pageSwitcher.newTab().setText(R.string.titleModel));
        pageSwitcher.addTab(pageSwitcher.newTab().setText(R.string.titleReport));
        Objects.requireNonNull(pageSwitcher.getTabAt(0)).select();
        pageSwitcher.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        quitHomePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerReceiver();
    }

    //获取权限(存储权限)
    private void acquirePermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissionArray = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissionArray, ACQUIRE_PERMISSION);
        }
    }

    //权限申请结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACQUIRE_PERMISSION) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        //获取失败后执行操作
                        Toast.makeText(this, "权限获取失败...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //获取成功后执行操作
            }
        }
    }

    //文件选择
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE);
    }

    //文件选择结果回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        assert data != null;
//        Uri uri = data.getData();  // 获取文件Uri
//        ContentResolver contentResolver = getContentResolver();
//        assert uri != null;
//        Cursor cursor = contentResolver.query(uri, null, null, null, null);
//        // 普通文件地址
//        if (cursor == null) {
//            String path = uri.getPath();
//            assert path != null;
//            Log.d("File", path);
//            return;
//        }
//        // 多媒体文件地址
//        if (cursor.moveToFirst()) {
//            String filePath = cursor.getString(cursor.getColumnIndex("_data"));
//            Log.d("File", filePath);
//        }
//        cursor.close();
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