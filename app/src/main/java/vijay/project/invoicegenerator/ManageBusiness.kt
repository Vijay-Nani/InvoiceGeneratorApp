package vijay.project.invoicegenerator

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class ManageBusiness : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BusinessSettingsScreen() }
    }
}

object BusinessPrefs {

    private const val PREF_NAME = "invoice_settings"
    private const val BUSINESS_NAME = "business_name"
    private const val TAX_PERCENT = "tax_percentage"
    private const val DISCOUNT_PERCENT = "discount_percentage"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveBusinessDetails(context: Context, name: String, tax: String, discount: String) {
        prefs(context).edit()
            .putString(BUSINESS_NAME, name)
            .putString(TAX_PERCENT, tax)
            .putString(DISCOUNT_PERCENT, discount)
            .apply()
    }

    fun getBusinessName(context: Context): String =
        prefs(context).getString(BUSINESS_NAME, "") ?: ""

    fun getTaxPercentage(context: Context): String =
        prefs(context).getString(TAX_PERCENT, "18") ?: "18"

    fun getDiscountPercentage(context: Context): String =
        prefs(context).getString(DISCOUNT_PERCENT, "30") ?: "30"
}

// ------------------------------------------------------------
// SCREEN UI
// ------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessSettingsScreen() {

    val context = LocalContext.current

    // Load saved values
    var businessName by remember { mutableStateOf(BusinessPrefs.getBusinessName(context)) }
    var taxPercentage by remember { mutableStateOf(BusinessPrefs.getTaxPercentage(context)) }
    var discountPercentage by remember { mutableStateOf(BusinessPrefs.getDiscountPercentage(context)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Manage Business",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Business Name
            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Tax %
            OutlinedTextField(
                value = taxPercentage,
                onValueChange = { taxPercentage = it },
                label = { Text("Tax Percentage") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Discount %
            OutlinedTextField(
                value = discountPercentage,
                onValueChange = { discountPercentage = it },
                label = { Text("Discount Percentage") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {

                    if (businessName.isEmpty()) {
                        Toast.makeText(context, "Please enter business name", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    if (taxPercentage.isEmpty()) {
                        Toast.makeText(context, "Please enter tax percentage", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    if (discountPercentage.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please enter discount percentage",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    BusinessPrefs.saveBusinessDetails(
                        context,
                        businessName,
                        taxPercentage,
                        discountPercentage
                    )

                    Toast.makeText(context, "Details updated", Toast.LENGTH_SHORT).show()

                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
