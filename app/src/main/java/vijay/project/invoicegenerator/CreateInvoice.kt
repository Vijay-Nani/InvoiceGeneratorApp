package vijay.project.invoicegenerator


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class CreateInvoiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateInvoiceScreen()
        }
    }
}

data class InvoiceItem(
    val name: String = "",
    val description: String = "",
    val amount: String = ""
) {
    constructor() : this("", "", "")
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateInvoiceScreen() {
    // Basic form states
    var invoiceNum by rememberSaveable { mutableStateOf("") }
    var invoiceDate by rememberSaveable { mutableStateOf("") }
    var invoiceDueDate by rememberSaveable { mutableStateOf("") }
    var clientName by rememberSaveable { mutableStateOf("") }
    var clientEmail by rememberSaveable { mutableStateOf("") }

    // Item list and dialog state
    val itemList = remember { mutableStateListOf<InvoiceItem>() }
    var showAddItemDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create Invoice",
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
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {


                Spacer(modifier = Modifier.height(16.dp))

                // Invoice Number
                Text(
                    text = "Invoice Number",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = invoiceNum,
                    onValueChange = { invoiceNum = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true,
                    placeholder = { Text("Enter invoice number") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Invoice Date
                Text(
                    text = "Invoice Date",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Our DatePicker composable (reusable)
                DatePickerField(1, onDateSelected = { fieldType, Selecteddate ->
                    if (fieldType == 1) {
                        invoiceDate = Selecteddate
                    }
                })

                Spacer(modifier = Modifier.height(12.dp))

                // Due Date
                Text(
                    text = "Due Date",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                DatePickerField(fieldType = 2, onDateSelected = { fieldType, Selecteddate ->
                    if (fieldType == 2) {
                        Log.e("test", "in-$Selecteddate")


                        invoiceDueDate = Selecteddate
                    }
                })

                Spacer(modifier = Modifier.height(12.dp))

                // Client Name
                Text(
                    text = "Client Name",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = clientName,
                    onValueChange = { clientName = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    placeholder = { Text("Enter client name") }
                )


                Spacer(modifier = Modifier.height(12.dp))

                // Client Email
                Text(
                    text = "Client Email",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = clientEmail,
                    onValueChange = { clientEmail = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    placeholder = { Text("client@example.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )


                // Items header + Add Item button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Add Item",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(color = Color.Red)
                            .clickable { showAddItemDialog = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Show current items (dynamically from itemList)
                if (itemList.isEmpty()) {
                    Text(
                        text = "No items yet. Tap Add Item to create one.",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray
                    )
                } else {
                    itemList.forEach { item ->

                        ItemCard(
                            itemName = item.name,
                            description = item.description,
                            amount = item.amount
                        )
                    }
                }

                val defaultTax = BusinessPrefs.get(context,"TAX_PERCENT")
                val defaultDiscount = BusinessPrefs.get(context,"DISCOUNT_PERCENT")

                val tax = BusinessPrefs.get(context,"TAX_PERCENT")

                if(tax.isEmpty())
                {

                }


                // Summary card that computes totals from itemList
                SummaryCardFromItems(
                    onGenerateClicked = { total, tax, discount, toPay ->

                        if (invoiceNum.isBlank() || invoiceDate.isBlank() || invoiceDueDate.isBlank() || clientName.isBlank() || clientEmail.isBlank()) {
                            Toast.makeText(context, "All Fields Mandatory", Toast.LENGTH_SHORT)
                                .show()
                        } else if (itemList.isEmpty()) {
                            Toast.makeText(context, "Add Products", Toast.LENGTH_SHORT)
                                .show()
                        } else {


                            val invoiceData = InvoiceData(
                                invoicenumber = invoiceNum,
                                invoicedate = invoiceDate,
                                duedate = invoiceDueDate,
                                clientname = clientName,
                                clientemail = clientEmail,
                                items = itemList,
                                total = total.toString(),
                                tax = tax.toString(),
                                discount = discount.toString(),
                                topay = toPay.toString()
                            )

                            saveInvoiceToFirebase(
                                invoiceData,
                                context,
                                onResult = { success, message ->
                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                            .show()

                                        (context as Activity).finish()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                })

                        }
                    },
                    itemList = itemList,
                    taxPercent = BusinessPrefs.get(context,"TAX_PERCENT","18").toDouble(),
                    discountPercent = BusinessPrefs.get(context,"DISCOUNT_PERCENT","10").toDouble()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

        }


    )

    // Add Item Dialog
    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onSubmit = { name, desc, amt ->
                // sanitize: treat amount as string but store a user-friendly string
                val normalizedAmount = formatPound(parseAmountOrZero(amt))
                itemList.add(InvoiceItem(name.trim(), desc.trim(), normalizedAmount))
                showAddItemDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    fieldType: Int,
    onDateSelected: (fieldType: Int, Selecteddate: String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val datePickerState = rememberDatePickerState()

    // Keep date field in sync with datePickerState
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            selectedDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            Log.e("test", "in-$selectedDate")
            onDateSelected(
                fieldType,
                selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: ""
            )


            showDatePicker = false
        }
    }

    OutlinedTextField(
        value = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "",
        onValueChange = {
            Log.e("test", "in-$selectedDate")
            onDateSelected(
                fieldType,
                selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: ""
            )
        },
        placeholder = { Text("Select Date") },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Pick Date",
                modifier = Modifier.clickable { showDatePicker = true }
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.DarkGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    )


    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {},
            dismissButton = {}
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = { Text("Amount (e.g., 1245 or £1,245)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && amount.isNotBlank()) {
                        onSubmit(name, description, amount)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ItemCard(
    itemName: String,
    description: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = Color.LightGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amount :",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun SummaryCardFromItems(
    onGenerateClicked: (total: Double, tax: Double, discount: Double, toPay: Double) -> Unit,
    itemList: List<InvoiceItem>,
    modifier: Modifier = Modifier,
    taxPercent: Double = 18.0,
    discountPercent: Double = 30.0,

    ) {
    // Compute numeric total by parsing amounts
    val total = itemList.sumOf { parseAmountOrZero(it.amount).toDouble() }
    val tax = total * taxPercent / 100.0
    val discount = total * discountPercent / 100.0
    val toPay = total + tax - discount

    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            SummaryRow(title = "Total Amount", value = formatPound(total))
            SummaryRow(title = "Tax (${taxPercent.toInt()}%)", value = formatPound(tax))
            SummaryRow(
                title = "Discount (${discountPercent.toInt()}%)",
                value = formatPound(discount)
            )

            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SummaryRow(
                title = "To Pay",
                value = formatPound(toPay),
                valueColor = Color.Red,
                bold = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onGenerateClicked(total, tax, discount, toPay)

                },
                modifier = Modifier
                    .width(180.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
            ) {
                Text(
                    text = "Generate Bill",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SummaryRow(
    title: String,
    value: String,
    valueColor: Color = Color.Black,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            color = Color.DarkGray
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}

fun parseAmountOrZero(amountStr: String): Double {
    return try {
        // Remove common currency symbols and commas and spaces
        val cleaned = amountStr.replace("[^0-9.]".toRegex(), "")
        if (cleaned.isBlank()) 0.0 else cleaned.toDouble()
    } catch (e: Exception) {
        0.0
    }
}

fun formatPound(value: Double): String {
    return try {
        val nf = NumberFormat.getCurrencyInstance(Locale.UK)
        nf.format(value)
    } catch (e: Exception) {
        "£${"%.2f".format(value)}"
    }
}


// ---------- Preview ----------
@Preview(showBackground = true, heightDp = 1200)
@Composable
fun CreateInvoicePreview() {
    val sampleItems = remember {
        mutableStateListOf(
            InvoiceItem("Electricity Bill", "November 2025 – Residential Connection", "1245"),
            InvoiceItem("Water Bill", "November 2025", "750")
        )
    }

    Column {
        Text(
            text = "Preview of Create Invoice Screen",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        sampleItems.forEach {
            ItemCard(
                itemName = it.name,
                description = it.description,
                amount = formatPound(parseAmountOrZero(it.amount))
            )
        }

        SummaryCardFromItems(onGenerateClicked = { total, tax, discount, toPay ->

        }, itemList = sampleItems)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    heightDp = 1400
)
@Composable
fun CreateInvoiceScreenPreview() {
    MaterialTheme {
        CreateInvoiceScreen()
    }
}



fun saveInvoiceToFirebase(
    invoice: InvoiceData,
    context: Context,
    onResult: (Boolean, String) -> Unit
) {
    val email = UserPrefs.getEmail(context)

    val safeEmail = email.replace(".", "_")

    val database = FirebaseDatabase.getInstance().getReference("Invoices")

    // Push creates a unique ID for each invoice
    val invoiceId = database.child(safeEmail).push().key

    invoice.id= invoiceId.toString()


    if (invoiceId == null) {
        onResult(false, "Failed to generate invoice ID")
        return
    }

    database.child(safeEmail)
        .child(invoiceId)
        .setValue(invoice)
        .addOnSuccessListener {
            onResult(true, "Invoice saved successfully")
        }
        .addOnFailureListener { error ->
            onResult(false, error.message ?: "Something went wrong")
        }
}




