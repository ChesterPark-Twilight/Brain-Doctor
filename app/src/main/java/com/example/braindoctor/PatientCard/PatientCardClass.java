package com.example.braindoctor.PatientCard;

public class PatientCardClass {
    private int headSculpture;
    private String name;
    private String detail;
    private String status;
    private int statusImage;

    private String uid;

    public PatientCardClass(int headSculpture, String name, String detail, String status, int statusImage) {
        this.headSculpture = headSculpture;
        this.name = name;
        this.detail = detail;
        this.status = status;
        this.statusImage = statusImage;
    }

    public PatientCardClass(int headSculpture, String name, String detail, String status, int statusImage, String uid) {
        this.headSculpture = headSculpture;
        this.name = name;
        this.detail = detail;
        this.status = status;
        this.statusImage = statusImage;
        this.uid = uid;
    }

    public int getHeadSculpture() {
        return headSculpture;
    }

    public void setHeadSculpture(int headSculpture) {
        this.headSculpture = headSculpture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(int statusImage) {
        this.statusImage = statusImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
