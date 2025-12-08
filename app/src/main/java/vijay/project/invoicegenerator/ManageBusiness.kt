package vijay.project.invoicegenerator

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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

// ------------------------------------------------------------
// ACTIVITY
// ------------------------------------------------------------
class ManageBusiness : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BusinessSettingsScreen() }
    }
}

// ------------------------------------------------------------
// SHARED PREFS
// ------------------------------------------------------------
object BusinessPrefs {

    private const val PREF_NAME = "invoice_settings"

    private const val BUSINESS_NAME = "business_name"
    private const val BUSINESS_TYPE = "business_type"
    private const val BUSINESS_ADDRESS = "business_address"
    private const val CITY = "city"
    private const val STATE = "state"
    private const val PINCODE = "pincode"

    private const val TAX_PERCENT = "tax_percentage"
    private const val DISCOUNT_PERCENT = "discount_percentage"

    private const val GST_NUMBER = "gst_number"
    private const val PAN_NUMBER = "pan_number"
    private const val BILLING_CURRENCY = "billing_currency"
    private const val INVOICE_PREFIX = "invoice_prefix"
    private const val WEBSITE_URL = "website_url"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveDetails(context: Context, map: Map<String, String>) {
        prefs(context).edit().apply {
            map.forEach { (key, value) ->
                putString(key, value)
            }
        }.apply()
    }

    fun get(context: Context, key: String, default: String = ""): String {
        return prefs(context).getString(key, default) ?: default
    }
}

// ------------------------------------------------------------
// DROPDOWN COMPONENT
// ------------------------------------------------------------
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    "",
                    Modifier.clickable { expanded = true })
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onValueChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------
// MAIN SCREEN UI
// ------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessSettingsScreen() {

    val context = LocalContext.current

    // ---------- Load Saved Values ----------
    var businessName by remember { mutableStateOf(BusinessPrefs.get(context, "business_name")) }
    var businessType by remember {
        mutableStateOf(
            BusinessPrefs.get(
                context,
                "business_type",
                "Freelancer"
            )
        )
    }
    var businessAddress by remember {
        mutableStateOf(
            BusinessPrefs.get(
                context,
                "business_address"
            )
        )
    }
    var city by remember { mutableStateOf(BusinessPrefs.get(context, "city")) }
    var state by remember { mutableStateOf(BusinessPrefs.get(context, "state")) }
    var pincode by remember { mutableStateOf(BusinessPrefs.get(context, "pincode")) }

    var taxPercentage by remember {
        mutableStateOf(
            BusinessPrefs.get(
                context,
                "tax_percentage",
                "18"
            )
        )
    }
    var discountPercentage by remember {
        mutableStateOf(
            BusinessPrefs.get(
                context,
                "discount_percentage",
                "30"
            )
        )
    }

    var gstNumber by remember { mutableStateOf(BusinessPrefs.get(context, "gst_number")) }
    var panNumber by remember { mutableStateOf(BusinessPrefs.get(context, "pan_number")) }

    var billingCurrency by remember {
        mutableStateOf(
            BusinessPrefs.get(
                context,
                "billing_currency",
                "INR"
            )
        )
    }
    var invoicePrefix by remember { mutableStateOf(BusinessPrefs.get(context, "invoice_prefix")) }
    var websiteUrl by remember { mutableStateOf(BusinessPrefs.get(context, "website_url")) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Manage Business", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------------- BUSINESS DETAILS ----------------
            Text("Business Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business / Company Name") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Business Type",
                options = listOf("Freelancer", "Company", "Shop", "Service Provider"),
                selectedValue = businessType,
                onValueChange = { businessType = it }
            )

            OutlinedTextField(
                value = businessAddress,
                onValueChange = { businessAddress = it },
                label = { Text("Business Address") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
                label = { Text("Pincode") },
                modifier = Modifier.fillMaxWidth()
            )

            // ---------------- TAX & BILLING ----------------
            Text("Tax & Billing Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            OutlinedTextField(
                value = gstNumber,
                onValueChange = { gstNumber = it },
                label = { Text("GST Number (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = panNumber,
                onValueChange = { panNumber = it },
                label = { Text("PAN Number (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Billing Currency",
                options = listOf("INR", "USD", "GBP", "EUR"),
                selectedValue = billingCurrency,
                onValueChange = { billingCurrency = it }
            )

            OutlinedTextField(
                value = invoicePrefix,
                onValueChange = { invoicePrefix = it },
                label = { Text("Invoice Prefix (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = websiteUrl,
                onValueChange = { websiteUrl = it },
                label = { Text("Website URL (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = taxPercentage,
                onValueChange = { taxPercentage = it },
                label = { Text("Tax Percentage") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = discountPercentage,
                onValueChange = { discountPercentage = it },
                label = { Text("Discount Percentage") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- SAVE BUTTON ----------------
            Button(
                onClick = {
                    BusinessPrefs.saveDetails(
                        context, mapOf(
                            "business_name" to businessName,
                            "business_type" to businessType,
                            "business_address" to businessAddress,
                            "city" to city,
                            "state" to state,
                            "pincode" to pincode,
                            "gst_number" to gstNumber,
                            "pan_number" to panNumber,
                            "billing_currency" to billingCurrency,
                            "invoice_prefix" to invoicePrefix,
                            "website_url" to websiteUrl,
                            "tax_percentage" to taxPercentage,
                            "discount_percentage" to discountPercentage
                        )
                    )

                    Toast.makeText(context, "Business details saved", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Details", fontWeight = FontWeight.Bold)
            }
        }
    }
}
