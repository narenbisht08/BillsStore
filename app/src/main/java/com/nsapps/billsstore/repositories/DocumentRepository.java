package com.nsapps.billsstore.repositories;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.nsapps.billsstore.beans.DocumentModel;
import com.nsapps.billsstore.roomHelper.DaoAccess;
import com.nsapps.billsstore.roomHelper.DocumentsDatabase;

import java.util.ArrayList;
import java.util.List;

public class DocumentRepository {
    private List<DocumentModel> documentModelList = new ArrayList<>();
    private DocumentsDatabase documentsDatabase;
    private Context mContext;
    LiveData<List<DocumentModel>> data;
    DaoAccess daoAccess;

    public DocumentRepository(Context context) {
        this.mContext = context;
        documentsDatabase = DocumentsDatabase.getDocumentDatabaseInstance(mContext.getApplicationContext());
        daoAccess = documentsDatabase.daoAccess();
        data = daoAccess.getAllDocuments();

    }


    public LiveData<List<DocumentModel>> getDocumentList() {


        return data;

    }


    public LiveData<List<DocumentModel>> getSearchedDocumentList(String searchQuery) {

        LiveData<List<DocumentModel>> searchedData = daoAccess.getAllDocuments(searchQuery);

        return searchedData;

    }


    public void insertDocument(DocumentModel document) {
        new InsertRecordTask(daoAccess).execute(document);
    }


    class InsertRecordTask extends AsyncTask<DocumentModel, Void, Void> {
        DaoAccess daoAccess;

        public InsertRecordTask(DaoAccess daoAccess) {
            this.daoAccess = daoAccess;
        }

        @Override
        protected Void doInBackground(DocumentModel... documentModels) {

            daoAccess.insertDocument(documentModels[0]);
            return null;
        }
    }

}
