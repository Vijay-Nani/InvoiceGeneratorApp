package vijay.project.invoicegenerator


import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


suspend fun fetchInvoiceList(context: Context): List<InvoiceData> {

    val safeEmail = UserPrefs.getEmail(context).replace(".", "_")

    val dbRef = FirebaseDatabase.getInstance().getReference("Invoices/$safeEmail")

    return try {

        val snapshot = dbRef.get().await()

        if (!snapshot.exists()) {
            return emptyList()
        }

        if (snapshot.childrenCount == 0L) {
            return emptyList()
        }


        val list = snapshot.children.mapNotNull { child ->

            val invoice = child.getValue(InvoiceData::class.java)


            invoice
        }


        list

    } catch (e: Exception) {
        emptyList()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen() {

    var invoices by remember { mutableStateOf<List<InvoiceData>>(emptyList()) }
    var expandedCardIndex by remember { mutableIntStateOf(-1) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val data = fetchInvoiceList(context)

        invoices = data


    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Invoice",
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
        content = { padding ->

            if (invoices.isEmpty()) {
                Toast.makeText(context, "No Invoices Added", Toast.LENGTH_SHORT)
                    .show()

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(invoices.indices.toList()) { index ->

                        val invoice = invoices[index]



                        InvoiceCard(
                            invoice = invoice,
                            isExpanded = expandedCardIndex == index,
                            onClick = {
                                val newIndex = if (expandedCardIndex == index) -1 else index
                                expandedCardIndex = newIndex
                            }
                        )
                    }
                }
            }
        }
    )
}


// ---------------------- CARD UI ----------------------
@Composable
fun InvoiceCard(
    invoice: InvoiceData,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ---------- TOP ROW ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Invoice Date",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = invoice.invoicedate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "£ ${invoice.topay}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = invoice.clientname,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            // ---------- EXPANDED SECTION ----------
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {

                    DetailRow("Invoice Number", invoice.invoicenumber)
                    DetailRow("Due Date", invoice.duedate)
                    DetailRow("Client Email", invoice.clientemail)
                    DetailRow("Total", "£ ${invoice.total}")
                    DetailRow("Tax", "£ ${invoice.tax}")
                    DetailRow("Discount", "£ ${invoice.discount}")
                    DetailRow("Amount To Pay", "£ ${invoice.topay}")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Items",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    invoice.items.forEach {
                        Text(
                            text = "- ${it.name} • Qty: ${it.description} • £${it.amount}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}


class MyInvoiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InvoiceListScreen() }

    }
}