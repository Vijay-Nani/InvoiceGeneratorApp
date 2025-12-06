package vijay.project.invoicegenerator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class InvoiceSummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InvoiceSummaryScreen() }

    }
}


data class InvoiceSummary(
    val totalInvoices: Int = 0,
    val totalPaid: Int = 0,
    val totalUnpaid: Int = 0,
    val totalCollected: Double = 0.0,
    val totalDue: Double = 0.0,
    val totalTax: Double = 0.0,
    val totalDiscount: Double = 0.0
)

// ---------------------------- FIREBASE SUMMARY ----------------------------
suspend fun fetchInvoiceSummary(context: Context): InvoiceSummary {
    return try {
        val safeEmail = UserPrefs.getEmail(context).replace(".", "_")
        val ref = FirebaseDatabase.getInstance()
            .getReference("Invoices/$safeEmail")

        val snap = ref.get().await()
        if (!snap.exists()) return InvoiceSummary()

        var totalInvoices = 0
        var totalPaid = 0
        var totalUnpaid = 0

        var totalCollected = 0.0
        var totalDue = 0.0
        var totalTax = 0.0
        var totalDiscount = 0.0

        snap.children.forEach { child ->
            val invoice = child.getValue(InvoiceData::class.java) ?: return@forEach

            totalInvoices += 1

            val pay = invoice.topay.toDoubleOrNull() ?: 0.0
            val tax = invoice.tax.toDoubleOrNull() ?: 0.0
            val discount = invoice.discount.toDoubleOrNull() ?: 0.0

            totalTax += tax
            totalDiscount += discount

            if (invoice.ispaid) {
                totalPaid++
                totalCollected += pay
            } else {
                totalUnpaid++
                totalDue += pay
            }
        }

        InvoiceSummary(
            totalInvoices = totalInvoices,
            totalPaid = totalPaid,
            totalUnpaid = totalUnpaid,
            totalCollected = totalCollected,
            totalDue = totalDue,
            totalTax = totalTax,
            totalDiscount = totalDiscount
        )

    } catch (e: Exception) {
        Log.e("SUMMARY", "Error summary: ${e.message}")
        InvoiceSummary()
    }
}

// ---------------------------- SUMMARY SCREEN UI ----------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceSummaryScreen() {
    val context = LocalContext.current
    var summary by remember { mutableStateOf(InvoiceSummary()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        summary = fetchInvoiceSummary(context)
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Invoice Summary",
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            if (isLoading) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading summary...")
                }
                return@Box
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                item { SummaryCard("Total Invoices", summary.totalInvoices.toString()) }
                item {
                    SummaryCard(
                        "Paid Invoices",
                        summary.totalPaid.toString(),
                        Color(0xFF2E7D32)
                    )
                }
                item {
                    SummaryCard(
                        "Unpaid Invoices",
                        summary.totalUnpaid.toString(),
                        Color(0xFFD32F2F)
                    )
                }

                item {
                    SummaryCard(
                        "Total Collected Amount",
                        "£${"%.2f".format(summary.totalCollected)}",
                        Color(0xFF2E7D32)
                    )
                }

                item {
                    SummaryCard(
                        "Total Due Amount",
                        "£${"%.2f".format(summary.totalDue)}",
                        Color(0xFFD32F2F)
                    )
                }

                item {
                    SummaryCard(
                        "Total Tax Amount",
                        "£${"%.2f".format(summary.totalTax)}",
                        Color(0xFF1565C0)
                    )
                }

                item {
                    SummaryCard(
                        "Total Discount Given",
                        "£${"%.2f".format(summary.totalDiscount)}",
                        Color(0xFF6A1B9A)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ---------------------------- SUMMARY CARD UI ----------------------------
@Composable
fun SummaryCard(title: String, value: String, valueColor: Color = Color.Black) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = valueColor
            )
        }
    }
}
