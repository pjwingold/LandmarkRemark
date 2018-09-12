package au.com.pjwin.landmarkremark

import android.support.annotation.CallSuper
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AppTestRunner::class)
@Config(constants = BuildConfig::class, application = TestLandmarkApp::class)
open class BaseTest {

    @CallSuper
    @Before
    open fun setup() {
        MockitoAnnotations.initMocks(this)
    }
}