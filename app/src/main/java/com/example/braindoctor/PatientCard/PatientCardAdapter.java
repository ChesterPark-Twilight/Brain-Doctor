package com.example.braindoctor.PatientCard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.braindoctor.R;

import java.util.List;

public class PatientCardAdapter extends BaseAdapter {
    private List<PatientCardClass> patientCardClassList;

    public PatientCardAdapter(List<PatientCardClass> patientCardClassList) {
        this.patientCardClassList = patientCardClassList;
    }

    @Override
    public int getCount() {
        return patientCardClassList.size();
    }

    @Override
    public Object getItem(int position) {
        return patientCardClassList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PatientCardHolder patientCardHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient, parent, false);
            patientCardHolder = new PatientCardHolder();
            patientCardHolder.headSculpture = convertView.findViewById(R.id.patientCardHeadSculpture);
            patientCardHolder.name = convertView.findViewById(R.id.patientCardName);
            patientCardHolder.detail = convertView.findViewById(R.id.patientCardDetail);
            patientCardHolder.status = convertView.findViewById(R.id.patientCardStatus);
            patientCardHolder.statusImage = convertView.findViewById(R.id.patientCardStatusImage);
            convertView.setTag(patientCardHolder);
        }else {
            patientCardHolder = (PatientCardHolder) convertView.getTag();
        }

        patientCardHolder.headSculpture.setImageResource(patientCardClassList.get(position).getHeadSculpture());
        patientCardHolder.name.setText(patientCardClassList.get(position).getName());
        patientCardHolder.detail.setText(patientCardClassList.get(position).getDetail());
        patientCardHolder.status.setText(patientCardClassList.get(position).getStatus());
        patientCardHolder.statusImage.setImageResource(patientCardClassList.get(position).getStatusImage());

        return convertView;


    }

    static class PatientCardHolder {
        ImageView headSculpture;
        TextView name;
        TextView detail;
        TextView status;
        ImageView statusImage;
    }
}
