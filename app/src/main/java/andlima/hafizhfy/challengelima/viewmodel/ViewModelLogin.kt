package andlima.hafizhfy.challengelima.viewmodel

import andlima.hafizhfy.challengelima.model.login.GetUserItem
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelLogin : ViewModel() {
    lateinit var livaDataUser : MutableLiveData<List<GetUserItem>>

    var email = ""
    var password = ""

    init {
        livaDataUser = MutableLiveData()
    }

    fun makeApiUser(context: Context, successAction : Any.()->Unit) {

    }
}