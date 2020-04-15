package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView

        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()
        return view
    }
    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    open inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view){

        lateinit var crime: Crime
        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)

        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)

        val requiresPoliceView: Button = itemView.findViewById(R.id.RequiresPoliceTextview)


//        init {
//            itemView.setOnClickListener(this)
//        }

/*        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
        }*/

        /*override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }*/
    }

    inner class NormalCrimeHolder(view: View): CrimeHolder(view), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            requiresPoliceView.text = getString(R.string.no_police)
                requiresPoliceView.setOnClickListener {
                    Toast.makeText(context,"No need for the police",Toast.LENGTH_SHORT).show()
                }
        }

            /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        override fun onClick(v: View?) {
                Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }

    }

    inner class SeriousCrimeHolder(view: View): CrimeHolder(view), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            requiresPoliceView.text = getString(R.string.call_police)
                requiresPoliceView.setOnClickListener {
                    Toast.makeText(context,"This crime requires the police as it is serious as kuda employee",Toast.LENGTH_SHORT).show()
                }
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }

    }

    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            /*val view = layoutInflater.inflate(R.layout.list_item_crime,parent,false)
        return  CrimeHolder(view)*/
            return when (viewType) {
                0 -> {
                    val view = layoutInflater.inflate(R.layout.list_item_crime,parent,false)
                    NormalCrimeHolder(view)
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.list_item_crime,parent,false)
                    SeriousCrimeHolder(view)
                }
            }
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            if (holder is NormalCrimeHolder){
                holder.bind(crime)
            } else if (holder is SeriousCrimeHolder){
                holder.bind(crime)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val crime = crimes[position]
            return when (crime.requiresPolice) {
                true -> 1
                else -> 0
            }
        }

    }
    companion object {
        // this creates an instance of this fragment
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}