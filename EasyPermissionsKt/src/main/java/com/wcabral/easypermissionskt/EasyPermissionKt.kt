package com.wcabral.easypermissionskt

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.wcabral.easypermissionskt.model.PermissionDeniedInfo
import com.wcabral.easypermissionskt.model.PermissionsResult

private const val PERMISSION_KEY = "permission_manager_key"

/**
 * Extension function to register permission result from [FragmentActivity]
 * @param callback of permission result
 */
fun FragmentActivity.registerForPermissionsResult(
    callback: PermissionsResult.() -> Unit
): EasyPermissionKt = EasyPermissionKtImpl(this, callback)

/**
 * Extension function to register permission result from [Fragment]
 * @param callback of permission result
 */
fun Fragment.registerForPermissionsResult(
    callback: PermissionsResult.() -> Unit
): EasyPermissionKt = EasyPermissionKtImpl(this.requireActivity(), callback)

/**
 * Contract to PermissionManager API
 */
interface EasyPermissionKt {
    fun requestPermissions(vararg permissions: String)
    fun hasPermissionFor(vararg permissions: String): Boolean
    fun shouldShowRequestPermissionRationale(permission: String): Boolean
    fun explainWhy(explainWhyUI: ExplainWhyUI): EasyPermissionKt
}

/**
 * A class which wraps boilerplate code of runtime permission and allows you
 * to request permissions.
 *
 * How to use:
 * ```
 * private val permissionsManager = registerForPermissionsResult {
 *     whenPermissionGranted {
 *         // All permissions granted
 *         TODO()
 *     }
 *     whenPermissionShouldShowRationale { permissionsList ->
 *         // Permissions denied but not permanently.
 *         // The app should show a rationale to the user in
 *         // a UI element.
 *         // @see [https://developer.android.com/training/permissions/requesting#explain]
 *         TODO()
 *     }
 *     whenPermissionDeniedPermanently {
 *         // Permission denied permanently
 *         TODO()
 *     }
 * }
 *
 * // Requesting permission or permissions
 * permissionsManager.requestPermissions(
 *      Manifest.permission.CAMERA,
 *      Manifest.permission.WRITE_EXTERNAL_STORAGE
 * )
 *
 * // Requesting permission with an explain why dialog
 * permissionsManager
 *      .explainWhy(
 *          WithDialog(
 *              title = "Permission Request",
 *              description = "To easily connect with family and friends, allow the app access to your contacts",
 *              positiveButtonText = "Continue",
 *              negativeButtonText = "Not now"
 *          )
 *      )
 *      .requestPermissions(Manifest.permission.READ_CONTACTS)
 *
 * // Asking if already have permissions for
 * permissionsManager.hasPermissionFor(
 *      Manifest.permission.CAMERA,
 *      Manifest.permission.WRITE_EXTERNAL_STORAGE
 * )
 *
 * // Asking if the permission should show rationale
 * if (permissionsManager.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
 *      TODO()
 * )
 *```
 *
 * @see EasyPermissionKt
 */
internal class EasyPermissionKtImpl(
    private val activity: FragmentActivity,
    private val callback: (PermissionsResult) -> Unit
) : DefaultLifecycleObserver, EasyPermissionKt {

    /**
     * Launcher that will launch
     * [ActivityResultContracts.RequestMultiplePermissions] contract
     */
    private var resultLauncher: ActivityResultLauncher<Array<String>>? = null

    /**
     * Reference to an [ExplainWhyUI]. In case the PermissionManager
     * have ExplainWhyUI configured before requesting permission.
     */
    private var explainWhyUI: ExplainWhyUI? = null

    init {
        // Add own PermissionManage implementing in the lifecycle scope.
        // This make sure that this component will be lifecycle aware.
        activity.lifecycle.addObserver(this)
    }

    /**
     * In the OnCreate lifecycle method, register the permission launcher
     * and wait for a result.
     */
    override fun onCreate(owner: LifecycleOwner) {
        resultLauncher = activity.activityResultRegistry.register(
            PERMISSION_KEY,
            activity,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            // Filter all permissions denied and maps them to a list of
            // [PermissionDeniedInfo]. If [shouldShowRequestPermissionRationale]
            // returns true, it means permission denied but not permanently.
            // @see [https://developer.android.com/training/permissions/requesting#explain]
            // Otherwise, permission permanently denied. Tell user the app won't
            // work as expected.
            val permissionsDenied = result.entries
                .filter { !it.value }
                .map { permissionMap ->
                    PermissionDeniedInfo(
                        permissionMap.key,
                        shouldShowRequestPermissionRationale(permissionMap.key)
                    )
                }

            if (permissionsDenied.isNullOrEmpty()) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                callback(PermissionsResult.PermissionGranted)
            } else {
                // Permissions denied permanently. Explain to the user that the
                // feature is unavailable because the features requires a
                // permission that the user has denied. At the same time, respect
                // the user's decision. Don't link to system settings in an effort
                // to convince the user to change their decision.
                callback(PermissionsResult.PermissionDenied(permissionsDenied))
            }
        }
    }

    /**
     * Request permission for one one ou many arguments
     *
     * @param permissions one ou many permissions like
     * Manifest.permission.CAMERA
     */
    override fun requestPermissions(vararg permissions: String) {
        if (permissions.isNotEmpty()) {
            // Lambda function to invoke launch permissions logic
            val launchPermissions: () -> Unit = {
                resultLauncher?.launch(permissions.asList().toTypedArray())
            }

            explainWhyUI?.let { explainWhy ->
                // Any permissions not yet granted?
                if (permissions.any { permission -> !hasPermissionFor(permission) }) {
                    // Show [ExplainWhyUI] interface to the user
                    explainWhy.show(activity) { continuation ->
                        // If user accepted, launch the permissions
                        if (continuation) launchPermissions()
                    }
                } else {
                    // Launch the permissions because some permissions
                    // have not yet been granted
                    launchPermissions()
                }
                this.explainWhyUI = null
            } ?: launchPermissions()
        }
    }

    /**
     * Checks if permission has already been granted.
     * @param permission that will be checked.
     */
    private fun hasPermissionFor(permission: String) = ContextCompat.checkSelfPermission(
        activity, permission
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * Checks if permission has already been granted.
     * @param permissions that will be checked.
     */
    override fun hasPermissionFor(
        vararg permissions: String
    ) = permissions.all { hasPermissionFor(it) }

    /**
     * Checks if permission should show rationale UI to the user.
     * @param permission that will be checked.
     */
    override fun shouldShowRequestPermissionRationale(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

    /**
     * Create a [ExplainWhyUI] dialog before requesting permissions
     * to the user.
     * @param explainWhyUI interface to build a custom dialog.
     * @return The same instance of [EasyPermissionKt] (build pattern)
     */
    override fun explainWhy(explainWhyUI: ExplainWhyUI): EasyPermissionKt {
        this.explainWhyUI = explainWhyUI
        return this
    }
}
