package com.nsapps.billsstore.beans;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "doc_table")
public class DocumentModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String documentName;

    private String createDate;

    private String documentDetail;

    private String documentImage;


    public DocumentModel(String documentName, String createDate, String documentDetail, String documentImage) {
        this.documentName = documentName;
        this.createDate = createDate;
        this.documentDetail = documentDetail;
        this.documentImage=documentImage;
    }


    public String getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(String documentImage) {
        this.documentImage = documentImage;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(@NonNull String documentName) {
        this.documentName = documentName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDocumentDetail() {
        return documentDetail;
    }

    public void setDocumentDetail(String documentDetail) {
        this.documentDetail = documentDetail;
    }
}
