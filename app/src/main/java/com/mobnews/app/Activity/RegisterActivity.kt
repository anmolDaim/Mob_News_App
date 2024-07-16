package com.mobnews.app.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.preference.PreferenceManager
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobnews.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

class RegisterActivity : AppCompatActivity(), InstallReferrerStateListener {

    lateinit var registerlogin:AppCompatButton
    lateinit var loginNow:TextView
    lateinit var termsBtn:TextView
    lateinit var inputEmail:EditText
    lateinit var inputPassword:EditText
    lateinit var inputConfirmPassword:EditText
    lateinit var checkBox:CheckBox
    var emailPatern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+"
    var progressDialog: ProgressDialog? = null
    var nAuth: FirebaseAuth? = null
    var nUser: FirebaseUser? = null

    private lateinit var mReferrerClient: InstallReferrerClient
    private lateinit var myGid: String

    //user's utm
    private var utmSource: String = ""
    private var utmMedium: String = ""
    private var utmTerm: String = ""
    private var utmContent: String = ""
    private var utmCompaign: String = ""

    private var referrerLink: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerlogin=findViewById(R.id.registerlogin)
        loginNow=findViewById(R.id.loginNow)
        termsBtn=findViewById(R.id.termsBtn)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword = findViewById(R.id.confirmInputPassword)
        checkBox=findViewById(R.id.checkBox)
        progressDialog = ProgressDialog(this)

        nAuth = FirebaseAuth.getInstance()
        nUser = nAuth!!.currentUser

        registerlogin.setOnClickListener {
//            val intent =Intent(this,LoginActivity::class.java)
//            startActivity(intent)
            PerForAuth()
        }
        loginNow.setOnClickListener {
            val intent =Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        termsBtn.setOnClickListener {
            val intent =Intent(this, TermsActivity::class.java)
            startActivity(intent)
        }

        setUtm()
        getAdvertisingId()
    }

    private fun PerForAuth() {
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()
        val confirmPassword = inputConfirmPassword.text.toString()
        val isChecked = checkBox.isChecked

        // Password pattern to enforce at least one alphabet, one digit, and one special character
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,}$".toRegex()

        if (!email.matches(emailPatern.toRegex())) {
            inputEmail.error = "Enter Correct Email"
        } else if (!password.matches(passwordPattern)) {
            inputPassword.error = "Password must contain at least one alphabet, one digit, and one special character and be at least 6 characters long"
        } else if (password != confirmPassword) {
            inputConfirmPassword.error = "Passwords don't match"
        } else if (!isChecked) {
            // Checkbox not checked
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog!!.setMessage("Please wait while registering...")
            progressDialog!!.setTitle("Registration")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            nAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val urlString = "https://performxcel.com/record?vendor=MGApps&app=MobNews&clickid=$utmMedium&adv_sub=$referrerLink"
                    val url = URL(urlString)

                    // Perform the network request on a background thread
                    Thread {
                        try {
                            val connection = url.openConnection() as HttpURLConnection
                            connection.requestMethod = "POST"
                            connection.connect()

                            val responseCode = connection.responseCode
                            runOnUiThread {
                                progressDialog!!.dismiss()
                                if (responseCode == 200) {
                                    // Success
                                    setLoggedIn(email)
                                    sendUserToNextActivity()
                                    Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Handle error
                                    Toast.makeText(this@RegisterActivity, "Failed to record UTM: $responseCode", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                progressDialog!!.dismiss()
                                Toast.makeText(this@RegisterActivity, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.start()
                } else {
                    progressDialog!!.dismiss()
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setLoggedIn(email:String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("email", email)
        editor.apply()
        Toast.makeText(this, "register true", Toast.LENGTH_SHORT).show()
    }
    private fun sendUserToNextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    private fun setUtm() {
        mReferrerClient = InstallReferrerClient.newBuilder(this).build()
        mReferrerClient.startConnection(this)
    }

    private fun getAdvertisingId() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this@RegisterActivity)
                val adId = adInfo?.id ?: "unknown"
                Log.d("Advertising ID", adId)
                myGid = adId
            } catch (e: Exception) {
                Log.e("Advertising ID", "Error fetching Advertising ID", e)
            }
        }
    }

    override fun onInstallReferrerSetupFinished(p0: Int) {
        when (p0) {
            InstallReferrerClient.InstallReferrerResponse.OK ->                 // Connection established
                try {
                    getReferralUser()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {}
            InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {}
            InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR -> {}
            InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED -> {}
        }
    }

    override fun onInstallReferrerServiceDisconnected() {
        TODO("Not yet implemented")
    }

    private fun getReferralUser() {
        try {
            val response: ReferrerDetails = mReferrerClient.installReferrer
            val referrerData = response.installReferrer
            referrerLink = referrerData.toString()
            Log.d("LINK_", referrerLink)
            Log.d("TAG12122", "Install referrer: $referrerData")
            Log.d("TAG1212", "Install referrer: $referrerLink")

            // Parse referrer data for UTM parameters
            val values = HashMap<String, String>()
            referrerData?.split("&")?.forEach { referrerValue ->
                val keyValue = referrerValue.split("=").toTypedArray()
                if (keyValue.size == 2) {
                    values[URLDecoder.decode(keyValue[0], "UTF-8")] =
                        URLDecoder.decode(keyValue[1], "UTF-8")
                }
            }

            utmSource = values["utm_source"].toString()
            utmMedium = values["utm_medium"].toString()
            utmTerm = values["utm_term"].toString()
            utmCompaign = values["utm_campaign"].toString()
            utmContent = values["utm_content"].toString()

            Log.d(
                "API_UTM",
                "utmSource: $utmSource, utmMedium: $utmMedium, utmTerm: $utmTerm, utmCompaign: $utmCompaign, utmContent: $utmContent"
            )
        } catch (e: Exception) {
            Log.e("TAG1", "Failed to get install referrer: ${e.message}")
        }
    }
}