package au.com.pjwin.landmarkremark

import au.com.pjwin.commonlib.Common
import au.com.pjwin.landmarkremark.util.CommonConfig

class TestLandmarkApp : LandmarkApp() {

    override fun initCommon() {
        Common.init(applicationContext, CommonConfig(), true)
    }
}