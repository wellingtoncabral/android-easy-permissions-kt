package com.wcabral.easypermissionskt

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

/**
 * Interface to create UI explaining why the user should allow the permissions
 */
fun interface ExplainWhyUI {
    /**
     * Shows the UI and sets whether to continue or not
     * @param activity a reference to FragmentActivity
     * @param continuation whether to continue or not
     */
    fun show(activity: FragmentActivity, continuation: (Boolean) -> Unit)
}

/**
 * Creates an abstraction of [ExplainWhyUI] that shows a [AlertDialog]
 */
class WithDialog(
    private val title: String,
    private val description: String,
    private val positiveButtonText: String,
    private val negativeButtonText: String,
) : ExplainWhyUI {

    override fun show(activity: FragmentActivity, continuation: (Boolean) -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton(positiveButtonText) { _, _ ->
                continuation(true)
            }
            .setNegativeButton(negativeButtonText) { _, _ ->
                continuation(false)
            }
            .setOnCancelListener {
                continuation(false)
            }
            .show()
    }
}
