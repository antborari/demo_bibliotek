package com.javright.bibliotek

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.util.*

const val ID_APP = "ID_APP"
const val MAP_LIST_KEYS : String = "com.javright.bibliotek.LIST_KEYS"

@HiltAndroidApp
class BaseApplication : Application() {

    private lateinit var sharedPref: SharedPreferences

    companion object {
        lateinit var instance: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        sharedPref = getSharedPreferences(MAP_LIST_KEYS, Context.MODE_PRIVATE)!!
    }

    private fun getIdApp(): String? {
        return sharedPref.getString(ID_APP, null)
    }

    fun writeAndGetIdApp() : String {
        val id: String? = getIdApp()
        var res = id
        if (id == null) {
            val key = UUID.randomUUID()
            res = key.toString()
            with(sharedPref.edit()) {
                clear()
                putString(ID_APP, key.toString())
                commit()
            }
        }
        return res!!
    }
}