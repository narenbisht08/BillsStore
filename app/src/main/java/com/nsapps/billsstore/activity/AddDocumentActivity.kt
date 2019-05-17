package com.nsapps.billsstore.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.common.base.CharMatcher
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.nsapps.billsstore.R
import com.nsapps.billsstore.adapters.HintAdpater
import com.nsapps.billsstore.beans.DocumentModel
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import kotlinx.android.synthetic.main.activity_add_document.*
import java.lang.Exception
import com.nsapps.billsstore.helper.*;
import com.nsapps.billsstore.viewmodels.AddDocumentViewModel
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class AddDocumentActivity : AppCompatActivity(), View.OnClickListener, HintAdpater.OnHintClick {
    override fun onClick(position: Int) {
        if (hintResult!!.size > 0) {
            et_title.setText(et_title.text.toString() + " " + hintResult!!.get(position))
            hintResult!!.removeAt(position)
            hintAdapter!!.notifyItemRemoved(position)
        }
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.iv_close -> finish()
            R.id.tv_camera -> {
                var intent = Intent(this@AddDocumentActivity, ScanActivity::class.java)
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)
                startActivityForResult(intent, GET_IMAGE)
            }
            R.id.tv_gallery -> {
                var intent = Intent(this@AddDocumentActivity, ScanActivity::class.java)
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA)
                startActivityForResult(intent, GET_IMAGE)

            }
            R.id.fab_save -> {

                if (checkValidations())

                    addDocumentModel!!.AddDocumentIntoDB(
                        this,
                        DocumentModel(
                            et_title.text.toString(),
                            Date().time.toString(),
                            et_desc.text.toString(),
                            uri.toString()
                        )
                    ).observe(this, Observer {

                        Toast.makeText(applicationContext, "Document Saved", Toast.LENGTH_SHORT).show()
                        Handler().postDelayed(Runnable {
                            finish()
                        }, 800)

                    })

            }


        }

    }

    fun checkValidations(): Boolean {
        if (bitmap == null) {
            Utils.showSnackbar(cl_main, "Please pick your bill")
            return false
        } else if (et_title.text.toString().equals("")) {
            Utils.showSnackbar(cl_main, "Please enter title")
            return false
        } else if (et_desc.text.toString().equals("")) {
            Utils.showSnackbar(cl_main, "Please enter description of bill")
            return false
        }
        return true
    }

    private val GET_IMAGE = 1001
    private var addDocumentModel: AddDocumentViewModel? = null
    var bitmap: Bitmap? = null
    var uri: Uri? = null
    var hintResult: ArrayList<String>? = null
    var hintAdapter: HintAdpater? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_document)

        if (!Utils.isDebugBuild()) {
            cl_image_picker.visibility = View.VISIBLE
            cl_document.visibility = View.GONE
        }

        addDocumentModel = ViewModelProviders.of(this).get(AddDocumentViewModel::class.java)


        var flayoutManager = FlexboxLayoutManager(this)
        flayoutManager.setFlexDirection(FlexDirection.ROW)
        flayoutManager.setJustifyContent(JustifyContent.FLEX_START)
        cg_hint.setLayoutManager(flayoutManager);

        iv_hint.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                if (cg_hint.visibility == View.GONE)
                    cg_hint.visibility = View.VISIBLE
                else
                    cg_hint.visibility = View.GONE
            }

        })

        et_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(chars: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (cg_hint.visibility == View.GONE) {
                    Handler().postDelayed(Runnable { sv_doc.fullScroll(ScrollView.FOCUS_DOWN) }, 200)
                }
            }

        })

        iv_close.setOnClickListener(this)
        tv_camera.setOnClickListener(this)
        tv_gallery.setOnClickListener(this)
        fab_save.setOnClickListener(this)
        et_desc.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {

                if (view!!.id == R.id.et_desc) {
                    view.parent.requestDisallowInterceptTouchEvent(true)

                    when (motionEvent!!.action) {
                        MotionEvent.ACTION_UP -> {
                            view.parent.requestDisallowInterceptTouchEvent(true)

                        }
                    }

                }




                return false

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {

            if (requestCode == GET_IMAGE) {

                uri = data.extras.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                    //contentResolver.delete(uri, null, null)
                    iv_cropped_image.setImageBitmap(bitmap)

                    val options = FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build()

                    val image = FirebaseVisionImage.fromBitmap(bitmap!!)
                    val detector = FirebaseVision.getInstance()
                        //.getVisionCloudDocumentTextDetector(options)
                        .onDeviceTextRecognizer


                    val result = detector.processImage(image)
                        .addOnSuccessListener { firebaseVisionText ->
                            // Task completed successfully
                            // ...
                            processTextRecognitionResult(firebaseVisionText)
                            if (firebaseVisionText.text.length > 0) {
                                et_desc.setText(firebaseVisionText.text)


                                var prevText = firebaseVisionText.text
                                if (prevText.contains("\n"))
                                    prevText = prevText.replace("\n", " ")

                                if (prevText.contains("\t"))
                                    prevText = prevText.replace("\t", " ")


                                val pattern = Pattern.compile("[^a-z A-Z]")
                                val matcher = pattern.matcher(prevText) as Matcher


                                prevText = matcher.replaceAll("")


                                var textArr = prevText.split(" ")


                                FindHintTask(textArr).execute()


                            }

                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            // ...
                        }


                    cl_image_picker.visibility = View.GONE
                    cl_document.visibility = View.VISIBLE

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

        }

    }


    private fun processTextRecognitionResult(texts: FirebaseVisionText) {


        val resultText = texts.text

        for (block in texts.textBlocks) {

            val blockText = block.text
            // Log.e("BillStore", "Block text : " + blockText)
            val blockConfidence = block.confidence

            val blockCornerPoints = block.cornerPoints

            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                Log.e("BillStore", "Line text : " + lineText)
                val lineConfidence = line.confidence
                Log.e("BillStore", "Line Confidence : " + lineConfidence)
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementConfidence = element.confidence
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }

        }


    }


    @SuppressLint("ResourceAsColor")
    fun getChipItem(text: String) {

        var chip = Chip(this)
        chip.text = text


        var params = ChipGroup.LayoutParams(
            ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT
            )
        )
        params.setMargins(0, 15, 10, 0) //substitute parameters for left, top, right, bottom
        chip.setLayoutParams(params)
        chip.setTextAppearanceResource(R.style.ChipTextStyle)
        chip.elevation = 15F

        chip.chipStrokeColor = ColorStateList.valueOf(R.color.colorPrimaryDark)
        chip.chipBackgroundColor = ColorStateList.valueOf(R.color.colorPrimary)

        chip.isClickable = true
        chip.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Utils.showSnackbar(cl_main, chip.text.toString())
            }
        })
        cg_hint.addView(chip)


    }


    inner class FindHintTask(var wordsList: List<String>) : AsyncTask<Void, Void, ArrayList<String>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pb_hint.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<String>? {
            var finalArr = ArrayList<String>()

            for (i in 0 until wordsList.size - 1) {
                var word = wordsList.get(i)
                Log.e("BillStore", " word : " + word.toString())
                if (word.length > 2 && WordChecker.checkWord(this@AddDocumentActivity, word))
                    finalArr.add(word)

            }




            return finalArr.distinct() as ArrayList<String>
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            super.onPostExecute(result)
            pb_hint.visibility = View.GONE
            hintResult = result
            if (hintResult != null && hintResult!!.size > 0) {

                hintAdapter = HintAdpater(this@AddDocumentActivity, hintResult!!, this@AddDocumentActivity)
                cg_hint.adapter = hintAdapter
                iv_hint.visibility = View.VISIBLE
            }
        }


    }


}
