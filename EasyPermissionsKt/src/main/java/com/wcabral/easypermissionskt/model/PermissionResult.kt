package com.wcabral.easypermissionskt.model

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Permission result type that EasyPermissionKt can return.
 */
sealed class PermissionsResult {
    data class PermissionGranted(
        val permissions: List<PermissionGrantedInfo>
    ) : PermissionsResult()

    /**
     * For denied permissions, you can use the list of [permissions] to
     * know each one.
     */
    data class PermissionDenied(
        val permissions: List<PermissionDeniedInfo>
    ) : PermissionsResult() {
        fun shouldShowRationale() = permissions.any { it.shouldShowRationale }
    }
}

/**
 * Executes the [block] lambda when the [PermissionsResult] is
 * granted.
 */
@OptIn(ExperimentalContracts::class)
inline fun PermissionsResult.whenPermissionGranted(
    crossinline block: (permissions: List<PermissionGrantedInfo>) -> Unit
): PermissionsResult {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (this is PermissionsResult.PermissionGranted) {
        block(this.permissions)
    }
    return this
}

/**
 * Executes the [block] lambda when the [PermissionsResult] is
 * should show rationale to the user.
 */
@OptIn(ExperimentalContracts::class)
inline fun PermissionsResult.whenPermissionShouldShowRationale(
    crossinline block: (permissions: List<PermissionDeniedInfo>) -> Unit
): PermissionsResult {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (this is PermissionsResult.PermissionDenied && this.shouldShowRationale()) {
        block(this.permissions)
    }
    return this
}

/**
 * Executes the [block] lambda when the [PermissionsResult] is
 * denied permanently.
 */
@OptIn(ExperimentalContracts::class)
inline fun PermissionsResult.whenPermissionDeniedPermanently(
    crossinline block: (permissions: List<PermissionDeniedInfo>) -> Unit
): PermissionsResult {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (this is PermissionsResult.PermissionDenied && !this.shouldShowRationale()) {
        block(this.permissions)
    }
    return this
}
