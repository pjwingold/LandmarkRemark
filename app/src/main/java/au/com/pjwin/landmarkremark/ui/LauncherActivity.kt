package au.com.pjwin.landmarkremark.ui

import android.databinding.ViewDataBinding
import android.os.Bundle
import au.com.pjwin.commonlib.ui.BaseActivity
import au.com.pjwin.landmarkremark.model.User
import au.com.pjwin.landmarkremark.viewmodel.AuthViewModel

class LauncherActivity : BaseActivity<User, AuthViewModel, ViewDataBinding>() {

    override fun layoutId() = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}