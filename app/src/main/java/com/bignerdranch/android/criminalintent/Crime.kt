package com.bignerdranch.android.criminalintent

import androidx.room.*
import java.util.*

@Entity
data class Crime (@PrimaryKey val id: UUID = UUID.randomUUID(),
                  var title: String = "",
                  var date: Date = Date(),
                  var time: Date = Date(),
                  var isSolved: Boolean = false
)