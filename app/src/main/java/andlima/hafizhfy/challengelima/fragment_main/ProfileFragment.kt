package andlima.hafizhfy.challengelima.fragment_main

import andlima.hafizhfy.challengelima.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import andlima.hafizhfy.challengelima.R
import andlima.hafizhfy.challengelima.SplashActivity
import andlima.hafizhfy.challengelima.func.alertDialog
import andlima.hafizhfy.challengelima.func.latestUserData
import andlima.hafizhfy.challengelima.func.toast
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ProfileFragment : Fragment() {

    // Get shared preferences
    private val sharedPrefFile = "logininfo"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @DelicateCoroutinesApi
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

        // Show data from SharedPreferences into things in profile layout
        Glide.with(this).load(avatar).into(iv_image_detail)
        tv_username_detail.text = username
        tv_email_detail.text = email

        if (completeName == "complete_name $id" || completeName == "") {
            tv_complete_name.visibility = View.GONE
        } else {
            tv_complete_name.text = completeName
        }

        if (address == "address $id" || address == "") {
            tv_address.visibility = View.GONE
        } else {
            tv_address.text = address
        }

        if (dateOfBirth == "dateofbirth $id" || dateOfBirth == "") {
            tv_date_of_birth.visibility = View.GONE
        } else {
            tv_date_of_birth.text = dateOfBirth
        }

        btn_goto_edit_profile.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        btn_logout.setOnClickListener {
            alertDialog(requireContext(), "Logout", "Are you sure want to logout?") {
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()

                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment)
                toast(requireContext(), "You're logged out")
            }

        }

        // Try to get latest user data -------------------------------------------------------------
        (requireContext() as MainActivity).runOnUiThread {
            GlobalScope.async {
                latestUserData(requireContext(), sharedPreferences) {
                    startActivity(Intent((requireContext() as MainActivity), SplashActivity::class.java))
                    (requireContext() as MainActivity).finish()
                }
            }
        }
    }
}