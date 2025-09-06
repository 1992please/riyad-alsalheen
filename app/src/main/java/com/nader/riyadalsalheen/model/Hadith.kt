package com.nader.riyadalsalheen.model

data class Hadith(
    val id: Int,
    val doorId: Int,
    val bookId: Int,
    val title: String,
    val hadith: String,
    val sharh: String
)
