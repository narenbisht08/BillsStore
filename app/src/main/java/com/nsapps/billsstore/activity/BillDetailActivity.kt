package com.nsapps.billsstore.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.nsapps.billsstore.R
import com.nsapps.billsstore.beans.DocumentModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_bill_detail.*
import android.content.Intent.EXTRA_STREAM
import android.os.Environment.getExternalStorageDirectory
import android.content.Intent.ACTION_SEND
import java.io.File


class BillDetailActivity : AppCompatActivity() {

    var document: DocumentModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawableResource(R.color.colorPrimary)


        setContentView(R.layout.activity_bill_detail)

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        if (intent != null && intent.hasExtra("doc")) {
             document = intent.getSerializableExtra("doc") as DocumentModel
            tv_desc.text = document!!.documentDetail
            tv_title.text = document!!.documentName
            tv_desc.setMovementMethod(ScrollingMovementMethod())

            try {
                val uri = Uri.parse(document!!.documentImage)
                Picasso.with(this).load(uri).into(iv_doc_image)
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        var share = Intent(ACTION_SEND)
        share.setType("image/*")
        val uri = Uri.parse(document!!.documentImage)
        share.putExtra(Intent.EXTRA_STREAM, uri)




        iv_share.setOnClickListener(View.OnClickListener {


            startActivity(Intent.createChooser(share, "" + document!!.documentName))

        })


    }
}
