package com.nsapps.billsstore.viewmodels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.nsapps.billsstore.beans.DocumentModel;
import com.nsapps.billsstore.repositories.DocumentRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private LiveData<List<DocumentModel>> mDocumentList;
    private DocumentRepository documentRepository;
    private Context mContext;


    public void init(Context mContext) {

        this.mContext = mContext;

        if (mDocumentList != null) return;

        documentRepository = new DocumentRepository(mContext);
        mDocumentList = documentRepository.getDocumentList();


    }


    public LiveData<List<DocumentModel>> getSearchedDocuments(String searchQuery) {

        return documentRepository.getSearchedDocumentList(searchQuery);


    }


    public LiveData<List<DocumentModel>> getDocumentsList() {
        return mDocumentList;
    }


}
