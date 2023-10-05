package com.example.electriccarsapp.presentation
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.electriccarsapp.R
import com.example.electriccarsapp.data.CarFactory
import com.example.electriccarsapp.data.CarsApi
import com.example.electriccarsapp.data.local.CarRepository
import com.example.electriccarsapp.data.local.CarrosContract
import com.example.electriccarsapp.data.local.CarrosContract.CarEntry.TABLE_NAME
import com.example.electriccarsapp.data.local.CarsDbHelper
import com.example.electriccarsapp.domain.Carro
import com.example.electriccarsapp.presentation.adapter.CarAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.CheckedInputStream
import kotlin.math.log


class CarFragment : Fragment(){

    lateinit var listaCarros: RecyclerView
    lateinit var progress: ProgressBar
    lateinit var noInternetImage : ImageView
    lateinit var noInternetText : TextView
    lateinit var carsAPI : CarsApi

    var carrosArray : ArrayList<Carro> =  ArrayList()

    override fun onCreateView( //funcao roda com view sendo criada
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_car, container, false)
    }

    override fun onViewCreated( // funcao roda depois que a view é criada
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRetroFit()
        setupView(view)


    }

    override fun onResume() {
        super.onResume()
        if (checkForInternet(context)) {
            //callService() outra forma de chamar servicos
            getAllCars()
        } else {
            emptyState()
        }
    }

    fun setupRetroFit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://igorbag.github.io/cars-api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        carsAPI = retrofit.create(CarsApi::class.java)
    }
    fun getAllCars() {
        carsAPI.getAllCars().enqueue(object : Callback<List<Carro>> {
            override fun onResponse(call: Call<List<Carro>>, response: Response<List<Carro>>) {
               if (response.isSuccessful) {
                   progress.isVisible = false
                   noInternetImage.isVisible = false
                   noInternetText.isVisible = false
                   response.body()?.let {
                       setupList(it)
                   }
               } else{
                   Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show()
               }
            }

            override fun onFailure(call: Call<List<Carro>>, t: Throwable) {
                Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show()
            }

        })
    }

    fun emptyState() {
            progress.isVisible = false
            listaCarros.isVisible = false
            noInternetImage.isVisible = true
            noInternetText.isVisible = true

    }




    fun setupView(view: View){
        listaCarros = view.findViewById(R.id.rv_lista_carros)
        progress = view.findViewById(R.id.pb_loader)
        noInternetImage = view.findViewById(R.id.iv_empty_state)
        noInternetText = view.findViewById(R.id.tv_no_wifi)
    }

    fun setupList(lista: List<Carro>){
        val carroAdapter = CarAdapter(lista)
        listaCarros.apply {
        isVisible = true
        adapter = carroAdapter
        }
        carroAdapter.carItemLister = { carro ->
            val isSaved = CarRepository(requireContext()).saveIfNotExist(carro)
        }

    }


    fun callService(){
        val urlBase = "https://igorbag.github.io/cars-api/cars.json"
        MyTask().execute(urlBase)
        progress.visibility = View.VISIBLE
    }

    fun checkForInternet(context: Context?) : Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network =  connectivityManager.activeNetwork?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network)?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else{
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }


    }


    // IMPLEMENTAÇÃO DA TASK DE HTTP - NAO UTILIZADA, POIS IMPLEMENTAMOS O RETROFIT.
    inner class MyTask : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg url: String?): String {
            var urlConnection: HttpURLConnection? = null

            try {
                val urlBase = URL(url[0])

                urlConnection = urlBase.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 60000
                urlConnection.readTimeout = 6000
                urlConnection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val responseCode = urlConnection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    var response = urlConnection.inputStream.bufferedReader().use { it.readText()}
                    publishProgress(response)
                } else {
                    Log.e("Erro", "Serviço indisponivel no momento")

                }


            }catch (ex: Exception){
                Log.e("Erro", "erro ao realizar processamento ....")
            } finally {
                urlConnection?.disconnect()
            }
            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                val jsonArray = JSONTokener(values[0]).nextValue() as JSONArray

                for( i in 0 until jsonArray.length()){
                    val id = jsonArray.getJSONObject(i).getString("id")
                    Log.d("id: ", id)
                    val preco = jsonArray.getJSONObject(i).getString("preco")
                    Log.d("preco: ", preco)
                    val bateria = jsonArray.getJSONObject(i).getString("bateria")
                    Log.d("bateria: ", bateria)
                    val potencia = jsonArray.getJSONObject(i).getString("potencia")
                    Log.d("potencia: ", potencia)
                    val recarga = jsonArray.getJSONObject(i).getString("recarga")
                    Log.d("recarga: ", recarga)
                    val urlPhoto = jsonArray.getJSONObject(i).getString("urlPhoto")
                    Log.d("foto: ", urlPhoto)

                    val model = Carro(
                        id = id.toInt(),
                        preco =  preco,
                        bateria = bateria,
                        potencia =  potencia,
                        recarga = recarga,
                        urlPhoto = urlPhoto,
                        isFavorite = false

                    )
                    carrosArray.add(model)
                    Log.d("model ", model.toString())

                }
                progress.visibility = View.GONE
                noInternetImage.visibility = View.GONE
                noInternetText.visibility = View.GONE
                //setupList()

            } catch (ex: Exception) {
                Log.e("Erro ->", ex.message.toString())

            }

        }



    }



}