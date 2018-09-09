package au.com.pjwin.landmarkremark

import android.app.Application
import au.com.pjwin.commonlib.Common
import au.com.pjwin.landmarkremark.util.CommonConfig

class LandmarkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Common.init(applicationContext, CommonConfig(), false)
    }
}