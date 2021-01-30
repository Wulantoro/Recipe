package com.example.latihanmvvm.ViewModel

import androidx.lifecycle.ViewModel
import com.example.latihanmvvm.Model.User
import com.example.latihanmvvm.Utils.Constant
import com.example.latihanmvvm.Utils.SingleLiveEvent
import com.example.latihanmvvm.Utils.WrappedResponse
import com.example.latihanmvvm.WebService.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserViewModel : ViewModel() {

    private var state : SingleLiveEvent<UserState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun login(email: String, password: String ) {
        state.value = UserState.isLoading(true)
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<User>
                    if (body.status.equals("1")) {
                        state.value = UserState.Success("Bearer ${body.data!!.api_token}")
                    } else {
                        state.value = UserState.Error("Login Gagal")
                    }
                } else {
                    state.value = UserState.Error("Kesalahan terjadi saat Login")
                }
                state.value = UserState.isLoading(false)
            }
        })
    }

    fun regis(name: String, email: String, password: String ) {
        state.value = UserState.isLoading(true)
        api.register(name, email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                println(t.message)
                state.value = UserState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<User>>, response: Response<WrappedResponse<User>>) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<User>
                    if (body.status.equals("1")) {
                        state.value = UserState.Success("Bearer ${body.data!!.api_token}")
                    } else {
                        state.value = UserState.Error("Register Gagal")
                    }
                } else {
                    state.value = UserState.Error("Kesalahan terjadi saat Registrasi")
                }
                state.value = UserState.isLoading(false)
            }
        })
    }

    fun validate(name: String, email: String, password: String) : Boolean {
        state.value = UserState.Reset
        if (name != null) {
            if (name.isEmpty()) {
                state.value = UserState.ShowToast("Nama tidak bileh kosong")
                return false
            }
            if (name.length < 5) {
                state.value = UserState.Validate(name = "Nama harusn berisi 5 karakter atau lebih")
                return false
            }
        }

        if (email.isEmpty() || password.isEmpty()) {
            state.value = UserState.ShowToast("Email dan password tidak boleh kosong")
            return false
        }
        if (!Constant.isValidEmail(email)) {
            state.value = UserState.Validate(email = "Email tidak valid")
            return false
        }
        if (!Constant.isValidPass(password)) {
            state.value = UserState.Validate(password = "Password tidak valid")
            return false
        }
        return true
    }
}

sealed class UserState() {
    data class Error(var err : String?) : UserState()
    data class ShowToast(var message : String?) : UserState()
    data class Validate(var name : String? = null, var email : String? = null, var password : String? = null) : UserState()
    data class isLoading(var state : Boolean = false) : UserState()
    data class Success(var token : String) : UserState()
    data class isFailed(var message: String?) : UserState()
    object Reset : UserState()


}