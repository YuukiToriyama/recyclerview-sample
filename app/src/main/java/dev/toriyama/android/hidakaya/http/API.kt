package dev.toriyama.android.hidakaya.http

import retrofit2.http.GET

interface API {
    @GET("menu/all.json")
    suspend fun getAllMenu(): List<Menu>
}