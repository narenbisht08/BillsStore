package com.nsapps.billsstore.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nsapps.billsstore.beans.DocumentModel
import com.nsapps.billsstore.repositories.DocumentRepository

class AddDocumentViewModel: ViewModel()
{
    var saveDocResult:MutableLiveData<String>?=null
    var mContext: Context?=null

    fun AddDocumentIntoDB(mContext: Context,documentModel: DocumentModel) : MutableLiveData<String>
    {
        this.mContext=mContext
        if(saveDocResult==null)
            saveDocResult= MutableLiveData()

        var documentRepository=DocumentRepository(mContext)
        documentRepository.insertDocument(documentModel)
        saveDocResult!!.value="success"






        return saveDocResult!!

    }





}