package com.example.braindoctor.InformationDisplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.braindoctor.DrawImage.DrawImage;
import com.example.braindoctor.FIleUtils.FileUtils;
import com.example.braindoctor.HttpUtils.HttpUtils;
import com.example.braindoctor.R;
import com.example.braindoctor.ReadNII.NiftiVolume;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.braindoctor.FIleUtils.FileUtils.TYPE;

public class FragmentModel extends Fragment {

    private NiftiVolume t1Matrix;
    private NiftiVolume t1CEMatrix;
    private NiftiVolume t2Matrix;
    private NiftiVolume flairMatrix;

    private NiftiVolume segMatrix;

    private NestedScrollView modelLayoutContainer;
    private LinearLayout cardAccountInfo;
    private Button uploadButton;
    private Button downloadButton;
    private TextView cardName;
    private TextView cardDetail;
    private TextView cardDoctor;

    private RadioGroup chooseModality;
    private RadioButton chooseT1;
    private RadioButton chooseT1CE;
    private RadioButton chooseT2;
    private RadioButton chooseFLARE;

    private CardView cardBrain;
    private TextView cardTagText;
    private CardView cardModelStatus;
    private TextView modelStatusText;
    private ImageView modelStatusImage;
    private ProgressBar modelStatusProgress;
    private ImageView modalityImage;
    private CheckBox tumourCheckbox;
    private CheckBox coreCheckbox;
    private CheckBox enhanceCheckbox;
    private SeekBar modalitySeekBar;

    private FragmentModelViewModel mViewModel;

    private String modelType;

    private int currentPosition;
    private boolean tumour;
    private boolean core;
    private boolean enhance;

    private String uid;

    private String lastStop;

    public static FragmentModel newInstance() {
        return new FragmentModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        assert bundle != null;
        uid = bundle.getString("patientUid");
        lastStop = bundle.getString("lastStop");
        return inflater.inflate(R.layout.fragment_model, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FragmentModelViewModel.class);
        // TODO: Use the ViewModel

        initFragment();
    }

    private void initFragment() {
        modelLayoutContainer = Objects.requireNonNull(getActivity()).findViewById(R.id.modelLayoutContainer);

        cardAccountInfo = getActivity().findViewById(R.id.cardAccountInfo);
        uploadButton = getActivity().findViewById(R.id.uploadButton);
        downloadButton = getActivity().findViewById(R.id.downloadButton);
        cardName = getActivity().findViewById(R.id.cardName);
        cardDetail = getActivity().findViewById(R.id.cardDetail);
        cardDoctor = getActivity().findViewById(R.id.cardDoctor);

        chooseModality = getActivity().findViewById(R.id.chooseModality);
        chooseT1 = getActivity().findViewById(R.id.chooseT1);
        chooseT1CE = getActivity().findViewById(R.id.chooseT1CE);
        chooseT2 = getActivity().findViewById(R.id.chooseT2);
        chooseFLARE = getActivity().findViewById(R.id.chooseFLAIR);
        chooseT1.setChecked(true);
        modelType = TYPE[0];

        cardBrain = getActivity().findViewById(R.id.cardBrain);
        cardTagText = getActivity().findViewById(R.id.cardTagText);
        cardModelStatus = getActivity().findViewById(R.id.cardModelStatus);
        modelStatusText = getActivity().findViewById(R.id.modelStatusText);
        modelStatusImage = getActivity().findViewById(R.id.modelStatusImage);
        modelStatusProgress = getActivity().findViewById(R.id.modelStatusProgress);
        modalityImage = getActivity().findViewById(R.id.modalityImage);
        tumourCheckbox = getActivity().findViewById(R.id.tumourCheckbox);
        coreCheckbox = getActivity().findViewById(R.id.coreCheckbox);
        enhanceCheckbox = getActivity().findViewById(R.id.enhanceCheckbox);
        modalitySeekBar = getActivity().findViewById(R.id.modalitySeekBar);

        if (lastStop.equals("activityHomeDoctor")) {
            cardAccountInfo.setEnabled(false);
        }

        //模态选择框监听器
        chooseModality.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (chooseT1.isChecked()) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
//                    cardBrain.startAnimation(animation);
                    cardTagText.setText(R.string.T1Tag);
                    modelType = TYPE[0];
                    if (t1Matrix != null) {
                        modelStatusText.setText(R.string.modelLoaded);
                        modelStatusImage.setImageResource(R.drawable.state_success);
                        Bitmap bitmap = DrawImage.drawBitmap(t1Matrix, currentPosition,tumour, core, enhance, segMatrix);
                        modalityImage.setImageBitmap(bitmap);
                    }else {
                        modelStatusText.setText(R.string.modelUnknown);
                        modelStatusImage.setImageResource(R.drawable.state_unknown);
                        modalityImage.setImageResource(R.drawable.picture_brain);
                    }
                }else if (chooseT1CE.isChecked()) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
//                    cardBrain.startAnimation(animation);
                    cardTagText.setText(R.string.T1CETag);
                    modelType = TYPE[1];
                    if (t1CEMatrix != null) {
                        modelStatusText.setText(R.string.modelLoaded);
                        modelStatusImage.setImageResource(R.drawable.state_success);
                        Bitmap bitmap = DrawImage.drawBitmap(t1CEMatrix, currentPosition, tumour, core, enhance, segMatrix);
                        modalityImage.setImageBitmap(bitmap);
                    }else {
                        modelStatusText.setText(R.string.modelUnknown);
                        modelStatusImage.setImageResource(R.drawable.state_unknown);
                        modalityImage.setImageResource(R.drawable.picture_brain);
                    }
                }else if (chooseT2.isChecked()) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
