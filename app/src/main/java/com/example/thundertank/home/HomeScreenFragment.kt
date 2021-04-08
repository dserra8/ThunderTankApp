package com.example.thundertank.home

import android.R.attr.data
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.thundertank.R
import com.example.thundertank.databinding.HomeScreenBinding
import com.example.thundertank.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

//android:networkSecurityConfig="@xml/network_security_config"
/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeScreenFragment : Fragment() {

    private lateinit var binding: HomeScreenBinding
    private lateinit var viewModelFactory: HomeScreenViewModelFactory
    private val viewModel: HomeScreenViewModel by viewModels({ this }, { viewModelFactory })

    // val drawable = resources.getDrawable(R.drawable.red_circle)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_screen,
            container,
            false
        )

        val repository = Repository()
        val homeFragmentArgs by navArgs<HomeScreenFragmentArgs>()
        viewModelFactory = HomeScreenViewModelFactory(repository)

        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this


        loadData()
        loadTankName()
        val phDrawable: LayerDrawable = binding.phProgressBar.progressDrawable as LayerDrawable
        val phShape: Drawable = phDrawable.findDrawableByLayerId(R.id.phShape)

        val tempDrawable: LayerDrawable = binding.tempProgressBar.progressDrawable as LayerDrawable
        val tempShape: Drawable = tempDrawable.findDrawableByLayerId(R.id.tempShape)

        val clarityDrawable: LayerDrawable =
            binding.clarityProgressBar.progressDrawable as LayerDrawable
        val clarityShape: Drawable = clarityDrawable.findDrawableByLayerId(R.id.clarityShape)

        if (homeFragmentArgs.fromConfirm) {
            if (homeFragmentArgs.configurationChange) {
                Toast.makeText(
                    context, "Changes Applied",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.postFeedingRate()
            } else {
                Toast.makeText(
                    context, "Changes DID NOT APPLY",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getRecentRanges()
            viewModel.getRotations()
            viewModel.fishNum
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.eventClarityPopUp.observe(viewLifecycleOwner, Observer { event ->
            if (event) {
                viewModel.eventClarityPopUpComplete()
                val dialog = LayoutInflater.from(context).inflate(R.layout.clarity_pop_up, null)
                val mBuilder = AlertDialog.Builder(context).setView(dialog)
                val view = dialog.findViewById<TextView>(R.id.num)
                view.text = "${viewModel.clarity.value} NTU"
                val mAlertDialog = mBuilder.show()

            }
        })

        viewModel.eventFab.observe(viewLifecycleOwner, Observer { event ->
            if (event) {
                viewModel.eventFabComplete()
                val dialog = LayoutInflater.from(context).inflate(R.layout.tips_layout, null)
                val mBuilder = AlertDialog.Builder(context).setView(dialog).setTitle("Tips")
                val view = dialog.findViewById<TextView>(R.id.tips_text)
                view.text = viewModel.currentTip.value
                val mAlertDialog = mBuilder.show()
                val button = dialog.findViewById<Button>(R.id.next_tip)
                val videoView: YouTubePlayerView = dialog.findViewById<YouTubePlayerView>(R.id.youtube_player_view)

                videoView.getPlayerUiController().showFullscreenButton(true)
                videoView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                        val videoId = viewModel.currentVideo.value
                        if (videoId != null) {
                            youTubePlayer.cueVideo(videoId, 0f)
                        }
                    }
                })

//                videoView.getPlayerUiController().setFullScreenButtonClickListener(View.OnClickListener {
//                    if (videoView.isFullScreen()) {
//                        videoView.exitFullScreen()
//                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//                        // Show ActionBar
//                        if (supportActionBar != null) {
//                            supportActionBar!!.show()
//                        }
//                    } else {
//                        third_party_player_view.enterFullScreen()
//                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//                        // Hide ActionBar
//                        if (supportActionBar != null) {
//                            supportActionBar!!.hide()
//                        }
//                    }
//                })

                button.setOnClickListener {
                    viewModel.nextTip()
                    view.text = viewModel.currentTip.value


            }
            }
        })

        viewModel.eventPhPopUp.observe(viewLifecycleOwner, Observer { event ->
            if (event) {
                viewModel.eventPhPopUpComplete()
                val dialog = LayoutInflater.from(context).inflate(R.layout.ranges_pop_up, null)
                val mBuilder = AlertDialog.Builder(context).setView(dialog)
                dialog.findViewById<TextView>(R.id.range1).text = "  Low: ${viewModel.phRange.value?.get(0)} pH"
                dialog.findViewById<TextView>(R.id.range2).text = "  High: ${viewModel.phRange.value?.get(1)} pH"
                val mAlertDialog = mBuilder.show()

            }
        })

        viewModel.eventTempPopUp.observe(viewLifecycleOwner, Observer { event ->
            if (event) {
                viewModel.eventTempPopUpComplete()
                val dialog = LayoutInflater.from(context).inflate(R.layout.ranges_pop_up, null)
                val mBuilder = AlertDialog.Builder(context).setView(dialog)
                dialog.findViewById<TextView>(R.id.range1).text = "  Low: ${viewModel.tempRange.value?.get(0)} \u2109 "
                dialog.findViewById<TextView>(R.id.range2).text = "  High: ${viewModel.tempRange.value?.get(1)} \u2109"
                val mAlertDialog = mBuilder.show()

            }
        })
        viewModel.getRotations.observe(viewLifecycleOwner, Observer { response ->

            viewModel.updateFeedingRate(response.rotations)
        })

        viewModel.getResponse.observe(viewLifecycleOwner, Observer { response ->

            //  Toast.makeText(context,"Error: ${response.temp} ", Toast.LENGTH_LONG).show()

            viewModel.updateProperties(
                response.pH, response.temp,
                response.clear
            )
        })

        viewModel.getRanges.observe(viewLifecycleOwner, Observer { response ->
            viewModel.updateRanges(
                response.phLow, response.phHigh,
                response.tempLow, response.tempHigh, response.clarityLow, response.clarityHigh,
                response.fishNum
            )
        })

        //observe methods for changing progress bar colors
        viewModel.pH.observe(viewLifecycleOwner, Observer { newPH ->
            if (viewModel.fishNum.value!! > 0) {
                changeProgress((newPH * 10).toInt(), binding.phProgressBar)
                phShape.setTint(
                    viewModel.changeProgressColor(
                        viewModel.phRange.value?.get(0)!!,
                        viewModel.phRange.value?.get(1)!!,
                        newPH
                    )
                )
            } else {
                changeProgress((newPH * 10).toInt(), binding.phProgressBar)
                phShape.setTint(Color.GRAY)
            }
        })

        viewModel.temp.observe(viewLifecycleOwner, Observer { newTemp ->
            if (viewModel.fishNum.value!! > 0) {
                changeProgress((newTemp * 10).toInt(), binding.tempProgressBar)
                tempShape.setTint(
                    viewModel.changeProgressColor(
                        viewModel.tempRange.value?.get(0)!!,
                        viewModel.tempRange.value?.get(1)!!,
                        newTemp
                    )
                )
            } else {
                changeProgress((newTemp * 10).toInt(), binding.tempProgressBar)
                tempShape.setTint(Color.GRAY)
            }
        })
        viewModel.clarity.observe(viewLifecycleOwner, Observer { newClarity ->
            if (viewModel.fishNum.value!! > 0) {
                changeProgress(4000, binding.clarityProgressBar)
                clarityShape.setTint(
                    viewModel.changeClarity()
                )
            } else {
                changeProgress(4000, binding.clarityProgressBar)
                clarityShape.setTint(Color.GRAY)
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun changeProgress(progress: Int, view: ProgressBar) {
        view.progress = progress;
    }

    private fun saveData() {
        val sharedPreferences =
            this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putFloat("ph", viewModel.pH.value!!)
        editor?.putFloat("temp", viewModel.temp.value!!)
        editor?.putFloat("clarity", viewModel.clarity.value!!)
        editor?.putFloat("phLow", viewModel.phRange.value?.get(0)!!)
        editor?.putFloat("phHigh", viewModel.phRange.value?.get(1)!!)
        editor?.putFloat("tempLow", viewModel.tempRange.value?.get(0)!!)
        editor?.putFloat("tempHigh", viewModel.tempRange.value?.get(1)!!)
        editor?.putFloat("clarityLow", viewModel.clarityRange.value?.get(0)!!)
        editor?.putFloat("clarityHigh", viewModel.clarityRange.value?.get(1)!!)
        editor?.putInt("numFish", viewModel.fishNum.value!!)
        editor?.apply()
    }

    private fun loadData() {
        val sp =
            this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val ph = sp?.getFloat("ph", 0F)
        val temp = sp?.getFloat("temp", 0F)
        val clarity = sp?.getFloat("clarity", 0F)
        val fishNum = sp?.getInt("numFish", 0)
        val phLow = sp?.getFloat("phLow", 0F)
        val phHigh = sp?.getFloat("phHigh", 0F)
        val tempLow = sp?.getFloat("tempLow", 0F)
        val tempHigh = sp?.getFloat("tempHigh", 0F)
        val clarityHigh = sp?.getFloat("clarityHigh", 0F)
        val clarityLow = sp?.getFloat("clarityLow", 0F)


        if (phLow != null && phHigh != null && tempLow != null && tempHigh != null && clarityLow != null && clarityHigh != null) {
            viewModel.restoreValues(
                ph,
                temp,
                clarity,
                fishNum,
                fishNum,
                phLow,
                phHigh,
                tempLow,
                tempHigh,
                clarityLow,
                clarityHigh
            )
        }
    }

    private fun loadTankName() {
        val sp =
            this.activity?.getSharedPreferences("other", Context.MODE_PRIVATE)
        val tankName = sp?.getString("tankName", "TankName")
        binding.tankName.text = tankName
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        saveData()
        super.onStop()
    }
}