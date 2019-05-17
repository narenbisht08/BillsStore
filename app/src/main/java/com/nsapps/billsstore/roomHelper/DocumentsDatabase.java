package com.nsapps.billsstore.roomHelper;

import android.content.Context;


import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.nsapps.billsstore.beans.DocumentModel;

import java.util.Date;


@Database(entities = {DocumentModel.class}, version = 1)
public abstract class DocumentsDatabase extends RoomDatabase {


    public abstract DaoAccess daoAccess();

    private static DocumentsDatabase instance;

    public static synchronized DocumentsDatabase getDocumentDatabaseInstance(Context mContext) {
        if (instance == null) {

            instance = Room.databaseBuilder(mContext.getApplicationContext(), DocumentsDatabase.class, "documentsDatabase")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;

    }

    public static void destroyInstance() {
        instance = null;
    }


    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
          //  new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private DaoAccess daoAccess;

        private PopulateDBAsyncTask(DocumentsDatabase db) {
            daoAccess = db.daoAccess();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            daoAccess.insertDocument(new DocumentModel("doc_" + new Date().getTime(), "test", "test", ""));
            daoAccess.insertDocument(new DocumentModel("doc_" + new Date().getTime(), "test", "test", ""));
            daoAccess.insertDocument(new DocumentModel("doc_" + new Date().getTime(), "test", "test", ""));

            return null;
        }
    }
}
