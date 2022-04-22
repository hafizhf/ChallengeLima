package andlima.hafizhfy.challengelima.func

import andlima.hafizhfy.challengelima.R
import andlima.hafizhfy.challengelima.model.login.GetUserItem
import andlima.hafizhfy.challengelima.network.ApiClient
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.logging.Handler

// Function to easy making Toast -------------------------------------------------------------------
fun toast(context: Context, message : String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}

// Function to easy making SnackBar ----------------------------------------------------------------
fun snackbarLong(view: View, message: String) {
    val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snack.setAction("Ok") {
        snack.dismiss()
    }
    snack.show()
}

// Function to easy making SnackBar ----------------------------------------------------------------
fun snackbarIndefinite(view: View, message: String) {
    val snack = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
    snack.setAction("Ok") {
        snack.dismiss()
    }
    snack.show()
}

// Function to easy making AlertDialog -------------------------------------------------------------
fun alertDialog(context: Context, title: String, message: String, action: Any.()->Unit) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            action(true)
        }
        .setCancelable(false)
        .show()
}

// Function to hide all error pop up ---------------------------------------------------------------
fun hideAllPopUp(cardView1: CardView, cardView2: CardView) {
    cardView1.visibility = View.GONE
    cardView2.visibility = View.GONE
}

// Function to show error pop up -------------------------------------------------------------------
fun showPopUp(cardViewID: CardView, textViewID: TextView, message: String) {
    cardViewID.visibility = View.VISIBLE
    textViewID.text = message
}

// Function to hide error pop up -------------------------------------------------------------------
fun hidePopUp(cardViewID: CardView) {
    cardViewID.visibility = View.GONE
}

// Function to show password on password EditText --------------------------------------------------
fun showPassword(editText: EditText, imageView: ImageView) {
    val hidden = PasswordTransformationMethod.getInstance()
    val show = HideReturnsTransformationMethod.getInstance()

    if (editText.transformationMethod == hidden) {
        editText.transformationMethod = show
        imageView.setImageResource(R.drawable.ic_eye_off)
    } else {
        editText.transformationMethod = hidden
        imageView.setImageResource(R.drawable.ic_eye)
    }
}

// Function to check match data between SharedPreferences and live database from rest api ----------
suspend fun isDataUserSame(context: Context, sharedPreferences: SharedPreferences) : Boolean {
    // Result code for user data
    // True = Still same, at least email and password
    // False = Password has been changed, or account disappear
    var result = false

    // Create SharedPreferences editor
    val editor : SharedPreferences.Editor = sharedPreferences.edit()

    // Internal function to clear SharedPreferences
    fun clearSharedPreferences() {
        editor.clear()
        editor.apply()
    }

    // Put data from SharedPreferences to variable
    // val id = sharedPreferences.getString("id_key", "id_key")
    val avatar = sharedPreferences.getString("avatar_key", "avatar_key")
    val username = sharedPreferences.getString("username_key", "username_key")
    val email = sharedPreferences.getString("email_key", "email_key")
    val password = sharedPreferences.getString("password_key", "password_key")
    val completeName = sharedPreferences.getString("complete_name_key", "complete_name_key")
    val address = sharedPreferences.getString("address_key", "address_key")
    val dateOfBirth = sharedPreferences.getString("dateofbirth_key", "dateofbirth_key")

    // Get latest data from API
    ApiClient.instanceUser.getUser(email!!)
        .enqueue(object : retrofit2.Callback<List<GetUserItem>>{
            override fun onResponse(
                call: Call<List<GetUserItem>>,
                response: Response<List<GetUserItem>>
            ) {
                if (response.isSuccessful) {

                    when {
                        response.body()!!.isEmpty() -> {
                            toast(context, "Logged out")
                            clearSharedPreferences()
                            result = false
                        }
                        response.body()!!.size > 1 -> {
                            toast(context, "Redundant account detected\nLogged out")
                            clearSharedPreferences()
                            result = false
                        }
                        password != response.body()!![0].password -> {
                            toast(context, "Logged out due to password changed")
                            clearSharedPreferences()
                            result = false
                        }
                        email != response.body()!![0].email -> {
                            toast(context, "Logged out due to email changed")
                            clearSharedPreferences()
                            result = false
                        }
                        username != response.body()!![0].username
                                || avatar != response.body()!![0].avatar
                                || completeName != response.body()!![0].complete_name
                                || address != response.body()!![0].address
                                || dateOfBirth != response.body()!![0].dateofbirth -> {

                            // Clear previous SharedPreferences
                            clearSharedPreferences()

                            // Add latest data to SharedPreferences
                            editor.putString("username_key", response.body()!![0].username)
                            editor.putString("email_key", response.body()!![0].email)
                            editor.putString("avatar_key", response.body()!![0].avatar)
                            editor.putString("password_key", response.body()!![0].password)
                            editor.putString("id_key", response.body()!![0].id)
                            editor.putString("complete_name_key", response.body()!![0].complete_name)
                            editor.putString("address_key", response.body()!![0].address)
                            editor.putString("dateofbirth_key", response.body()!![0].dateofbirth)
                            editor.apply()

                            result = true
                        }
                        else -> {
                            // Nothing changed, let user enjoy app
                            result = true
                        }
                    }
                } else {
                    alertDialog(context, "Something went wrong",
                        "${response.message()}\n\nPlease restart app or @developer") {}
                    result = false
                }
            }

            override fun onFailure(call: Call<List<GetUserItem>>, t: Throwable) {
                alertDialog(context, "Something went wrong",
                    "${t.message}\n\nPlease restart app or @developer") {}
                result = false
            }
        })

    // Give delay because function can give return faster than the API connection, and it's not good
    delay(3000)
    return result
}

// Function to match user latest data while app is running -------------------------------------
@DelicateCoroutinesApi
suspend fun latestUserData(context: Context, sharedPreferences: SharedPreferences, dataNotMatchAction : Any.()->Unit) {
    // Make it as infinity loop but slower hehe
    var z = 3
    var isDataMatch : Boolean

    for (i in 1..z) {
        GlobalScope.launch {
            isDataMatch = isDataUserSame(context, sharedPreferences)
            (delay(10000))
            if (!isDataMatch) {
                dataNotMatchAction(true)
            }
            z++
        }
    }
}
