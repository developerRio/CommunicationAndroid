package com.originalstocks.blueserverapp.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton class for sharedPreferences
 */
class MySharedPreferences {
    //Shared Preference key
    private val KEY_PREFERENCE_NAME = "BlueDB"

    //private keyS
    private val KEY_DEFAULT: String? = null

    //Method to set boolean for (AppIntro)
    fun setBooleanKey(keyname: String?) {
        mSharedPreference.edit().putBoolean(keyname, true).apply()
    }

    fun setBooleanKey(keyname: String?, state: Boolean) {
        mSharedPreference.edit().putBoolean(keyname, state).apply()
    }

    /*
     * Method to get boolean key
     * true = means set
     * false = not set (show app intro)
     * */
    fun getBooleanKey(keyname: String?): Boolean {
        return mSharedPreference.getBoolean(keyname, false)
    }

    //Method to store user Mobile number
    fun setKey(key: String?, value: String?) {
        mSharedPreference.edit().putString(key, value).apply()
    }

    //Method to get User mobile number
    fun getKey(key: String?): String? {
        return mSharedPreference.getString(key, KEY_DEFAULT)
    }

    companion object {
        const val USER_KEY = "key_token"


        //instance field
        private lateinit var mSharedPreference: SharedPreferences
        private var mInstance: MySharedPreferences? = null
        private var mContext: Context? = null
        fun getInstance(context: Context?): MySharedPreferences? {
            mContext = context
            if (mInstance == null) {
                mInstance = MySharedPreferences()
            }
            return mInstance
        }

        fun removeValueFromKey(keyname: String?) {
            mSharedPreference.edit().remove(keyname).apply()
        }

        fun removeAllData() {
            mSharedPreference.edit().clear().apply()
        }
    }

    init {
        mSharedPreference =
            mContext!!.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}