package hu.itatti.saunamaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import hu.itatti.saunamaster.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            if(!isFormValid()){
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(
                binding.etEmail.text.toString(), binding.etPassword.text.toString()
            ).addOnSuccessListener {
                //mAuth.currentUser?.uid
                Toast.makeText(
                    this.requireContext(),
                    "Successful login as ${binding.etEmail.text}",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)

            }.addOnFailureListener {
                Toast.makeText(
                    this.requireContext(),
                    "Login error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
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

            else -> true
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}