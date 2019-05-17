package com.nsapps.billsstore.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.nsapps.billsstore.R
import com.nsapps.billsstore.activity.BillDetailActivity
import com.nsapps.billsstore.beans.DocumentModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import android.support.v4.*
import androidx.core.content.ContextCompat.startActivity
import androidx.core.util.Pair
import androidx.core.view.ViewCompat.getTransitionName
import java.util.*


class DocumentListAdapter(
    var mContext: Context, var mDocumentList: List<DocumentModel>
) :
    RecyclerView.Adapter<DocumentListAdapter.DocumentListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentListViewHolder {
        return DocumentListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_document_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {

        Log.e("BillsStore", "List Size " + mDocumentList.size)

        return mDocumentList.size
    }

    override fun onBindViewHolder(holder: DocumentListViewHolder, position: Int) {
        holder.bind(mDocumentList.get(position))
    }

    inner class DocumentListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvTitle: TextView
        var ivDocImage: ImageView
        var tvDesc: TextView
        var tvCreatedDate: TextView

        init {
            tvTitle = view.findViewById(R.id.tv_title)
            tvDesc = view.findViewById(R.id.tv_desc)
            tvCreatedDate = view.findViewById(R.id.tv_created_date)
            ivDocImage = view.findViewById(R.id.iv_doc_image)

            itemView.setOnClickListener(View.OnClickListener {


                var pair1 = Pair.create(tvTitle as View, "doc_title")
                var pair2 = Pair.create(tvDesc as View, "doc_desc")
                var pair3 = Pair.create(ivDocImage as View, "doc_image")


                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, pair1, pair2, pair3)
                //  startActivity(intent, options.toBundle())

                var i = Intent(mContext, BillDetailActivity::class.java)
                i.putExtra("doc", mDocumentList.get(position))
                mContext.startActivity(i, options.toBundle())


            })


        }

        fun bind(documentModel: DocumentModel) {
            tvTitle.text = documentModel.documentName
            tvDesc.text = documentModel.documentDetail

            if (!documentModel.createDate.equals("")) {

                /*var calendar= Calendar.getInstance()
                calendar.time= Date(documentModel.createDate.toLong())
                */
                var sdf = SimpleDateFormat("MMMM dd")



                tvCreatedDate.text = "" + sdf.format(Date(documentModel.createDate.toLong()))
            }



            try {
                val uri = Uri.parse(documentModel.documentImage)
                Picasso.with(mContext).load(uri).into(ivDocImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }
}