package com.mobnews.app.Fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobnews.app.Activity.LoginActivity
import com.mobnews.app.Activity.PrivacyActivity
import com.mobnews.app.Activity.RateUsActivity
import com.mobnews.app.Activity.RegisterActivity
import com.mobnews.app.Activity.TermsActivity
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.R


class ProfileFragment : Fragment() {
    lateinit var generalCnstraintLayout:ConstraintLayout
    lateinit var darkModeConstraintLayout:ConstraintLayout
    lateinit var cacheConstraintLayout:ConstraintLayout
    lateinit var clearCacheConstraintLayout:ConstraintLayout
    lateinit var shareFriendsConstraintLayout:ConstraintLayout
    lateinit var rateUsConstraintLayout:ConstraintLayout
    lateinit var aboutConstraintLayout:ConstraintLayout
    lateinit var generalImage:ImageView
    lateinit var cacheImage:ImageView
    lateinit var aboutImage:ImageView
    lateinit var toggleDarkModeButton:AppCompatButton
    lateinit var termsConstraint:ConstraintLayout
    lateinit var termsAndConditionsConstraint:ConstraintLayout
    lateinit var privacyPolicyConstraint:ConstraintLayout
    lateinit var termsImage:ImageView
    lateinit var fetchedemail:TextView

    private var isExpanded = false
    private var ClearExpanded = false
    private var aboutExpanded = false

    private var article: Data1.Article? = null

    fun setArticle(article: Data1.Article) {
        this.article = article
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        generalCnstraintLayout=view.findViewById(R.id.generalCnstraintLayout)
        darkModeConstraintLayout=view.findViewById(R.id.darkModeConstraintLayout)
        cacheConstraintLayout=view.findViewById(R.id.cacheConstraintLayout)
        clearCacheConstraintLayout=view.findViewById(R.id.clearCacheConstraintLayout)
        shareFriendsConstraintLayout=view.findViewById(R.id.shareFriendsConstraintLayout)
        rateUsConstraintLayout=view.findViewById(R.id.rateUsConstraintLayout)
        aboutConstraintLayout=view.findViewById(R.id.aboutConstraintLayout)
        toggleDarkModeButton=view.findViewById(R.id.toggleDarkModeButton)
        cacheImage=view.findViewById(R.id.cacheImage)
        generalImage=view.findViewById(R.id.generalImage)
        aboutImage=view.findViewById(R.id.aboutImage)
        termsConstraint=view.findViewById(R.id.termsConstraint)
        privacyPolicyConstraint=view.findViewById(R.id.privacyPolicyConstraint)
        termsAndConditionsConstraint=view.findViewById(R.id.termsAndConditionsConstraint)
        termsImage=view.findViewById(R.id.termsImage)
        fetchedemail=view.findViewById(R.id.fetchedEmail)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val email = preferences.getString("email", "") // Fetch the email
        fetchedemail.text = email // Set the email to the TextView

        generalCnstraintLayout.setOnClickListener {
            if (isExpanded) {
                // Wrap up
                darkModeConstraintLayout.visibility = View.GONE

                generalImage.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            } else {
                // Expand
                darkModeConstraintLayout.visibility = View.VISIBLE
                generalImage.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            }
            isExpanded = !isExpanded
        }
        cacheConstraintLayout.setOnClickListener {
            if (ClearExpanded) {
                // Wrap up
                clearCacheConstraintLayout.visibility = View.GONE

                cacheImage.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            } else {
                // Expand
                clearCacheConstraintLayout.visibility = View.VISIBLE
                cacheImage.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            }
            ClearExpanded = !ClearExpanded
        }
        clearCacheConstraintLayout.setOnClickListener {
            // Use an Intent to open the specific application settings page
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Unable to open app settings.", Toast.LENGTH_SHORT).show()
            }
        }
        aboutConstraintLayout.setOnClickListener {
            if (aboutExpanded) {
                // Wrap up
                rateUsConstraintLayout.visibility = View.GONE
                shareFriendsConstraintLayout.visibility = View.GONE

                aboutImage.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            } else {
                // Expand
                rateUsConstraintLayout.visibility = View.VISIBLE
                shareFriendsConstraintLayout.visibility = View.VISIBLE
                aboutImage.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            }
            aboutExpanded = !aboutExpanded
        }

