package au.com.pjwin.landmarkremark.repo.firebase

import au.com.pjwin.commonlib.repo.firebase.FirebaseBaseRepository
import au.com.pjwin.landmarkremark.model.User
import au.com.pjwin.landmarkremark.repo.UserRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserFirebaseRepository : FirebaseBaseRepository(), UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = null
    private lateinit var googleCredential: AuthCredential

    //todo
    override fun login() {
        //rootReference.push()
    }

    override fun loginAnonymous(success: (User) -> Unit, failure: (Throwable) -> Unit) {
        //var id: String
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            val authUser = auth.currentUser
                            authUser?.let {
                                val user = User(it.uid, null, null)
                                success(user)
                            }

                        } else {
                            result.exception?.let {
                                failure(it)
                            }
                        }
                    }
        }
    }

}