//                    cardBrain.startAnimation(animation);
                    cardTagText.setText(R.string.T2Tag);
                    modelType = TYPE[2];
                    if (t2Matrix != null) {
                        modelStatusText.setText(R.string.modelLoaded);
                        modelStatusImage.setImageResource(R.drawable.state_success);
                        Bitmap bitmap = DrawImage.drawBitmap(t2Matrix, currentPosition, tumour, core, enhance, segMatrix);
                        modalityImage.setImageBitmap(bitmap);
                    }else {
                        modelStatusText.setText(R.string.modelUnknown);
                        modelStatusImage.setImageResource(R.drawable.state_unknown);
                        modalityImage.setImageResource(R.drawable.picture_brain);
                    }
                }else if (chooseFLARE.isChecked()) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
//                    cardBrain.startAnimation(animation);
                    cardTagText.setText(R.string.FlairTag);
                    modelType = TYPE[3];
                    if (flairMatrix != null) {
                        modelStatusText.setText(R.string.modelLoaded);
                        modelStatusImage.setImageResource(R.drawable.state_success);
                        Bitmap bitmap = DrawImage.drawBitmap(flairMatrix, currentPosition, tumour, core, enhance, segMatrix);
                        modalityImage.setImageBitmap(bitmap);
                    }else {
                        modelStatusText.setText(R.string.modelUnknown);
                        modelStatusImage.setImageResource(R.drawable.state_unknown);
                        modalityImage.setImageResource(R.drawable.picture_brain);
                    }
                }
            }
        });

        //头像信息按钮事件
        cardAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityAccount.class);
                intent.putExtra("accountUid", uid);
                intent.putExtra("accountType", "patient");
                startActivity(intent);
            }
        });

        //上传按钮事件
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //下载按钮事件
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String s : TYPE) {
                    File file = new File(FileUtils.getNiiFilePath("p0", s, Objects.requireNonNull(getContext())));
                    Log.d("Data", file.getAbsolutePath());
                    if (file.exists()) {
                        // 已经存在
                        Toast.makeText(getContext(), s + "模型已存在，无需下载", Toast.LENGTH_SHORT).show();
                    }else {
                        HttpUtils.Download(uid, s, getContext());
                        Toast.makeText(getContext(), s + "模型下载完成！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //模型加载按钮事件
        cardModelStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String modelStatus = modelStatusText.getText().toString();
                if (modelStatus.equals(getString(R.string.modelLoaded))) {
                    Snackbar.make(modelLayoutContainer, modelType + "模型已加载...", BaseTransientBottomBar.LENGTH_SHORT).show();
                }else {
                    modelStatusText.setText(R.string.modelLoading);
                    modelStatusImage.setVisibility(View.INVISIBLE);
                    modelStatusProgress.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Load", "Load Start " + modelType);
                            if (modelType.equals(TYPE[0])) {
                                try {
                                    Log.d("Load", "Load T1");
                                    t1Matrix = NiftiVolume.read(FileUtils.getNiiFilePath("p0", TYPE[0], Objects.requireNonNull(getContext())));
                                    Log.d("Load", "Load Success");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else if (modelType.equals(TYPE[1])) {
                                try {
                                    t1CEMatrix = NiftiVolume.read(FileUtils.getNiiFilePath("p0", TYPE[1], Objects.requireNonNull(getContext())));
                                    Log.d("Load", "Load Success");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else if (modelType.equals(TYPE[2])) {
                                try {
                                    t2Matrix = NiftiVolume.read(FileUtils.getNiiFilePath("p0", TYPE[2], Objects.requireNonNull(getContext())));
                                    Log.d("Load", "Load Success");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else if (modelType.equals(TYPE[3])) {
                                try {
                                    flairMatrix = NiftiVolume.read(FileUtils.getNiiFilePath("p0", TYPE[3], Objects.requireNonNull(getContext())));
                                    Log.d("Load", "Load Success");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (segMatrix == null) {
                                try {
                                    segMatrix = NiftiVolume.read(FileUtils.getNiiFilePath("p0", TYPE[4], Objects.requireNonNull(getContext())));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Intent intent = new Intent().setAction("com.example.braindoctor.loadSuccess");
                            Objects.requireNonNull(getContext()).sendBroadcast(intent);
                        }
                    }).start();
                }
            }
        });

        tumourCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tumour = segMatrix != null;
                }else {
                    tumour = false;
                }
            }
        });

        coreCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    core = segMatrix != null;
                }else {
                    core = false;
                }
            }
        });

        enhanceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enhance = segMatrix != null;
                }else {
                    enhance = false;
                }
            }
        });

        //进度条拖动监听器
        modalitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentPosition = progress;
                Log.d("绘图", "绘图开始");
                if (modelType.equals(TYPE[0]) && t1Matrix != null) {
                    Bitmap bitmap = DrawImage.drawBitmap(t1Matrix, progress, tumour, core, enhance, segMatrix);
                    modalityImage.setImageBitmap(bitmap);
                }else if (modelType.equals(TYPE[1]) && t1CEMatrix != null ) {
                    Bitmap bitmap = DrawImage.drawBitmap(t1CEMatrix, progress, tumour, core, enhance, segMatrix);
                    modalityImage.setImageBitmap(bitmap);
                }else if (modelType.equals(TYPE[2])  && t2Matrix != null) {
                    Bitmap bitmap = DrawImage.drawBitmap(t2Matrix, progress, tumour, core, enhance, segMatrix);
                    modalityImage.setImageBitmap(bitmap);
                }else if (modelType.equals(TYPE[3])  && flairMatrix != null) {
                    Bitmap bitmap = DrawImage.drawBitmap(flairMatrix, progress, tumour, core, enhance, segMatrix);
                    modalityImage.setImageBitmap(bitmap);
                }else {

                }
                Log.d("绘图", "绘图完成");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        registerReceiver();

        //信息初始化
        HttpUtils.Query(uid, "patient", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "信息获取失败，请重试", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("Data", responseData);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(responseData);
                    boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");
                    if (status) {
                        JSONObject userInfo = jsonObject.getJSONObject("user_info");
                        final String name = userInfo.getString("name");
                        final String age = "年龄: " + userInfo.getString("age");
                        final String doctor = "主治医师: " + userInfo.getString("doctor");
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cardName.setText(name);
                                cardDetail.setText(age);
                                cardDoctor.setText(doctor);
                            }
                        });
                    }
                    Log.d("Message", msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Data", responseData);
            }
        });
    }

    BroadcastReceiver loadStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            modelStatusImage.setImageResource(R.drawable.state_success);
            modelStatusProgress.setVisibility(View.INVISIBLE);
            modelStatusImage.setVisibility(View.VISIBLE);
            modelStatusText.setText(R.string.modelLoaded);
        }
    };

    private void registerReceiver() {
        Objects.requireNonNull(getContext()).registerReceiver(loadStatusReceiver, new IntentFilter("com.example.braindoctor.loadSuccess"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Objects.requireNonNull(getContext()).unregisterReceiver(loadStatusReceiver);
    }
}