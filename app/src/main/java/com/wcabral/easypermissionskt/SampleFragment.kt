package com.wcabral.easypermissionskt

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wcabral.easypermissionskt.model.*
import com.wcabral.easypermissionskt_sample.R
import com.wcabral.easypermissionskt_sample.databinding.FragmentSampleBinding

class SampleFragment : Fragment() {

    private var binding: FragmentSampleBinding? = null
    private lateinit var easyPermission: EasyPermissionKt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        easyPermission = registerForPermissionsResult {
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSampleBinding.inflate(layoutInflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnRequestPermission?.setOnClickListener {
            easyPermission.requestPermissions(
                Manifest.permission.CAMERA
            )
        }

        binding?.btnRequestPermissions?.setOnClickListener {
            easyPermission.requestPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }

        binding?.btnHasPermissionFor?.setOnClickListener {
            easyPermission.hasPermissionFor(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ).also {
                updateUi {
                    "hasPermissionFor\n${Manifest.permission.CAMERA}: $it"
                }
            }
        }

        binding?.btnShouldShowRequestPermissionRationale?.setOnClickListener {
            easyPermission.shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ).also {
                updateUi {
                    "shouldShowRequestPermissionRationale\n${Manifest.permission.CAMERA}: $it"
                }
            }
        }

        binding?.btnExplainWhy?.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
        binding?.tvResult?.text = block()
    }


}