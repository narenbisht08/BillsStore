package com.nsapps.billsstore

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nsapps.billsstore.activity.AddDocumentActivity
import com.nsapps.billsstore.activity.ProfileActivity
import com.nsapps.billsstore.adapters.DocumentListAdapter
import com.nsapps.billsstore.beans.DocumentModel
import com.nsapps.billsstore.helper.StickHeaderItemDecoration
import com.nsapps.billsstore.roomHelper.DaoAccess
import com.nsapps.billsstore.roomHelper.DocumentsDatabase
import com.nsapps.billsstore.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), StickHeaderItemDecoration.SectionCallback {
    override fun isSection(position: Int): Boolean {
        if (position == 0 || !convertIntoMonth(documentsList!!.get(position).createDate).equals(
                convertIntoMonth(
                    documentsList!!.get(position - 1).createDate
                )
            )
        ) {
            return true
        }
        return false
    }

    override fun getSectionHeader(position: Int): CharSequence {

        return convertIntoMonth(documentsList!!.get(position).createDate)
    }


    fun convertIntoDate(time: String): String {

        val sdf = SimpleDateFormat("dd MMM yyyy")
        return sdf.format(Date(time.toLong()))


    }
    fun convertIntoMonth(time: String): String {

        val sdf = SimpleDateFormat("MMM yyyy")
        return sdf.format(Date(time.toLong()))


    }

    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    private val ALL_PERMISSIONS_RESULT = 1011
    private lateinit var mMainActivityViewModel: MainActivityViewModel
    var documentListAdapter: DocumentListAdapter? = null
    private lateinit var documentsDatabase: DocumentsDatabase
    var document: DocumentModel? = null
    var documentsList: List<DocumentModel>? = null
    var isGrid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        documentsDatabase = DocumentsDatabase.getDocumentDatabaseInstance(this)




        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        mMainActivityViewModel.init(this)
        mMainActivityViewModel.documentsList.observe(this, object : Observer<List<DocumentModel>> {
            override fun onChanged(t: List<DocumentModel>?) {
                //     documentListAdapter.notifyDataSetChanged()
                documentsList = t
                if (documentListAdapter == null) {
                    documentListAdapter = DocumentListAdapter(this@MainActivity, documentsList!!)
                    rv_document_list.adapter = documentListAdapter
                } else {
                    documentListAdapter!!.notifyDataSetChanged()
                }

            }
        })

        fab_upload.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (checkAllPermissionsAreGiven()) {
                    startActivity(Intent(this@MainActivity, AddDocumentActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Please allow necessary permissions", Toast.LENGTH_SHORT).show()
                    checkPermissions()
                }

            }
        })
        initViews()
    }


    fun showSearchResultList() {
        rv_searched_document_list.visibility = View.VISIBLE
        rv_document_list.visibility = View.GONE

    }

    fun hideSearchResultList() {
        rv_searched_document_list.visibility = View.GONE
        rv_document_list.visibility = View.VISIBLE

    }


    fun initViews() {
        rv_document_list.setHasFixedSize(true)
        rv_document_list.layoutManager = LinearLayoutManager(this)


        var sectionItemDecoration = StickHeaderItemDecoration(
            50,
            true,
            this
        );


        rv_document_list.addItemDecoration(sectionItemDecoration)




        rv_searched_document_list.setHasFixedSize(true)
        rv_searched_document_list.layoutManager = LinearLayoutManager(this)






        et_search_bills.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (charSequence.isNullOrBlank()) {
                    hideSearchResultList()
                } else {

                    showSearchResultList()
                    mMainActivityViewModel.getSearchedDocuments(charSequence.toString())
                        .observe(this@MainActivity, Observer {

                            if (it != null) {
                                Log.e("BillStore", "Search list : " + it.size);
                                rv_searched_document_list.adapter = DocumentListAdapter(this@MainActivity, it)
                            }
                        })


                }

            }
        })


        iv_drawer.setOnClickListener(View.OnClickListener {


            if (isGrid) {

                iv_drawer.setImageResource(R.drawable.ic_grid)
                rv_document_list.layoutManager = LinearLayoutManager(this)
            } else {
                iv_drawer.setImageResource(R.drawable.ic_hamburger)
                rv_document_list.layoutManager = GridLayoutManager(this, 2)
            }
            isGrid = !isGrid


        })
        iv_profile.setOnClickListener(View.OnClickListener {

            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        })

    }


    fun checkPermissions() {
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)


        permissionsToRequest = permissionsToRequest(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!!.size > 0) {
                requestPermissions(
                    permissionsToRequest!!.toArray(
                        arrayOfNulls(permissionsToRequest!!.size)
                    ), ALL_PERMISSIONS_RESULT
                )
            }
        }

    }

    private fun permissionsToRequest(wantedPermissions: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        for (perm in wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }


    private fun checkAllPermissionsAreGiven(): Boolean {
        var counter = 0

        for (perm in permissions) {
            if (hasPermission(perm))
                counter++
        }
        if (counter < 3) return false
        else return true

    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else true

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        when (requestCode) {
            ALL_PERMISSIONS_RESULT -> {

                for (perm in permissionsToRequest.orEmpty()) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm)
                    }
                }

                if (permissionsRejected.size > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            AlertDialog.Builder(this@MainActivity)
                                .setMessage("These permissions are mandatory to get your documents. You need to allow them.")
                                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            permissionsRejected.toArray(arrayOfNulls(permissionsRejected.size)),
                                            ALL_PERMISSIONS_RESULT
                                        )
                                    }
                                }).setNegativeButton("Cancel", null).create().show()

                            return
                        }
                    }
                }


            }
        }


    }
}
