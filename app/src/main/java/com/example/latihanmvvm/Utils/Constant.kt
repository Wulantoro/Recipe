package com.example.latihanmvvm.Utils

import android.content.Context

class Constant{
    companion object {
        const val API_ENDPOINT = "https://resep-mau.herokuapp.com/"

        fun getToken(context : Context) : String{
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            val token = pref.getString("TOKEN", "undefine")
            return token!!
        }

        fun setToken(context: Context, token : String) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("Token", token)
                apply()
            }
        }

        fun clearToken(context: Context) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun isValidEmail(email : String) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isValidPass(pass : String) = pass.length > 8
    }

}

