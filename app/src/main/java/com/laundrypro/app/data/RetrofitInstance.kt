package com.laundrypro.app.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private var retrofit: Retrofit? = null
        private val BASE_URL = "https://dhobikart-server.onrender.com/"

        val service: ApiService?
            get() {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }

                return retrofit?.create(ApiService::class.java) // need to pass a service interface which defines and describes the API endpoints and their expected responses format.
            }
    }
}