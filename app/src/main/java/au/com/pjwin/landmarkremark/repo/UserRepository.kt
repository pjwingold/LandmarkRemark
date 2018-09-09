package au.com.pjwin.landmarkremark.repo

import au.com.pjwin.landmarkremark.model.User

interface UserRepository {

    fun login()

    fun loginAnonymous(success: (User) -> Unit, failure: (Throwable) -> Unit) {
    }
}