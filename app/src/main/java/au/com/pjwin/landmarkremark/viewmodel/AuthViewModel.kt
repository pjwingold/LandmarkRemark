package au.com.pjwin.landmarkremark.viewmodel

import au.com.pjwin.commonlib.util.Pref
import au.com.pjwin.commonlib.util.get
import au.com.pjwin.commonlib.viewmodel.DataViewModel
import au.com.pjwin.landmarkremark.model.User
import au.com.pjwin.landmarkremark.repo.UserRepository
import au.com.pjwin.landmarkremark.repo.firebase.UserFirebaseRepository
import au.com.pjwin.landmarkremark.util.PrefKey

//open so that it can be tested by Mockito
open class AuthViewModel : DataViewModel<User>() {

    //private var liveDataAuth: LiveData<User>? = null
    //todo set up DI & factory
    private val userRepo: UserRepository by lazy {
        UserFirebaseRepository()
    }

    //todo login with google auth
    fun login() {
        //liveData
        //userRepo.login()
    }

    fun loginAnonymous() {
        val uId: String? = Pref.SHARED_PREF[PrefKey.USER_UID.name]

        if (uId == null) {
            userRepo.loginAnonymous(
                    { user ->
                        //todo refactor so don't have to callback() every time
                        callback(this::onData, user)
                        Pref[PrefKey.USER_UID.name] = user.uId
                    },
                    { throwable -> callback(this::onError, throwable) }
            )

        } else {
            onData(User(uId, null, null))
        }
    }
}