package andlima.hafizhfy.challengelima.fragment_main

import andlima.hafizhfy.challengelima.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import andlima.hafizhfy.challengelima.R
import andlima.hafizhfy.challengelima.SplashActivity
import andlima.hafizhfy.challengelima.adapter.AdapterFilm
import andlima.hafizhfy.challengelima.func.latestUserData
import andlima.hafizhfy.challengelima.func.toast
import andlima.hafizhfy.challengelima.model.GetAllFilmResponseItem
import andlima.hafizhfy.challengelima.viewmodel.ViewModelFilm
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class HomeFragment : Fragment() {

    lateinit var adapterFilm: AdapterFilm
    lateinit var dataFilm: List<GetAllFilmResponseItem>

    // Get shared preferences
    private val sharedPrefFile = "logininfo"

    // Used for double back to exit app
    private var doubleBackToExit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get something from shared preference
        val sharedPreferences : SharedPreferences =
            requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        // Show username from SharedPreferences
        tv_username.text = sharedPreferences.getString("username_key", "username_key")

        // Check if user click back button twice
        doubleBackExit()

        initRecycler()
        getDataFilm()

        btn_goto_profile.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_profileFragment)
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

//    fun getDataFilm() {
//        ApiClient.instance.getAllFilm()
//            .enqueue(object : retrofit2.Callback<List<GetAllFilmResponseItem>>{
//                override fun onResponse(
//                    call: Call<List<GetAllFilmResponseItem>>,
//                    response: Response<List<GetAllFilmResponseItem>>
//                ) {
//                    if (response.isSuccessful) {
//                        val dataFilm = response.body()
//
//                        val adapter = AdapterFilm(dataFilm!!) {
//                            Navigation.findNavController(view!!).navigate(R.id.action_homeFragment_to_detailFragment)
//                        }
//                        val lm = LinearLayoutManager(
//                            requireContext(),
//                            LinearLayoutManager.VERTICAL,
//                            false
//                        )
//                        rv_film_list.layoutManager = lm
//                        rv_film_list.adapter = adapter
//
//                    } else {
//                        toast(requireContext(), response.message())
//                    }
//                }
//
//                override fun onFailure(call: Call<List<GetAllFilmResponseItem>>, t: Throwable) {
//                    t.message?.let { toast(requireContext(), it) }
//                }
//
//            })
//    }

    fun initRecycler() {
        rv_film_list.layoutManager = LinearLayoutManager(requireContext())
        adapterFilm = AdapterFilm {
            // Kode buat masuk ke detail selected film
            val selectedData = bundleOf("SELECTED_DATA" to it)
            Navigation.findNavController(view!!)
                .navigate(R.id.action_homeFragment_to_detailFragment, selectedData)
        }
        rv_film_list.adapter = adapterFilm
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getDataFilm() {
        val viewModel = ViewModelProvider(this).get(ViewModelFilm::class.java)
        viewModel.liveDataFilm.observe(this, Observer {
            if (it != null) {
                adapterFilm.setDataFilm(it)
                adapterFilm.notifyDataSetChanged()
            } else {
                toast(requireContext(), "it null")
            }
        })
        viewModel.makeApiFilm(requireContext()) {
            nothing_handler.visibility = View.GONE
            loading_content.visibility = View.GONE
        }
    }

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
}