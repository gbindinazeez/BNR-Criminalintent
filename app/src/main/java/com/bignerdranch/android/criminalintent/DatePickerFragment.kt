package com.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class DatePickerFragment: DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val resultDate: Date = GregorianCalendar(year, month, dayOfMonth).time
            targetFragment?.let { fragment -> (fragment as Callbacks).onDateSelected(resultDate) }
        }
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calender = Calendar.getInstance()
        calender.time = date
        val initialYear = calender.get(Calendar.YEAR)
        val initialMonth = calender.get(Calendar.MONTH)
        val initialDay = calender.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,initialMonth,initialDay
        )
    }

    companion object{
        fun newInstance(date: Date): DatePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_DATE,date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}