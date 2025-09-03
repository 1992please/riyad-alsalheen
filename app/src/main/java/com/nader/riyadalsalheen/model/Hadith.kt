package com.nader.riyadalsalheen.model

data class Hadith(
    val id: Int,
    val doorId: Int,
    val bookId: Int,
    val titleAr: String,
    val textAr: String,
    val sharhAr: String
)
