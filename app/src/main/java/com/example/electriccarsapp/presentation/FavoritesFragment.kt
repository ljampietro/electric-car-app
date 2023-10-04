package com.example.electriccarsapp.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.electriccarsapp.R
import com.example.electriccarsapp.data.local.CarRepository
import com.example.electriccarsapp.domain.Carro
import com.example.electriccarsapp.presentation.adapter.CarAdapter

class FavoritesFragment : Fragment() {

    lateinit var listaCarrosFav : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupList()

    }

    private fun getCarsOnLocalDb(): List<Carro> {
        val repository = CarRepository(requireContext())
        val carList = repository.getAll()
        Log.d("listaCarros", carList.size.toString())
        return carList
    }


    fun setupView(view: View){
        listaCarrosFav = view.findViewById(R.id.rv_lista_carros_fav)
    }

    fun setupList(){
        val cars = getCarsOnLocalDb()
        val carroAdapter = CarAdapter(cars, isFavoriteScreen = true)
        listaCarrosFav.apply {
            isVisible = true
            adapter = carroAdapter
        }
        carroAdapter.carItemLister = { carro ->
//            val isSaved = CarRepository(requireContext()).saveIfNotExist(carro)
        }

    }
}
