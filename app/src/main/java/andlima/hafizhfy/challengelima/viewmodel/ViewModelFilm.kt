package andlima.hafizhfy.challengelima.viewmodel

import andlima.hafizhfy.challengelima.MainActivity
import andlima.hafizhfy.challengelima.func.alertDialog
import andlima.hafizhfy.challengelima.func.toast
import andlima.hafizhfy.challengelima.model.GetAllFilmResponseItem
import andlima.hafizhfy.challengelima.network.ApiClient
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response

class ViewModelFilm : ViewModel() {
    lateinit var liveDataFilm : MutableLiveData<List<GetAllFilmResponseItem>>

    init {
        liveDataFilm = MutableLiveData()
    }
    fun getLiveFilmObserver() : MutableLiveData<List<GetAllFilmResponseItem>> {
        return liveDataFilm
    }

    fun makeApiFilm(context: Context, successAction : Any.()->Unit) {
        ApiClient.instance.getAllFilm()
            .enqueue(object : retrofit2.Callback<List<GetAllFilmResponseItem>>{
                override fun onResponse(
                    call: Call<List<GetAllFilmResponseItem>>,
                    response: Response<List<GetAllFilmResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        successAction(true)
                        liveDataFilm.postValue(response.body())

                    } else {
                        alertDialog(
                            context,
                            "Error",
                            "Problem occurred.\nPlease restart app or ask developer."
                            + "\n\nError message:\n" + response.message()
                        ) {
                            (context as MainActivity).finish()
                        }
                        liveDataFilm.postValue(null)
                    }
                }

                override fun onFailure(call: Call<List<GetAllFilmResponseItem>>, t: Throwable) {
                    alertDialog(
                        context,
                        "Error",
                        "Problem occurred.\nPlease restart app or ask developer."
                                + "\n\nError message:\n" + t.message
                    ) {
                        (context as MainActivity).finish()
                    }
                    liveDataFilm.postValue(null)
                }

            })
    }
}