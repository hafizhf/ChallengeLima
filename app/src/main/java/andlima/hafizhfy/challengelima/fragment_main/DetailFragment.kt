package andlima.hafizhfy.challengelima.fragment_main

import andlima.hafizhfy.challengelima.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import andlima.hafizhfy.challengelima.R
import andlima.hafizhfy.challengelima.SplashActivity
import andlima.hafizhfy.challengelima.func.latestUserData
import andlima.hafizhfy.challengelima.model.GetAllFilmResponseItem
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class DetailFragment : Fragment() {

    // Get shared preferences
    private val sharedPrefFile = "logininfo"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get something from shared preference
        val sharedPreferences : SharedPreferences =
            requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        val selectedData = arguments?.getParcelable("SELECTED_DATA") as GetAllFilmResponseItem?

        Glide.with(this).load(selectedData?.image)
            .into(iv_thumbnail_detail)
        tv_title_film.text = selectedData?.title
        tv_director.append(selectedData?.director)
        tv_release_date.append(selectedData?.releaseDate)
        tv_synopsis.text = selectedData?.synopsis

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