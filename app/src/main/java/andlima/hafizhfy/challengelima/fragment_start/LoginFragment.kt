package andlima.hafizhfy.challengelima.fragment_start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import andlima.hafizhfy.challengelima.R
import andlima.hafizhfy.challengelima.func.*
import andlima.hafizhfy.challengelima.model.login.GetUserItem
import andlima.hafizhfy.challengelima.network.ApiClient
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class LoginFragment : Fragment() {

    // Used for double back to exit app
    private var doubleBackToExit = false

    // Get shared preferences
    private val sharedPrefFile = "logininfo"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get something from shared preference
        val sharedPreferences : SharedPreferences =
            requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        // Check if user already logged in
        isLoggedIn(sharedPreferences)

        // Check if user click back button twice
        doubleBackExit()

        // Show password
        btn_show_pwd.setOnClickListener {
            showPassword(et_password, btn_show_pwd)
        }

        // Action for login button
        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()

            hideAllPopUp(cv_email_popup, cv_password_popup)

            if (email != "" && password != "") {
                loading_login.visibility = View.VISIBLE
                login(email, password, sharedPreferences)
            } else {
                toast(requireContext(), "Please input all field")
            }
        }

        btn_goto_register.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    fun login(email: String, password: String, sharedPreferences: SharedPreferences) {
        ApiClient.instanceUser.getUser(email)
            .enqueue(object : retrofit2.Callback<List<GetUserItem>>{
                override fun onResponse(
                    call: Call<List<GetUserItem>>,
                    response: Response<List<GetUserItem>>
                ) {
                    loading_login.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body()?.isEmpty() == true) {
                            toast(requireContext(), "Unknown user")
                        } else {
                            when {
                                response.body()?.size!! > 1 -> {
                                    toast(requireContext(), "Please input your data correctly")
                                }
                                email != response.body()!![0].email -> {
//                                    toast(requireContext(), "Email not registered")
                                    showPopUp(cv_email_popup, tv_email_popup, "Email not registered")
                                }
                                password != response.body()!![0].password -> {
//                                    toast(requireContext(), "Wrong password")
                                    showPopUp(cv_password_popup, tv_password_popup, "Wrong password")
                                } else -> {
                                    val editor : SharedPreferences.Editor = sharedPreferences.edit()

                                    editor.putString("username_key", response.body()!![0].username)
                                    editor.putString("email_key", response.body()!![0].email)
                                    editor.putString("avatar_key", response.body()!![0].avatar)
                                    editor.putString("password_key", response.body()!![0].password)

                                    editor.putString("id_key", response.body()!![0].id)
                                    editor.putString("complete_name_key", response.body()!![0].complete_name)
                                    editor.putString("address_key", response.body()!![0].address)
                                    editor.putString("dateofbirth_key", response.body()!![0].dateofbirth)
                                    editor.apply()

                                    Navigation.findNavController(view!!)
                                        .navigate(R.id.action_loginFragment_to_homeFragment)
                                }
                            }
                        }
                    } else {
                        alertDialog(requireContext(), "Login failed", response.message()
                                +"\n\nTry again") {}
                    }
                }

                override fun onFailure(call: Call<List<GetUserItem>>, t: Throwable) {
                    loading_login.visibility = View.GONE
                    alertDialog(requireContext(), "Login error", "${t.message}") {}
                }

            })
    }

//    private fun login(email: String, password: String) {
//        ApiClient.instance.postLogin(RequestLogin(email, password))
//            .enqueue(object : retrofit2.Callback<List<LoginThing>>{
//
//                override fun onResponse(
//                    call: Call<List<LoginThing>>,
//                    response: Response<List<LoginThing>>
//                ) {
//                    if (response.isSuccessful) {
//                        Navigation.findNavController(view!!)
//                            .navigate(R.id.action_loginFragment_to_homeFragment)
//                        toast(requireContext(), "Welcome")
//                    } else {
////                        toast(requireContext(), "Login gagal")
//                        alertDialog(requireContext(), "Login failed", response.body().toString()) {}
//                    }
//                }
//
//                override fun onFailure(call: Call<List<LoginThing>>, t: Throwable) {
//                    alertDialog(requireContext(), "Login API error", "${t.message}") {}
//                }
//            })
//    }

    // Function to exit app with double click on back button----------------------------------------
    private fun doubleBackExit() {
        activity?.onBackPressedDispatcher
            ?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (doubleBackToExit) {
                    activity!!.finish()
                } else {
                    doubleBackToExit = true
                    toast(requireContext(), "Press again to exit")

                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        kotlin.run {
                            doubleBackToExit = false
                        }
                    }, 2000)
                }
            }
        })
    }

    // Check if user already logged in -------------------------------------------------------------
    @DelicateCoroutinesApi
    private fun isLoggedIn(sharedPreferences: SharedPreferences) {
        val sharedEmail = sharedPreferences.getString("email_key", "email_key")
        val sharedPassword = sharedPreferences.getString("password_key", "password_key")
        var isUserDataFine : Boolean

        loading_check_user_loggedin.visibility = View.VISIBLE
        if (sharedEmail != "email_key" && sharedPassword != "password_key") {
            GlobalScope.launch {
                isUserDataFine = isDataUserSame(requireContext(), sharedPreferences)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isUserDataFine) {
                        Navigation.findNavController(view!!).navigate(R.id.action_loginFragment_to_homeFragment)
                        loading_check_user_loggedin.visibility = View.GONE
                        toast(requireContext(), "Welcome back")
                    } else {
                        loading_check_user_loggedin.visibility = View.GONE
                    }
                }, 0)
            }
        } else {
            loading_check_user_loggedin.visibility = View.GONE
        }
    }
}