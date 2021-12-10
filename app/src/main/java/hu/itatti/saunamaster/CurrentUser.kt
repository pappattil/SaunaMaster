package hu.itatti.saunamaster

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.itatti.saunamaster.data.Users

object CurrentUser {

    var currentUser=Users("","","", guest = false, master = false)
    fun initCurrentUserQuery() {

        val docRef = FirebaseFirestore.getInstance().collection(RegistrationFragment.COLLECTION_USER)
        docRef.get().addOnSuccessListener { documents ->
           for(document in documents) {
               if(document.get("uid") == FirebaseAuth.getInstance().currentUser?.uid) {
                   currentUser.name = document.get("name") as String
                   currentUser.master = document.get("master") as Boolean
               }
           }
        }.addOnFailureListener {

        }

    }
}
