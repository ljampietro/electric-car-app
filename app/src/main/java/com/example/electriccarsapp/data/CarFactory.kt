package com.example.electriccarsapp.data

import com.example.electriccarsapp.domain.Carro

object CarFactory {

    val list = listOf(
        Carro(
            id = 1,
            preco = "R$ 300.000,00",
            bateria = "100Kwh",
            potencia = "200cv",
            recarga = "30 min",
            urlPhoto = "www.presearch.com",
            isFavorite = false


        )

    )
}