package hu.itatti.saunamaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.itatti.saunamaster.data.Users
import hu.itatti.saunamaster.databinding.FragmentRegistrationBinding


class RegistrationFragment : Fragment() {
    companion object{
        const val COLLECTION_USER = "users"

    }

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegistrar.setOnClickListener{
            if(!isFormValid()){
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.etEmail.text.toString(), binding.etPassword.text.toString()
            ).addOnSuccessListener {
                    mAuth.signInWithEmailAndPassword(
                        binding.etEmail.text.toString(), binding.etPassword.text.toString()
                    ).addOnSuccessListener {

                        val userCollection = FirebaseFirestore.getInstance().collection(
                            COLLECTION_USER)
                        val user = Users(
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            binding.etName.text.toString(),
                            binding.etEmail.text.toString(),
                            binding.rbGuest.isChecked,
                            binding.rbMaster.isChecked
                        )

                        userCollection.add(user).addOnSuccessListener {
                            Toast.makeText(this.requireContext(),"Upload successfully", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this.requireContext(),"Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }


                        Toast.makeText(
                            this.requireContext(),
                            "Successful login as ${binding.etName.text}",
                            Toast.LENGTH_LONG
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this.requireContext(),
                            "Login error: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

            }.addOnFailureListener {
                Toast.makeText(
                    this.requireContext(),
                    "Error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }

            findNavController().navigate(R.id.action_registrationFragment_to_mainFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_mainFragment)
        }
    }

    private fun isFormValid(): Boolean {
        return when {
            binding.etEmail.text!!.isEmpty() -> {
                binding.etEmail.error = "This field can not be empty"
                false
            }
            binding.etPassword.text!!.isEmpty() ->{
                binding.etPassword.error = "This field can not be empty"
                false
            }
            binding.etName.text!!.isEmpty() ->{
                binding.etName.error = "This field can not be empty"
                false
            }

            else -> true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}