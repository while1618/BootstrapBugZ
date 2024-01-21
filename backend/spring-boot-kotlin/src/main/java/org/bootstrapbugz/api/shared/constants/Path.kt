package org.bootstrapbugz.api.shared.constants

object Path {
    private const val API_VERSION = "/v1"
    const val AUTH = "$API_VERSION/auth"
    const val PROFILE = "$API_VERSION/profile"
    const val USERS = "$API_VERSION/users"
    const val ADMIN_USERS = "$API_VERSION/admin/users"
}
