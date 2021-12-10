package hu.itatti.saunamaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.itatti.saunamaster.CurrentUser.currentUser
import hu.itatti.saunamaster.CurrentUser.initCurrentUserQuery
import hu.itatti.saunamaster.data.MasterList
import hu.itatti.saunamaster.databinding.FragmentAddEventBinding


class AddEventFragment : Fragment() {
    companion object{
        const val COLLECTION_EVENT = "events"
    }
    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAdd.setOnClickListener {
            uploadEvent()
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_addEventFragment_to_mainFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEventBinding.inflate(inflater,container,false)
        return binding.root
    }


    private fun uploadEvent() {
        val eventCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_EVENT)
        //val userCollection = FirebaseFirestore.getInstance().collection(RegistrationFragment.COLLECTION_USER)
       initCurrentUserQuery()
        val event = MasterList(
            FirebaseAuth.getInstance().currentUser!!.uid,
            currentUser.name,
            binding.etDate.text.toString(),
            binding.etHour.text.toString(),
            binding.etAddress.text.toString()
        )


        eventCollection.add(event).addOnSuccessListener {
            Toast.makeText(this.requireContext(),"Upload successfully", Toast.LENGTH_LONG).show()
            binding.root.findNavController().navigate(R.id.action_addEventFragment_to_mainFragment)
        }.addOnFailureListener {
            Toast.makeText(this.requireContext(),"Error: ${it.message}", Toast.LENGTH_LONG).show()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}