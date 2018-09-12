package au.com.pjwin.landmarkremark.util

import au.com.pjwin.commonlib.Common
import au.com.pjwin.landmarkremark.BuildConfig

class CommonConfig : Common.Config {

    override fun port() = BuildConfig.PORT

    override fun schema() = BuildConfig.SCHEMA

    override fun host() = BuildConfig.HOST

    override fun contextRoot() = BuildConfig.CONTEXT_ROOT

    override fun readTimeout() = BuildConfig.READ_TIMEOUT

    override fun connectionTimeout() = BuildConfig.CONNECTION_TIMEOUT

    override fun debug() = BuildConfig.DEBUG

    override fun credentialBase64() = BuildConfig.KUMULOS_REST_KEY
}