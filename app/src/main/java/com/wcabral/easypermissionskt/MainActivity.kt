package com.wcabral.easypermissionskt

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.wcabral.easypermissionskt.model.whenPermissionDeniedPermanently
import com.wcabral.easypermissionskt.model.whenPermissionGranted
import com.wcabral.easypermissionskt.model.whenPermissionShouldShowRationale
import com.wcabral.easypermissionskt_sample.R
import com.wcabral.easypermissionskt_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val easyPermission = registerForPermissionsResult {
        whenPermissionGranted {
            Toast.makeText(this@MainActivity, "Granted", Toast.LENGTH_SHORT).show()
        }

        whenPermissionShouldShowRationale {
            Toast.makeText(this@MainActivity, "PermissionShouldShowRationale", Toast.LENGTH_SHORT).show()
        }

        whenPermissionDeniedPermanently {
            Toast.makeText(this@MainActivity, "Denied", Toast.LENGTH_SHORT).show()
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
            val result = easyPermission.hasPermissionFor(Manifest.permission.CAMERA)
            Toast.makeText(
                this,
                "hasPermissionFor ${Manifest.permission.CAMERA}: $result",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnShouldShowRequestPermissionRationale.setOnClickListener {
            val result = easyPermission.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            Toast.makeText(
                this,
                "shouldShowRequestPermissionRationale ${Manifest.permission.CAMERA}: $result",
                Toast.LENGTH_SHORT
            ).show()
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
}