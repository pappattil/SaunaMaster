package hu.itatti.saunamaster

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import hu.itatti.saunamaster.CurrentUser.currentUser
import hu.itatti.saunamaster.CurrentUser.initCurrentUserQuery
import hu.itatti.saunamaster.adapter.MasterListAdapter
import hu.itatti.saunamaster.data.MasterList
import hu.itatti.saunamaster.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private lateinit var eventListener: EventListener<QuerySnapshot>
    private lateinit var queryRef: CollectionReference
    private var listenerReg: ListenerRegistration?=null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var masterListAdapter: MasterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initRecylerView()
    }

    private fun initRecylerView() {
        var adapterUid = FirebaseAuth.getInstance().currentUser?.uid
        adapterUid = if (adapterUid != null) {
            FirebaseAuth.getInstance().currentUser?.uid
        } else {
            ""
        }
        masterListAdapter = MasterListAdapter(
            adapterUid!!
        )

        binding.recyclerView.adapter = masterListAdapter
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        initFirebaseQuery()
    }

    private fun initFirebaseQuery() {
        queryRef= FirebaseFirestore.getInstance().collection(AddEventFragment.COLLECTION_EVENT)
        eventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?,e: FirebaseFirestoreException?){
                if(e != null){
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    return
                }
                for (docChange in querySnapshot?.documentChanges!!){
                    if (docChange.type == DocumentChange.Type.ADDED){
                        val list = docChange.document.toObject(MasterList::class.java)

                        masterListAdapter.addEvent(list, docChange.document.id)
                    }else if (docChange.type== DocumentChange.Type.REMOVED){
                        masterListAdapter.removeEventByKey(docChange.document.id)
                    }
                }
            }
        }

        listenerReg = queryRef.addSnapshotListener(eventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.actionExit){
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()}

        if (item.itemId == R.id.actionRegister){
             binding.root.findNavController().navigate(R.id.action_mainFragment_to_registrationFragment)
        }

        if (item.itemId == R.id.actionAddEvent){
            binding.root.findNavController().navigate(R.id.action_mainFragment_to_addEventFragment)
        }
        if (item.itemId == R.id.actionSignIn) {
            binding.root.findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }
        if (item.itemId == R.id.actionSignOut) {
            FirebaseAuth.getInstance().signOut()
            initRecylerView()
            Toast.makeText(requireContext(),"Successful logout",Toast.LENGTH_LONG).show()
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        initCurrentUserQuery()

        if (FirebaseAuth.getInstance().currentUser?.uid != null) {
            menu.findItem(R.id.actionSignOut)?.isVisible = true
            menu.findItem(R.id.actionSignIn)?.isVisible = false
            menu.findItem(R.id.actionRegister)?.isVisible = false
            menu.findItem(R.id.actionAddEvent)?.isVisible = currentUser.master
            } else {
            menu.findItem(R.id.actionSignOut)?.isVisible = false
            menu.findItem(R.id.actionAddEvent)?.isVisible = false
            menu.findItem(R.id.actionSignIn)?.isVisible = true
            menu.findItem(R.id.actionRegister)?.isVisible = true
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}