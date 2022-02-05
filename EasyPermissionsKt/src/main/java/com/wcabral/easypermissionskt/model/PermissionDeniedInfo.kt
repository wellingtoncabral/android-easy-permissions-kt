package com.wcabral.easypermissionskt.model

/**
 * Model that represent permission denied info.
 * @property permission name of permission denied.
 * @property shouldShowRationale If true, the app should show a rationale UI.
 * Otherwise the permission is denied permanently.
 */
data class PermissionDeniedInfo(val permission: String, val shouldShowRationale: Boolean)

/**
 * Checks if any permissions in the list should be rationalized for the user.
 */
fun List<PermissionDeniedInfo>.shouldShowRationale() = this.any { it.shouldShowRationale }
