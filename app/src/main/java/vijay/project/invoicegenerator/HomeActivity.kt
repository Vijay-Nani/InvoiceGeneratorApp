package vijay.project.invoicegenerator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))


                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 6.dp)
                            .background(color = colorResource(id = R.color.trans_white))
                            .padding(vertical = 6.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,


                        )
                    {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {

                            Text(
                                text = "Total Invoices",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,

                                )



                            Text(
                                text = "35",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )

                        }


                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {

                            Text(
                                text = "Paid Invoices",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,

                                )



                            Text(
                                text = "30",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold

                            )

                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {

                            Text(
                                text = "Pending Invoices",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,

                                )



                            Text(
                                text = "05",
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
                            text = "All Invoice",
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
                            painter = painterResource(id = R.drawable.manage_invoice),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Manage Invoice",
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
                            text = "Clients",
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
                            painter = painterResource(id = R.drawable.summary),
                            contentDescription = "",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Summary",
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
                            text = "Profile",
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
