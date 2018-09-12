package au.com.pjwin.landmarkremark

import android.app.Application
import au.com.pjwin.commonlib.Common
import au.com.pjwin.landmarkremark.util.CommonConfig
import com.kumulos.android.Kumulos
import com.kumulos.android.KumulosConfig

open class LandmarkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initCommon()

        Kumulos.initialize(this,
                KumulosConfig.Builder(BuildConfig.KUMULOS_API_KEY, BuildConfig.KUMULOS_SECRET_KEY).build())
    }

    protected open fun initCommon() {
        Common.init(applicationContext, CommonConfig(), false)
    }
}