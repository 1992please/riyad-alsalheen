package com.nader.riyadalsalheen.model

data class Hadith(
    val id: Int,
    val doorId: Int,
    val bookId: Int,
    val title: String,
    val text: String,
    val sharh: String
)