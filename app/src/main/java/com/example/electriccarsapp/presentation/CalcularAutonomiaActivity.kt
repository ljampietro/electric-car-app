package com.example.electriccarsapp.presentation

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.electriccarsapp.R
import java.net.HttpURLConnection
import java.net.URL

class CalcularAutonomiaActivity() : AppCompatActivity()  {

    lateinit var preço: EditText
    lateinit var kmRodado: EditText
    lateinit var btnCalcular: Button
    lateinit var autonomia: TextView
    lateinit var btnClose: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_autonomia)
        setupView()
        setupListeners()
        setupCachedResult()
    }
    //
    private fun setupCachedResult() {
        val valorCalculado = getSharedPref()
        autonomia.text = valorCalculado.toString()
    }

    fun setupView(){
        preço = findViewById(R.id.et_preco_kwh)
        kmRodado = findViewById(R.id.et_km_percorrido)
        btnCalcular = findViewById(R.id.btn_calcular)
        autonomia = findViewById(R.id.tv_autonomia)
        btnClose = findViewById(R.id.iv_btn_close)
    }

    fun setupListeners(){
        btnCalcular.setOnClickListener {
            calcular()
        }
        btnClose.setOnClickListener {
            finish()
        }
    }
    fun calcular(){
        val precoDigitado = preço.text.toString().toFloat()
        val kmDigitado = kmRodado.text.toString().toFloat()

        val result = precoDigitado / kmDigitado
        autonomia.text = result.toString()
        saveSharedPref(result)

    }

    // salvando o valor do resultado da função calculo acima e salvando em cache
    fun saveSharedPref(resultado : Float){
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putFloat(getString(R.string.saved_calc), resultado)
            apply()
        }
    }
    // lendo o valor salvo em cache pela função acima
    fun getSharedPref(): Float {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getFloat(getString(R.string.saved_calc), 0.0f)

    }


}