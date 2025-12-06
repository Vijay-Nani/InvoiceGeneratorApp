package vijay.project.invoicegenerator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val context = LocalContext.current

    val userName = UserPrefs.getName(context)

    val lifecycleOwner = LocalLifecycleOwner.current

    var totalInvoices by remember { mutableStateOf(InvoiceCounts()) }

    suspend fun refreshCounts() {
        val result = getInvoiceCounts(context)
        Log.d("INVOICE_COUNT", "Refreshed Count: $result")
        totalInvoices = result
    }

    LaunchedEffect(Unit) {
        refreshCounts()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Log.d("INVOICE_COUNT", "Screen resumed → refreshing counts")
                // Launch coroutine because observer is not a suspend function
                lifecycleOwner.lifecycleScope.launch {
                    refreshCounts()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Scaffold(
        topBar = {

        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = R.color.brown_c1))
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "",
                            modifier = Modifier
                                .size(44.dp)
                                .padding(start = 8.dp)
                                .clickable {
                                }

                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Welcome, $userName",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))


                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,


                        )
                    {
                        Column(
                            modifier = Modifier
                                .background(color = colorResource(id = R.color.trans_white))
                                .padding(horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {

                            Text(
                                text = "Total Invoices",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,

                                )



                            Text(
                                text = totalInvoices.totalInvoices.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )

                        }


                        Column(
                            modifier = Modifier
                                .background(color = colorResource(id = R.color.trans_white))
                                .padding(horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {

                            Text(
                                text = "Paid Invoices",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,

                                )



                            Text(
                                text = totalInvoices.paidInvoices.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold

                            )

                        }
                        Column(
                            modifier = Modifier
                                .background(color = colorResource(id = R.color.trans_white))
                                .padding(horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {

                            Text(
                                text = "Pending Invoices",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,

                                )



                            Text(
                                text = totalInvoices.pendingInvoices.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold

                            )

                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                (context as Activity).startActivity(
                                    Intent(
                                        context,
                                        CreateInvoiceActivity::class.java
                                    )
                                )
                            }
                            .background(
                                color = Color(0xFFEFEFEF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)

                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.create_invoice),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Create Invoice",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center

                        )


                    }
                    Spacer(modifier = Modifier.width(6.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                (context as Activity).startActivity(
                                    Intent(
                                        context,
                                        MyInvoiceActivity::class.java
                                    )
                                )
                            }
                            .background(
                                color = Color(0xFFEFEFEF),   // your background color
                                shape = RoundedCornerShape(12.dp) // optional rounded corners
                            )
                            .padding(16.dp) // inner padding so content doesn't touch edges

                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.all_invoice),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "All\nInvoice",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center

                        )


                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                (context as Activity).startActivity(
                                    Intent(
                                        context,
                                        ManageInvoiceActivity::class.java
                                    )
                                )
                            }
                            .background(
                                color = Color(0xFFEFEFEF),   // your background color
                                shape = RoundedCornerShape(12.dp) // optional rounded corners
                            )
                            .padding(16.dp) // inner padding so content doesn't touch edges

                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.manage_invoice),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Manage\nInvoice",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,

                            textAlign = TextAlign.Center

                        )


                    }


                }


                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                (context as Activity).startActivity(
                                    Intent(
                                        context,
                                        ManageBusiness::class.java
                                    )
                                )
                            }
                            .background(
                                color = Color(0xFFEFEFEF),   // your background color
                                shape = RoundedCornerShape(12.dp) // optional rounded corners
                            )
                            .padding(16.dp) // inner padding so content doesn't touch edges

                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.client),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Manage\nBusiness",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )


                    }
                    Spacer(modifier = Modifier.width(6.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                (context as Activity).startActivity(
                                    Intent(
                                        context,
                                        InvoiceSummaryActivity::class.java
                                    )
                                )
                            }
                            .background(
                                color = Color(0xFFEFEFEF),   // your background color
                                shape = RoundedCornerShape(12.dp) // optional rounded corners
                            )
                            .padding(16.dp) // inner padding so content doesn't touch edges

                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.summary),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Summary\n",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center

                        )


                    }
                    Spacer(modifier = Modifier.width(6.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                            }
                            .background(
                                color = Color(0xFFEFEFEF),   // your background color
                                shape = RoundedCornerShape(12.dp) // optional rounded corners
                            )
                            .padding(16.dp) // inner padding so content doesn't touch edges

                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.profile_2),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Profile\n",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center

                        )


                    }


                }

            }
        }
    )


}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}


data class InvoiceCounts(
    val totalInvoices: Int = 0,
    val paidInvoices: Int = 0,
    val pendingInvoices: Int = 0
)

suspend fun getInvoiceCounts(context: Context): InvoiceCounts {
    val safeEmail = UserPrefs.getEmail(context).replace(".", "_")
    val dbRef = FirebaseDatabase.getInstance().getReference("Invoices/$safeEmail")

    return try {
        val snapshot = dbRef.get().await()

        if (!snapshot.exists()) {
            Log.d("INVOICE_COUNT", "No invoices found.")
            return InvoiceCounts(0, 0, 0)
        }

        var total = 0
        var paid = 0
        var pending = 0

        for (child in snapshot.children) {
            total++

//            val status = child.child("status").getValue(String::class.java) ?: ""

            val status = child.child("ispaid").getValue(Boolean::class.java) ?: false



            if (status) {
                paid++
            } else {
                pending++
            }


//            when (status.lowercase()) {
//                "paid" -> paid++
//                "pending" -> pending++
//            }
        }

        Log.d("INVOICE_COUNT", "Total=$total Paid=$paid Pending=$pending")

        InvoiceCounts(total, paid, pending)

    } catch (e: Exception) {
        Log.e("INVOICE_COUNT", "Error: ${e.message}")
        InvoiceCounts(0, 0, 0)
    }
}
