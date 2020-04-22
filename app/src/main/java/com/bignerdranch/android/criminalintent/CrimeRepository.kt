package com.bignerdranch.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.criminalintent.CrimeDatabase.CrimeDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context){

    private val database: CrimeDatabase = Room.databaseBuilder(context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME).build()

    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()

/*
    fun getCrimes(): List<Crime> = crimeDao.getCrimes()
*/

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

/*
    fun getCrime(id: UUID): Crime? = crimeDao.getCrime(id)
*/

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)


    fun updateCrime(crime: Crime){
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }
    
    fun addCrime(crime: Crime){
        executor.execute { 
            crimeDao.addCrime(crime)
        }
    }
    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialze(context: Context){
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository{
            return INSTANCE?:throw IllegalStateException("CrimeRepository must be iniitilized")
        }
    }
}