package vijay.project.invoicegenerator


import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        val data = fetchInvoiceList(context)
        isLoading = false

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

            } else {

                if (invoices.isEmpty()) {
                    Toast.makeText(context, "No Invoices Added", Toast.LENGTH_SHORT)
                        .show()

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 12.dp),
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
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }


    if (isLoading) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {}, // required in Material3
            title = { Text("Generating Invoice") },
            text = { Text("Please wait…") }
        )


    }

    val formattedTopay = String.format("%.1f", invoice.topay.toDoubleOrNull() ?: 0.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Invoice Date", fontSize = 12.sp, color = Color.Gray)
                    Text(invoice.invoicedate, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "£ $formattedTopay",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(invoice.clientname, fontSize = 15.sp, fontWeight = FontWeight.Medium)

            AnimatedVisibility(isExpanded) {
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
                    DetailRow("To Pay", "£ $formattedTopay")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Items", fontWeight = FontWeight.Bold)
                    invoice.items.forEach {
                        Text("- ${it.name} • ${it.description} • ${it.amount}", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val uri = generatePdfWithMediaStore(context, invoice)
                                isLoading = false
                                sharePdfUri(context, uri)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Share Invoice", color = Color.White)
                    }
                }
            }
        }
    }
}


fun sharePdfUri(context: Context, pdfUri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, pdfUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(intent, "Share Invoice PDF")
    )
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

fun generatePdfWithMediaStore(
    context: Context,
    invoice: InvoiceData
): Uri {

    val fileName = "Invoice_${invoice.invoicenumber}.pdf"

    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        put(MediaStore.Downloads.RELATIVE_PATH, "Download/")
        put(MediaStore.Downloads.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: throw Exception("Failed to create PDF file location")

    resolver.openOutputStream(uri).use { outStream ->

        val doc = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas

        // ----------- BUSINESS INFO -----------
        paint.textSize = 22f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(BusinessPrefs.get(context, "business_name"), 40f, 60f, paint)

        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(BusinessPrefs.get(context, "business_type"), 40f, 90f, paint)
        canvas.drawText(BusinessPrefs.get(context, "business_address"), 40f, 120f, paint)

        // ----------- INVOICE HEADER ----------
        canvas.drawText("Invoice Number: ${invoice.invoicenumber}", 40f, 170f, paint)
        canvas.drawText("Invoice Date: ${invoice.invoicedate}", 40f, 200f, paint)
        canvas.drawText("Due Date: ${invoice.duedate}", 40f, 230f, paint)

        // ----------- CLIENT DETAILS ----------
        canvas.drawText("Bill To: ${invoice.clientname}", 40f, 270f, paint)
        canvas.drawText("Email: ${invoice.clientemail}", 40f, 300f, paint)

        // ----------- ITEMS ----------
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Items:", 40f, 340f, paint)

        paint.typeface = Typeface.DEFAULT
        var y = 370f
        invoice.items.forEach {
            canvas.drawText("${it.name} - ${it.description} - ${it.amount}", 40f, y, paint)
            y += 30f
        }

        // ----------- TOTALS ----------
        canvas.drawText("Total: £${invoice.total}", 40f, y + 40, paint)
        canvas.drawText("Tax: £${invoice.tax}", 40f, y + 70, paint)
        canvas.drawText("Discount: £${invoice.discount}", 40f, y + 100, paint)
        canvas.drawText("To Pay: £${invoice.topay}", 40f, y + 130, paint)

        // ----------- QR CODE IMAGE ----------
        val qrBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.qrcode)

        // Scale QR to a good PDF size (150x150 px)
        val scaledQR = Bitmap.createScaledBitmap(qrBitmap, 180, 180, false)

        val qrX = 380f
        val qrY = 200f

        canvas.drawBitmap(scaledQR, qrX, qrY, paint)

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 14f
        canvas.drawText("Pay to this QR", qrX, qrY + 200, paint)

        doc.finishPage(page)
        doc.writeTo(outStream)
        doc.close()
    }

    values.put(MediaStore.Downloads.IS_PENDING, 0)
    resolver.update(uri, values, null, null)

    return uri
}


class MyInvoiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InvoiceListScreen() }

    }
}