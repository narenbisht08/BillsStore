package com.nsapps.billsstore.helper

import android.content.Context
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader


class WordChecker {

    companion object {

        var bufferedReader: BufferedReader? = null

        fun checkWord(mContext: Context, word: String): Boolean {
            try {
                //  if (bufferedReader == null)
                bufferedReader = BufferedReader(InputStreamReader(mContext.getAssets().open("words.txt")))
                do {

                    var str = bufferedReader!!.readLine()

                    if (str == null)
                        break

                    if (str.equals(word, true)) {
                        return true
                    }
                } while (true)

                bufferedReader!!.close()
            } catch (e: IOException) {
            }
            return false

        }

    }

}