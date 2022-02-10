package com.wcabral.easypermissionskt

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wcabral.easypermissionskt.model.*
import com.wcabral.easypermissionskt_sample.R
import com.wcabral.easypermissionskt_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val easyPermission = registerForPermissionsResult {
        whenPermissionGranted { permissions ->
            showPermissionsGranted(permissions)
        }

        whenPermissionShouldShowRationale { permissions ->
            showPermissionShouldShowRationale(permissions)
        }

        whenPermissionDeniedPermanently { permissions ->
            showPermissionDenied(permissions)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRequestPermission.setOnClickListener {
            easyPermission.requestPermissions(
                Manifest.permission.CAMERA
            )
        }

        binding.btnRequestPermissions.setOnClickListener {
            easyPermission.requestPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }

        binding.btnHasPermissionFor.setOnClickListener {
            easyPermission.hasPermissionFor(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ).also {
                updateUi {
                    "hasPermissionFor\n${Manifest.permission.CAMERA}: $it"
                }
            }
        }

        binding.btnShouldShowRequestPermissionRationale.setOnClickListener {
            easyPermission.shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ).also {
                updateUi {
                    "shouldShowRequestPermissionRationale\n${Manifest.permission.CAMERA}: $it"
                }
            }
        }

        binding.btnExplainWhy.setOnClickListener {
            easyPermission
                .explainWhy(
                    WithDialog(
                        title = getString(R.string.permission_dialog_title),
                        description = getString(R.string.permission_dialog_description),
                        positiveButtonText = getString(R.string.permission_dialog_positive_button),
                        negativeButtonText = getString(R.string.permission_dialog_negative_button)
                    )
                )
                .requestPermissions(Manifest.permission.READ_CONTACTS)
        }

    }

    private fun showPermissionsGranted(permissions: List<PermissionGrantedInfo>) {
        updateUi {
            permissions.joinToString(
                prefix = "Permissions Granted \n",
                separator = "\n"
            ) { it.permission }
        }
    }

    private fun showPermissionShouldShowRationale(permissions: List<PermissionDeniedInfo>) {
        updateUi {
            permissions.joinToString(
                prefix = "Permissions should show rationale \n",
                separator = "\n"
            ) { it.permission }
        }
    }

    private fun showPermissionDenied(permissions: List<PermissionDeniedInfo>) {
        updateUi {
            permissions.joinToString(
                prefix = "Permissions Denied Permanently \n",
                separator = "\n"
            ) { it.permission }
        }
    }

    private fun updateUi(block: () -> String) {
        binding.tvResult.text = block()
    }

}