        termsConstraint.setOnClickListener {
            // Toggle the visibility of termsAndConditionsConstraint
            if (isExpanded) {
                termsAndConditionsConstraint.visibility = View.GONE
                privacyPolicyConstraint.visibility=View.GONE
                termsImage.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            } else {
                termsAndConditionsConstraint.visibility = View.VISIBLE
                privacyPolicyConstraint.visibility=View.VISIBLE
                termsImage.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            }
            isExpanded = !isExpanded
        }

        termsAndConditionsConstraint.setOnClickListener {
            val intent=Intent(requireContext(), TermsActivity()::class.java)
            startActivity(intent)
        }
        privacyPolicyConstraint.setOnClickListener {
            val intent=Intent(requireContext(), PrivacyActivity()::class.java)
            startActivity(intent)
        }

        rateUsConstraintLayout.setOnClickListener {
            val intent=Intent(requireContext(), RateUsActivity::class.java)
            startActivity(intent)
        }
        shareFriendsConstraintLayout.setOnClickListener {
            shareContent()
        }

        val currentMode = AppCompatDelegate.getDefaultNightMode()
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            // Dark mode is enabled
            toggleDarkModeButton.text = "Light"
        } else {
            // Light mode is enabled
            toggleDarkModeButton.text = "Dark"
        }

        // Set click listener for the button
        toggleDarkModeButton.setOnClickListener {
            toggleDarkMode()
        }

//        textSizeConstraintLayout.setOnClickListener {
//            showTextSizeDialog()
//        }
    }
//    private fun showTextSizeDialog() {
//        val dialogBuilder = Dialog(requireContext(), R.style.CustomAlertDialogStyle)
//        dialogBuilder.setContentView(R.layout.text_size_layout)
//
//        val radioGroup = dialogBuilder.findViewById<EditText>(R.id.radioGroup)
//        val radioButtonLarge = dialogBuilder.findViewById<EditText>(R.id.radioButtonLarge)
//        val radioButtonMedium = dialogBuilder.findViewById<EditText>(R.id.radioButtonMedium)
//        val radioButtonSmall = dialogBuilder.findViewById<AppCompatButton>(R.id.radioButtonSmall)
//        dialogBuilder.setTitle("Select Text Size")
//
////        val checkedId = radioGroup.checkedRadioButtonId
////        var textSize =13
////        when (checkedId) {
////            radioButtonSmall -> {
////                textSize = 12 // Small text size
////            }
////
////            radioButtonMedium -> {
////                textSize = 15 // Medium text size
////            }
////
////            radioButtonLarge -> {
////                textSize = 18 // Large text size
////            }
////        }
//
//        //article?.content = textSize.toString()
//
//        // Close the dialog
//        dialogBuilder.show()
//    }

    private fun toggleDarkMode() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            // Dark mode is currently enabled, disable it
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toggleDarkModeButton.text = "Dark"
        } else {
            // Dark mode is currently disabled, enable it
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toggleDarkModeButton.text = "Light"
        }
    }
    private fun shareContent() {

        val urlToShare = "https://play.google.com/store/apps/details?id=com.mobnews.app"
        // Define the text you want to share
        val shareText = "Check out this amazing app! $urlToShare"

        // Create an ACTION_SEND intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        // Create a chooser intent to let the user select the app to share the content
        val chooserIntent = Intent.createChooser(shareIntent, "Share with...")

        // Try to launch the chooser
        try {
            startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No application available to share content", Toast.LENGTH_SHORT).show()
        }
    }
}