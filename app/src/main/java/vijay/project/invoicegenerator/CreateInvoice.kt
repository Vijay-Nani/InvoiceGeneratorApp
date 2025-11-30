package vijay.project.invoicegenerator

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import vijay.project.invoicegenerator.ui.theme.Purple40


// Single-file Compose screen: CreateInvoiceScreenWithAddItem.kt
// Drop into your Compose module. Adjust package if needed.

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions as FKeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.mutableStateListOf

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CreateInvoiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateInvoiceScreen()
        }
    }
}

data class InvoiceItem(
    val name: String,
    val description: String,
    val amount: String
)

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF6D4C41)) // fallback brown
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Invoice",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

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
            if (fieldType == 1){ invoiceDate = Selecteddate }
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
            if (fieldType == 2){
                Log.e("test","in-$Selecteddate")


                invoiceDueDate = Selecteddate }})

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

                ItemCard(itemName = item.name, description = item.description, amount = item.amount)
            }
        }



        // Summary card that computes totals from itemList
        SummaryCardFromItems(
            onGenerateClicked = {
                if (invoiceNum.isBlank() || invoiceDate.isBlank() || invoiceDueDate.isBlank() || clientName.isBlank() || clientEmail.isBlank()) {
                    Log.e("test","in-$invoiceNum")
                    Log.e("test","in-$invoiceDate")
                    Log.e("test","in-$invoiceDueDate")
                    Log.e("test","in-$clientName")
                    Log.e("test","in-$clientEmail")

                    Toast.makeText(context, "All Fields 1", Toast.LENGTH_SHORT)
                        .show()
                }
                else{
                    Toast.makeText(context, "Report is Saved", Toast.LENGTH_SHORT)
                        .show()}
            },
            itemList = itemList
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onSubmit = { name, desc, amt ->
                // sanitize: treat amount as string but store a user-friendly string
                val normalizedAmount = formatINR(parseAmountOrZero(amt))
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

            Log.e("test","in-$selectedDate")
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
            Log.e("test","in-$selectedDate")
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
                    placeholder = { Text("Amount (e.g., 1245 or ₹1,245)") },
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
                    } else {
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
    onGenerateClicked: () -> Unit,
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

            SummaryRow(title = "Total Amount", value = formatINR(total))
            SummaryRow(title = "Tax (${taxPercent.toInt()}%)", value = formatINR(tax))
            SummaryRow(
                title = "Discount (${discountPercent.toInt()}%)",
                value = formatINR(discount)
            )

            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SummaryRow(
                title = "To Pay",
                value = formatINR(toPay),
                valueColor = Color.Red,
                bold = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onGenerateClicked()

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

fun formatINR(value: Double): String {
    return try {
        val nf = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        nf.format(value)
    } catch (e: Exception) {
        "₹${"%.2f".format(value)}"
    }
}

fun formatINR(value: Long): String = formatINR(value.toDouble())

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
                amount = formatINR(parseAmountOrZero(it.amount))
            )
        }

        SummaryCardFromItems(onGenerateClicked = {}, itemList = sampleItems)
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

data class InvoiceData(
    val Invoicenumber: String = "",
    val Invoicedate: String = "",
    val Duedate: String = "",
    val Clientname: String = "",
    val Clientemail: String = "",

    )




