package com.nsapps.billsstore.helper

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.nsapps.billsstore.BuildConfig


class Utils {

    companion object {

        fun showSnackbar(view: View, msg: String) = Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()


        fun isDebugBuild(): Boolean {
            if (BuildConfig.DEBUG)
                return true
            else
                return false
        }


    }


}
