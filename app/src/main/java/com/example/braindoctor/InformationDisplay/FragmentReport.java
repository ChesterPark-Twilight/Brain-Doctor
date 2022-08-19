package com.example.braindoctor.InformationDisplay;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.braindoctor.FIleUtils.FileUtils;
import com.example.braindoctor.HttpUtils.HttpUtils;
import com.example.braindoctor.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

public class FragmentReport extends Fragment {

    private FloatingActionButton downloadReport;
    private ImageView reportImage;

    private String uid;

    private String lastStop;

    private FragmentReportViewModel mViewModel;

    public static FragmentReport newInstance() {
        return new FragmentReport();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        assert bundle != null;
        uid = bundle.getString("patientUid");
        lastStop = bundle.getString("lastStop");
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FragmentReportViewModel.class);
        // TODO: Use the ViewModel

        downloadReport = Objects.requireNonNull(getActivity()).findViewById(R.id.downloadReport);
        reportImage = getActivity().findViewById(R.id.reportImage);
        downloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Objects.requireNonNull(getContext()).getFilesDir().getAbsolutePath() + "/" + uid + "Report.jpg");
                Log.d("Test", file.getAbsolutePath());
                if (!file.exists()) {
                    HttpUtils.DownloadReport(uid, getContext());
                    Log.d("Test", "下载");
                }
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                reportImage.setImageBitmap(bitmap);
            }
        });
    }

}