package com.example.latihanmvvm.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.latihanmvvm.Model.Recipe
import com.example.latihanmvvm.Utils.Constant
import com.example.latihanmvvm.Utils.SingleLiveEvent
import com.example.latihanmvvm.Utils.WrappedListResponse
import com.example.latihanmvvm.Utils.WrappedResponse
import com.example.latihanmvvm.WebService.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//clas view model di extends dengan ViewModel, symbol : = extends
class RecipeViewModel : ViewModel() {

    private var recipes = MutableLiveData<List<Recipe>>()  //dua data
    private var recipe = MutableLiveData<Recipe>()  // 1 data
    private var state : SingleLiveEvent<RecipeState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun fetchAllPost(token : String) {
        state.value = RecipeState.IsLoading(true)
        api.allRecipe(token).enqueue(object : Callback<WrappedListResponse<Recipe>>{
            override fun onFailure(call: Call<WrappedListResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedListResponse<Recipe>>, response: Response<WrappedListResponse<Recipe>>) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedListResponse<Recipe>
                    if (body.status.equals(1)) {
                        val  r = body.data
                        recipes.postValue(r)
                    }
                } else {
                    state.value = RecipeState.Error("Terjadi kesalahan, gagal mendapat kan respone")
                }
                state.value = RecipeState.IsLoading(false)
            }

        })
    }

    fun fetchOnePost(token: String, id: String) {
        state.value = RecipeState.IsLoading(true);
        api.getOneRecipe(token, id).enqueue(object : Callback<WrappedResponse<Recipe>> {
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                print(t.message)
                state.value = RecipeState.Error(t.message)

            }

            override fun onResponse(call: Call<WrappedResponse<Recipe>>, response: Response<WrappedResponse<Recipe>>) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        val r = body.data
                        recipe.postValue(r)
                    } else {
                        state.value = RecipeState.Error("Gagal mendapatkan data dari SErver")
                    }
                    state.value = RecipeState.IsLoading(false)
                }
            }
        })
    }

    fun create(token: String, title: String, content: String) {
        state.value = RecipeState.IsLoading(true)
        api.createRecipe(token, title, content).enqueue(object : Callback<WrappedResponse<Recipe>> {
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<Recipe>>, response: Response<WrappedResponse<Recipe>>) {
               if (response.isSuccessful) {
                   val body = response.body() as WrappedResponse<Recipe>
                   if (body.equals("1")) {
//                       0 is success create
//                       1 is success create
//                       2 is success create
                       state.value = RecipeState.IsSuccess(0)
                   } else {
                       state.value = RecipeState.Error("Gagal saat Create Recipe")
                   }
               } else {
                   state.value = RecipeState.Error("Kesalahan saat Create Recipe")
               }
                state.value = RecipeState.IsLoading(false)
            }

        })
    }

    fun update(token: String, id: String, title: String, content: String) {
        state.value = RecipeState.IsLoading(true)
        api.updateRecipe(token, id, title, content ).enqueue(object : Callback<WrappedResponse<Recipe>>{
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<Recipe>>, response: Response<WrappedResponse<Recipe>>) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        state.value = RecipeState.IsSuccess(1)
                    } else {
                        state.value = RecipeState.Error("Gagal saat Update Recipe")
                    }
                } else {
                    state.value = RecipeState.Error("Kesalahan saat Update Recipe")
                }
                state.value = RecipeState.IsLoading(false)
            }

        })
    }

    fun deleteRecipe(token: String, id: String) {
        state.value = RecipeState.IsLoading(true)
        api.deleteRecipe(token, id).enqueue(object : Callback<WrappedResponse<Recipe>>{
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<Recipe>>, response: Response<WrappedResponse<Recipe>>) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        state.value = RecipeState.IsSuccess(2)
                    } else {
                        state.value = RecipeState.Error("Tidaj dapat menghapus Recipe")
                    }
                } else {
                    state.value = RecipeState.Error("Terjadi kesalahan saat menhapus Recipe")
                }
                state.value = RecipeState.IsLoading(false)
            }

        })
    }

    fun validate(title: String, content: String ) : Boolean {
        state.value = RecipeState.Reset


        if (title.isEmpty() || content.isEmpty()) {
            state.value = RecipeState.ShowToast("Semua form harus diisi")
            return false
        }

        if (title.length < 10) {
            state.value = RecipeState.RecipeValidation("Judul setidak nya berisi 10 char")
            return false
        }

        if (content.length < 20) {
            state.value = RecipeState.RecipeValidation("Content setidak nya berisi 20 char")
            return false
        }
        return true
    }

    fun getRecipes() = recipes
    fun getRecipe() = recipe
    fun getState() = state
}

    sealed class RecipeState {
        //didalam sealed class jika ada class maka harus meng extends class parent RecipeState()
        data class ShowToast(var message : String) : RecipeState()
        data class IsLoading(var state : Boolean = false) : RecipeState()
        data class RecipeValidation(var title : String? = null, var content : String? = null) : RecipeState()
        data class Error(var err : String? = null) : RecipeState()
        data class IsSuccess(var what : Int? = null) : RecipeState()
        object Reset : RecipeState()

}