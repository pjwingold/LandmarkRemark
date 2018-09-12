package au.com.pjwin.landmarkremark

import org.robolectric.RobolectricTestRunner

/**
 * to clear any unwanted stuffs from manifest
 */
class AppTestRunner(testClass: Class<*>?) : RobolectricTestRunner(testClass) {
}