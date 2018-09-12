package au.com.pjwin.landmarkremark.util

import au.com.pjwin.commonlib.util.Pref

enum class PrefKey() : Pref.Key {
    USER_UID,
    HAS_LOCATION_PERM;

    override var value: String = name

    constructor(value: String) {
        this.value = value
    }
}