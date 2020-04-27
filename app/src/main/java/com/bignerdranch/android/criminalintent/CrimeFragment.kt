package com.bignerdranch.android.criminalintent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*

private const val DIALOG_DATE = "DialogDate"

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val PERMIT_CONTACT = 1
private const val DATE_FORMAT = "EEE, MMM, dd"
class CrimeFragment: Fragment(), DatePickerFragment.Callbacks {


    /*here we created the crimefragment and attached the model crime properties in model crime class to this fragment*/
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callSuspect: Button

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID

        crimeDetailViewModel.loadCrime(crimeId)
    }

    /*
    * onCreateView is used to inflate views in fragments*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText

        dateButton = view?.findViewById(R.id.crime_date) as Button

        /*dateButton.apply {
            text = crime.date.toString()
            isEnabled = true
        }*/


        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        callSuspect = view.findViewById(R.id.crime_call) as Button

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { crime -> crime?.let { this.crime = crime
        updateUI()} })
    }
    override fun onStart() {
        super.onStart()

        /*
        * the below is a watcher for the edittext */
        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener{ _,
                isChecked ->
            crime.isSolved = isChecked}
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.parentFragmentManager,
                DIALOG_DATE) }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent,getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
//            pickContactIntent.addCategory(Intent.CATEGORY_HOME)
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(pickContactIntent,
            PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null){
                isEnabled = false
            }

        }
        callSuspect.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${crime.phoneNumber}"))
            startActivity(callIntent)
        }
    }

    override fun onStop() {
        super.onStop()

        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            // does not show animation
            view?.jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()){
            suspectButton.text = crime.suspect
        }
        if (crime.phoneNumber.isNotEmpty()){
            callSuspect.isEnabled = true
            callSuspect.text = "Call Suspect ${crime.suspect}"
            } else{
            callSuspect.isEnabled = false
            callSuspect.text = "No suspect To call"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when{
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null ->{
                val contactUri: Uri? = data.data
                // Specify which fields you want your query to return values for
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts._ID)
                // Perform your query - the contactUri is like a "where" clause here
                val cursor = requireActivity().contentResolver.query(contactUri!!,queryFields,null,null,null)
                cursor?.use { // Verify cursor contains at least one result
                    if (it.count == 0){
                        return
                    } // Pull out the first column of the first row of data -
                    // that is your suspect's name
                    it.moveToFirst()
                val suspect = it.getString(0)
                    val suspectID = it.getLong(1)
                crime.suspect = suspect
                crimeDetailViewModel.saveCrime(crime)
                suspectButton.text = suspect
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMIT_CONTACT)
                }else{getPhoneNumber(suspectID)}}
            }
        }
    }
    private fun getCrimeReport():String{
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT,crime.date).toString()
        val suspect = if (crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report,crime.title,dateString,solvedString,suspect)
    }

    private fun getPhoneNumber(id:Long){
        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=$id",null,null
        )
        cursor?.use {
            if (it.count == 0){
                Log.d(TAG,"getPhone: null")
                return
            }
            it.moveToFirst()
            val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            crime.phoneNumber = phone.toString()
            crimeDetailViewModel.saveCrime(crime)
            Log.d(TAG,"getPhone: $phone")
        }
    }
    companion object {

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {

                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }
}