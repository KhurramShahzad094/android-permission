package com.example.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.View.VISIBLE
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_permission.*

class PermissionActivity : AppCompatActivity() {

    var PERMISSION_REQUEST_CODE = 121

    // give any number of permissions in it
    var appPermissions =
        arrayOf("android.permission.CALL_PHONE", "android.permission.ACCESS_FINE_LOCATION")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        button.setOnClickListener {
            if (checkAndRequestPermission()) {
                init()
            }
        }
    }


    // do your thing here after allowing the permission
    private fun init() {
        imageView.visibility = VISIBLE
    }

    private fun checkAndRequestPermission(): Boolean {
        // check which permissions are granted
        var listPermissionNeeded = arrayListOf<String>()
        for (permission in appPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permission)
            }
        }

        // ask for non granted permission
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionNeeded.toArray(arrayOf()),
                PERMISSION_REQUEST_CODE
            )
            return false
        }

        // app has all permission now proceed ahead
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var permissionResult = HashMap<String, Int>()
            var deniedCount = 0
            // gather permission grant result
            for (i in grantResults.indices) {
                // add only permissions which are denied
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult[permissions[i]] = grantResults[i]
                    deniedCount++
                }
            }

            // check if all permissions are granted
            if (deniedCount == 0) {
                init()
            } else {
                // if one or more permission is denied
                var checkPermissionRationale = false
                for (entry in permissionResult.entries) {
                    var permissionName = entry.key
                    var permissionResult = entry.value

                    // permission is denied (this is the first time, when "never ask again" is not checked)
                    // so ask again explaining the usage of permission
                    // shouldshowRequestPermissionRationale will return true
                    checkPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissionName)
                }

                if (checkPermissionRationale) {
                    againAskForPermssionDialog()
                } else {
                    // permission is denied and never ask is checked
                    // shouldShowPermissionRationale will return false
                    showSettingsDialog()
                }
            }
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", this!!.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun showSettingsDialog() {
        var dialogBox = AlertDialog.Builder(
            ContextThemeWrapper(
                this,
                android.R.style.ThemeOverlay_Material_Dialog
            )
        )
        var inflater = layoutInflater
        var dialogView = inflater.inflate(R.layout.dialog_box_permission, null)
        dialogBox.setView(dialogView)

        var btn_yes: Button = dialogView.findViewById(R.id.btnYes)
        var btn_no: Button = dialogView.findViewById(R.id.btnNO)

        val alertDialog = dialogBox.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        alertDialog.setCancelable(false)

        btn_yes.setOnClickListener {
            alertDialog.dismiss()
            openSettings()
        }

        btn_no.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    private fun againAskForPermssionDialog() {
        var dialogBox = AlertDialog.Builder(
            ContextThemeWrapper(
                this,
                android.R.style.ThemeOverlay_Material_Dialog
            )
        )
        var inflater = layoutInflater
        var dialogView = inflater.inflate(R.layout.dialog_box_permission, null)
        dialogBox.setView(dialogView)

        var btn_yes: Button = dialogView.findViewById(R.id.btnYes)
        var btn_no: Button = dialogView.findViewById(R.id.btnNO)

        btn_yes.text = "Yes Grant"
        val alertDialog = dialogBox.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        alertDialog.setCancelable(false)

        btn_yes.setOnClickListener {
            alertDialog.dismiss()
            checkAndRequestPermission()
        }

        btn_no.setOnClickListener {
            alertDialog.dismiss()
        }

    }
}
