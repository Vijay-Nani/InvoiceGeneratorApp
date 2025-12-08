package vijay.project.invoicegenerator


// ----------------- imports -----------------
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale


class ManageInvoiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InvoiceAppNavHost() }

    }
}


data class InvoiceData(
    var id: String = "",
    val invoicenumber: String = "",
    val invoicedate: String = "",
    val duedate: String = "",
    val clientname: String = "",
    val clientemail: String = "",
    val items: List<InvoiceItem> = emptyList(),
    val total: String = "",
    val tax: String = "",
    val discount: String = "",
    val topay: String = "",
    val ispaid: Boolean = false
) {
    constructor() : this("", "", "", "", "", "", emptyList(), "", "", "", "")
}

private fun formatCurrencyOneDecimal(valueStr: String): String {
    val d = valueStr.toDoubleOrNull() ?: 0.0
    return String.format(Locale.ENGLISH, "%.1f", d)
}


private fun formatCurrencyWithSymbol(valueStr: String): String {
    val d = valueStr.toDoubleOrNull() ?: 0.0
    val nf = NumberFormat.getCurrencyInstance(Locale.UK) // £
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = 2
    return nf.format(d)
}

suspend fun markInvoicePaid(context: Context, invoiceId: String): Boolean {
    return try {
        val safeEmail = UserPrefs.getEmail(context).replace(".", "_")
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Invoices/$safeEmail/$invoiceId/ispaid")

        dbRef.setValue(true).await()
        Log.d("FIREBASE", "Invoice $invoiceId marked as PAID")
        true
    } catch (e: Exception) {
        Log.e("FIREBASE", "Error marking as paid: ${e.message}")
        false
    }
}



suspend fun deleteInvoice(context: Context, invoiceId: String): Boolean {
    return try {
        val safeEmail = UserPrefs.getEmail(context).replace(".", "_")
        val dbRef = FirebaseDatabase.getInstance().getReference("Invoices/$safeEmail/$invoiceId")
        dbRef.removeValue().await()
        Log.d("FIREBASE", "Invoice $invoiceId removed")
        true
    } catch (e: Exception) {
        Log.e("FIREBASE", "Error deleting invoice: ${e.message}")
        false
    }
}

@Composable
fun InvoiceAppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            InvoiceListScreen(onEdit = { invoiceId ->
                navController.navigate("edit/$invoiceId")
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(onEdit: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var invoices by remember { mutableStateOf<List<InvoiceData>>(emptyList()) }
    var expandedIndex by remember { mutableIntStateOf(-1) }
    var deletingInvoiceId by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Log.d("INVOICE_UI", "fetching invoices (initial)...")
        invoices = fetchInvoiceList(context)
        isLoading = false
        Log.d("INVOICE_UI", "Invoices loaded: ${invoices.size}")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Manage Invoices",
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
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (isLoading) {
                // simple loading
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading invoices...")
                }
                return@Box
            }

                if (invoices.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No invoices found", fontSize = 18.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(invoices) { index, invoice ->
                            InvoiceListCard(
                                invoice = invoice,
                                isExpanded = expandedIndex == index,
                                onToggleExpand = {
                                    expandedIndex = if (expandedIndex == index) -1 else index
                                },
                                onMarkPaid = {
                                    scope.launch {
                                        val ok = markInvoicePaid(context, invoice.id)
                                        if (ok) {
                                            Toast.makeText(
                                                context,
                                                "Marked as Paid",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            invoices = fetchInvoiceList(context)
                                        }
                                    }
                                },
                                onDelete = {
                                    deletingInvoiceId = invoice.id
                                    showDeleteConfirm = true
                                }
                            )

                        }
                    }
                }



            // Delete confirmation dialog
            if (showDeleteConfirm && deletingInvoiceId != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteConfirm = false
                        deletingInvoiceId = null
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteConfirm = false
                            val idToDelete = deletingInvoiceId
                            deletingInvoiceId = null
                            if (!idToDelete.isNullOrBlank()) {
                                scope.launch {
                                    val success = deleteInvoice(context, idToDelete)
                                    if (success) {
                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT)
                                            .show()
                                        // refresh list
                                        invoices = fetchInvoiceList(context)
                                    } else {
                                        Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteConfirm = false
                            deletingInvoiceId = null
                        }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Delete invoice?") },
                    text = { Text("This will permanently delete the selected invoice.") }
                )
            }
        }
    }
}

@Composable
fun InvoiceListCard(
    invoice: InvoiceData,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onMarkPaid: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row {
                        Text("Invoice Date", fontSize = 12.sp, color = Color.Gray)
                        if (invoice.ispaid) {
                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "PAID",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(invoice.invoicedate.ifBlank { "-" }, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(invoice.clientname.ifBlank { "-" }, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    // format pay amount to 1 decimal
                    val payText = try {
                        "£ ${formatCurrencyOneDecimal(invoice.topay)}"
                    } catch (e: Exception) {
                        "£ ${invoice.topay}"
                    }
                    Text(payText, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    DetailRow("Invoice No", invoice.invoicenumber)
                    DetailRow("Due Date", invoice.duedate)
                    DetailRow("Client Email", invoice.clientemail)
                    DetailRow("Total", formatCurrencyWithSymbol(invoice.total))
                    DetailRow("Tax", formatCurrencyWithSymbol(invoice.tax))
                    DetailRow("Discount", formatCurrencyWithSymbol(invoice.discount))
                    DetailRow("To Pay", formatCurrencyWithSymbol(invoice.topay))

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Items", fontWeight = FontWeight.Bold)
                    invoice.items.forEach { it ->
                        Text("- ${it.name} • ${it.description} • ${it.amount}", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // MARK AS PAID BUTTON
                        IconButton(
                            onClick = onMarkPaid
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Mark Paid",
                                tint = Color(0xFF4CAF50) // green
                            )
                        }

                        // DELETE BUTTON
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }

                }
            }
        }
    }
}


