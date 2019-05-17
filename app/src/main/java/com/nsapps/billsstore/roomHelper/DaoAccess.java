package com.nsapps.billsstore.roomHelper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.*;
import com.nsapps.billsstore.beans.DocumentModel;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void insertDocument(DocumentModel document);

    @Insert
    void insertMultipleDocument(List<DocumentModel> documentModelList);

    @Delete
    void deleteDocument(DocumentModel document);

    @Update
    void updateDocument(DocumentModel document);

    @Query("SELECT * FROM doc_table ORDER BY createDate DESC")
    LiveData<List<DocumentModel>> getAllDocuments();

    @Query("SELECT * FROM doc_table WHERE documentName LIKE '%' || :searchString || '%'")
    LiveData<List<DocumentModel>> getAllDocuments(String searchString);

}
