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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vijay.project.invoicegenerator.ui.theme.Purple40
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CreateInvoiceScreen()
{
    var invoiceNum by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),

    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.brown_c1))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Text(
                text = "Create Invoice",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

        }

        Spacer(modifier= Modifier.height(16.dp))

        Text(
            text = "Invoice Number",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = invoiceNum,
            onValueChange = {invoiceNum= it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            )
        )

        Spacer(modifier= Modifier.height(12.dp))

        Text(
            text = "Invoice Date",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(6.dp))

        DatePicker()

        Spacer(modifier= Modifier.height(12.dp))

        Text(
            text = "Due Date",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(6.dp))

        DatePicker()

        Spacer(modifier= Modifier.height(12.dp))

        Text(
            text = "Client Name",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = clientName,
            onValueChange = {clientName= it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            )
        )

        Spacer(modifier= Modifier.height(12.dp))

        Text(
            text = "Client Email",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = clientEmail,
            onValueChange = {clientEmail= it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            )
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically)
        {

            Text(
                text = "Items",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
//                    .padding(start = 12.dp)

            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Add Item",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = Color.Red,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                textAlign = TextAlign.Center
            )
        }


        ItemCard(
            itemName = "Electricity Bill",
            description = "November 2025 – Residential Connection",
            amount = "₹1,245"
        )

        SummaryCard(
            totalAmount = "₹10,000",
            taxAmount = "₹1,800",
            discountAmount = "₹3,000",
            toPayAmount = "₹8,800"
        )

        Spacer(modifier = Modifier.height(24.dp))




    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker() {
    // Whether the date picker dialog is visible
    var showDatePicker by remember { mutableStateOf(false) }

    // Store the selected date
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // State for the Material 3 DatePicker
    val datePickerState = rememberDatePickerState()

    // Automatically update when user selects a date
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            selectedDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            showDatePicker = false // auto-close after selecting
        }
    }

    OutlinedTextField(
        value = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "",
        onValueChange = {},
        placeholder = { Text("Select Date") },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Pick Date",
                modifier = Modifier.clickable { showDatePicker = true } // 👈 open picker
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),

        )

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {}, // no confirm button
            dismissButton = {}
        ) {
            DatePicker(state = datePickerState)
        }
    }
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
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))


            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = Color.LightGray, thickness = 2.dp)

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
fun SummaryCard(
    totalAmount: String,
    taxAmount: String,
    discountAmount: String,
    toPayAmount: String,
    modifier: Modifier = Modifier
) {
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
            // Title
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary rows
            SummaryRow(title = "Total Amount", value = totalAmount)
            SummaryRow(title = "Tax (18%)", value = taxAmount)
            SummaryRow(title = "Discount (30%)", value = discountAmount)

            Divider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Final amount row
            SummaryRow(
                title = "To Pay",
                value = toPayAmount,
                valueColor = Color.Red,
                bold = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Centered Generate Bill Button
            Button(
                onClick = { /* TODO: Handle Generate Bill action */ },
                modifier = Modifier
                    .width(180.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor =Purple40)
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


@Preview(showBackground = true,  heightDp = 1200)
@Composable
fun CreateInvoicePreview() {
    CreateInvoiceScreen()
}