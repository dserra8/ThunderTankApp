package com.example.thundertank.home

import android.R.attr.data
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.thundertank.R
import com.example.thundertank.databinding.HomeScreenBinding
import com.example.thundertank.repository.Repository


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeScreenFragment : Fragment() {

    private lateinit var binding: HomeScreenBinding
    private lateinit var viewModelFactory: HomeScreenViewModelFactory
    private val viewModel: HomeScreenViewModel by viewModels({this},{viewModelFactory})

   // val drawable = resources.getDrawable(R.drawable.red_circle)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        Log.i("fragment", "created")
         binding = DataBindingUtil.inflate(
                inflater,
                R.layout.home_screen,
                container,
                false)

        val repository = Repository()
        val homeFragmentArgs by navArgs<HomeScreenFragmentArgs>()
        viewModelFactory = HomeScreenViewModelFactory(repository)

        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this


        loadData()
        val phDrawable: LayerDrawable = binding.phProgressBar.progressDrawable as LayerDrawable
        val phShape: Drawable = phDrawable.findDrawableByLayerId(R.id.phShape)

        val tempDrawable: LayerDrawable = binding.tempProgressBar.progressDrawable as LayerDrawable
        val tempShape: Drawable = tempDrawable.findDrawableByLayerId(R.id.tempShape)

        val clarityDrawable: LayerDrawable = binding.clarityProgressBar.progressDrawable as LayerDrawable
        val clarityShape: Drawable = clarityDrawable.findDrawableByLayerId(R.id.clarityShape)

        if(homeFragmentArgs.fromConfirm) {
            if (homeFragmentArgs.configurationChange) {
                Toast.makeText(
                    context, "Changes Applied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context, "Changes DID NOT APPLY",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getRecentRanges()
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.postResponse.observe(viewLifecycleOwner, Observer { response ->

            Toast.makeText(context,"Success: ${response.success} Message: ${response.message}", Toast.LENGTH_SHORT).show()

        })
        viewModel.getResponse.observe(viewLifecycleOwner, Observer { response ->

           // Toast.makeText(context,"Error: ${response.temp} ", Toast.LENGTH_LONG).show()

            viewModel.updateProperties(response.pH, response.temp,
                     response.clear)
        })

        viewModel.getRanges.observe(viewLifecycleOwner, Observer { response ->
            viewModel.updateRanges(response.phLow, response.phHigh,
                response.tempLow, response.tempHigh, response.clarityLow, response.clarityHigh,
                response.fishNum, response.feedingRate)
        })

        //observe methods for changing progress bar colors
        viewModel.pH.observe(viewLifecycleOwner, Observer {newPH ->
            changeProgress((newPH*10).toInt(), binding.phProgressBar)
            phShape.setTint(viewModel.changeProgressColor(viewModel.phRange.value?.get(0)!! ,viewModel.phRange.value?.get(1)!!, newPH))
        })

        viewModel.temp.observe(viewLifecycleOwner, Observer {newTemp ->
            changeProgress((newTemp*10).toInt(), binding.tempProgressBar)
            tempShape.setTint(viewModel.changeProgressColor(viewModel.tempRange.value?.get(0)!! ,viewModel.tempRange.value?.get(1)!!, newTemp))
        })
        viewModel.clarity.observe(viewLifecycleOwner, Observer {newClarity ->
            changeProgress((newClarity*10).toInt(), binding.clarityProgressBar)
            clarityShape.setTint(viewModel.changeProgressColor(viewModel.clarityRange.value?.get(0)!! ,viewModel.clarityRange.value?.get(1)!!, newClarity))
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun changeProgress(progress: Int, view: ProgressBar){
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
        editor?.putFloat("feedingRate", viewModel.feedingRate.value!!)
        editor?.apply()
    }

    private fun loadData(){
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
        val feedingRate = sp?.getFloat("feedingRate", 0F)

        if (phLow != null && phHigh != null && tempLow != null && tempHigh != null && clarityLow != null && clarityHigh != null) {
                viewModel.restoreValues(ph,temp,clarity, fishNum, feedingRate, phLow, phHigh, tempLow, tempHigh, clarityLow, clarityHigh)
            }
    }


    override fun onDestroy() {
        saveData()
        super.onDestroy()
    }
}