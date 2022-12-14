package ru.cherepanov.apps.dictionary.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryWebService {
    @GET("/findTitlesHavingPrefix/{prefix}")
    fun findTitlesHavingPrefix(
        @Path("prefix")
        prefix: String
    ): Single<List<String>>

    @GET("/getShortDefsByTitle/{title}")
    fun getShortDefsByTitle(
        @Path("title")
        title: String
    ): Single<List<WordDefDto>>

    @GET("/getFullDefById/{title}/{langNum}/{senseNum}/{glossNum}")
    fun getFullDefById(
        @Path("title")
        title: String,
        @Path("langNum")
        langNum: Int,
        @Path("senseNum")
        senseNum: Int,
        @Path("glossNum")
        glossNum: Int
    ): Single<WordDefDto>

    @GET("/getRandomWordShortDefs")
    fun getRandomWordShortDefs(
    ): Single<List<WordDefDto>>

    @GET("/findSimilarWordTitles/{searchTerm}")
    fun findSimilarWordTitles(@Path("searchTerm") searchTerm: String): Single<List<String>>
}