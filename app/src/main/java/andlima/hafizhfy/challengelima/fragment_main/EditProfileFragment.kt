package andlima.hafizhfy.challengelima.fragment_main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import andlima.hafizhfy.challengelima.R
import andlima.hafizhfy.challengelima.func.alertDialog
import andlima.hafizhfy.challengelima.func.snackbarLong
import andlima.hafizhfy.challengelima.func.toast
import andlima.hafizhfy.challengelima.model.login.GetUserItem
import andlima.hafizhfy.challengelima.model.login.PutUser
import andlima.hafizhfy.challengelima.network.ApiClient
import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import retrofit2.Call
import retrofit2.Response

class EditProfileFragment : Fragment() {

    // Get shared preferences
    private val sharedPrefFile = "logininfo"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get something from shared preference
        val sharedPreferences : SharedPreferences =
            requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        val id = sharedPreferences.getString("id_key", "id_key")
        val avatar = sharedPreferences.getString("avatar_key", "avatar_key")
        val username = sharedPreferences.getString("username_key", "username_key")
        val email = sharedPreferences.getString("email_key", "email_key")

        val completeName = sharedPreferences.getString("complete_name_key", "complete_name_key")
        val address = sharedPreferences.getString("address_key", "address_key")
        val dateOfBirth = sharedPreferences.getString("dateofbirth_key", "dateofbirth_key")

        et_edit_name.setText(username)
        et_edit_email.setText(email)
        et_edit_avatar.setText(avatar)

        if (completeName == "complete_name $id" || completeName == "") {
            et_edit_complete_name.setText("")
        } else {
            et_edit_complete_name.setText(completeName)
        }

        if (dateOfBirth == "dateofbirth $id" || dateOfBirth == "") {
            et_edit_dateofbirth.setText("")
        }else {
            et_edit_dateofbirth.setText(dateOfBirth)
        }

        if (address == "address $id" || address == "") {
            et_edit_address.setText("")
        } else {
            et_edit_address.setText(address)
        }

        btn_save_profile.setOnClickListener {
            when {
                et_edit_name.text.toString() == "" -> {
                    toast(requireContext(), "Username field cannot be empty")
                }
                et_edit_email.text.toString() == "" -> {
                    toast(requireContext(), "Email field cannot be empty")
                }
                else -> {
                    updateProfile(
                        id!!.toInt(),
                        et_edit_dateofbirth.text.toString(),
                        et_edit_address.text.toString(),
                        et_edit_avatar.text.toString(),
                        et_edit_complete_name.text.toString(),
                        et_edit_email.text.toString(),
                        et_edit_name.text.toString(),
                        sharedPreferences
                    )
                }
            }
        }
    }

    private fun updateProfile(
        id: Int,
        dateofbirth: String,
        address: String,
        avatar: String,
        complete_name: String,
        email: String,
        username: String,
        sharedPreferences: SharedPreferences
    ) {
        ApiClient.instanceUser
            .updateUser(id, PutUser(dateofbirth, address, avatar, complete_name, email, username))
            .enqueue(object : retrofit2.Callback<GetUserItem>{
                override fun onResponse(
                    call: Call<GetUserItem>,
                    response: Response<GetUserItem>
                ) {
                    if (response.isSuccessful) {
                        snackbarLong(requireView(), "Update saved")

                        val editor = sharedPreferences.edit()
                        editor.putString("username_key", response.body()!!.username)
                        editor.putString("email_key", response.body()!!.email)
                        editor.putString("avatar_key", response.body()!!.avatar)
                        editor.putString("complete_name_key", response.body()!!.complete_name)
                        editor.putString("address_key", response.body()!!.address)
                        editor.putString("dateofbirth_key", response.body()!!.dateofbirth)
                        editor.apply()

                        Navigation.findNavController(view!!)
                            .navigate(R.id.action_editProfileFragment_to_homeFragment)
                    } else {
                        alertDialog(
                            requireContext(),
                            "Update profile failed",
                            response.message() +"\n\nTry again"
                        ) {}
                    }
                }

                override fun onFailure(call: Call<GetUserItem>, t: Throwable) {
                    alertDialog(
                        requireContext(),
                        "Update profile error",
                        "${t.message}"
                    ) {}
                }

            })
    }
}