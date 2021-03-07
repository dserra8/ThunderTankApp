package com.example.thundertank.home

import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.thundertank.R
import com.example.thundertank.databinding.HomeScreenBinding
import com.example.thundertank.network.TanksProperties
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
         binding = DataBindingUtil.inflate(
                inflater,
                R.layout.home_screen,
                container,
                false)

        val repository = Repository()
        viewModelFactory = HomeScreenViewModelFactory(repository)


        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this

        val drawable: LayerDrawable = binding.phProgressBar.progressDrawable as LayerDrawable
        val shape: Drawable = drawable.findDrawableByLayerId(R.id.shape)

        val myPost = TanksProperties(20.0,5.1,9.9,1.5)
        viewModel.pushPost(myPost)

        viewModel.response2.observe(viewLifecycleOwner, Observer { response ->
            if(response.isSuccessful){
                Log.d("main", response.body().toString())
                Log.d("main", response.code().toString())
                Log.d("main", response.message())
                Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Log.d("main", response.errorBody().toString())
                Toast.makeText(context,"${response.errorBody().toString()}", Toast.LENGTH_SHORT).show()

            }
        })
        viewModel.response.observe(viewLifecycleOwner, Observer { response ->
            if(response.isSuccessful){
                viewModel.updateProperties(response.body()?.pH!!, response.body()?.temp!!,
                    response.body()?.salt!!, response.body()?.clear!!)
            }
            else
            {
                Log.d("Response", response.errorBody().toString())
            }
        })
        viewModel.pH.observe(viewLifecycleOwner, Observer {newPH ->
            ObjectAnimator.ofInt(binding.phProgressBar,"progress",(newPH*10).toInt())
                    .setDuration(50)
                    .start()
            shape.setTint(viewModel.changeProgressColor(viewModel.phRange.value?.get(0)!! ,viewModel.phRange.value?.get(1)!!, newPH))
        })

        viewModel.phRange.observe(viewLifecycleOwner, Observer { newRange ->
           shape.setTint(viewModel.changeProgressColor(newRange[0],newRange[1], viewModel.pH.value!!))
        })

        binding.buttonTest.setOnClickListener {viewModel.onClickTest(binding.testingPH.text)
        }

        // Inflate the layout for this fragment
        return binding.root
    }


}