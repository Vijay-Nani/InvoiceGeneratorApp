package vijay.project.invoicegenerator

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen() {

    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var step2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun openDatePicker(onSelect: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val c = Calendar.getInstance().apply { set(year, month, day) }
                onSelect(dateFormat.format(c.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ----------------------------------------------
            // TOP BLACK SECTION WITH TITLE
            // ----------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(modifier = Modifier.padding(start = 18.dp)) {
                    Text(
                        text = "Forgot Password?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reset it now!",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            // ----------------------------------------------
            // WHITE ROUNDED CONTAINER
            // ----------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .clip(RoundedCornerShape(topStart = 32.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Reset Password",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // *************************************
                    // STEP 1 - EMAIL + DOB VERIFICATION
                    // *************************************
                    if (!step2) {

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                disabledContainerColor = Color.LightGray,
                                focusedBorderColor = Color(0xFF6200EE),
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = dob,
                            onValueChange = { },
                            label = { Text("Date of Birth") },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openDatePicker { dob = it } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                disabledContainerColor = Color.LightGray,
                                focusedBorderColor = Color(0xFF6200EE),
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                loading = true
                                errorMessage = ""
                                successMessage = ""

                                val key = email.replace(".", ",")

                                FirebaseDatabase.getInstance()
                                    .getReference("BusinessAccounts")
                                    .child(key)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        loading = false

                                        if (!snapshot.exists()) {
                                            errorMessage = "User not found"
                                            return@addOnSuccessListener
                                        }

                                        val dbDob = snapshot.child("dob").value?.toString() ?: ""

                                        if (dbDob == dob) {
                                            step2 = true
                                        } else {
                                            errorMessage = "Email or DOB incorrect"
                                        }
                                    }
                                    .addOnFailureListener {
                                        loading = false
                                        errorMessage = "Error: ${it.localizedMessage}"
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Verify Details", color = Color.White)
                        }
                    }

                    // *************************************
                    // STEP 2 - RESET PASSWORD
                    // *************************************
                    if (step2) {

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (newPassword != confirmPassword) {
                                    errorMessage = "Passwords do not match"
                                    return@Button
                                }

                                loading = true
                                val key = email.replace(".", ",")

                                FirebaseDatabase.getInstance()
                                    .getReference("BusinessAccounts")
                                    .child(key)
                                    .child("password")
                                    .setValue(newPassword)
                                    .addOnSuccessListener {
                                        loading = false
                                        successMessage = "Password updated successfully!"


                                        (context as Activity).finish()
                                    }
                                    .addOnFailureListener {
                                        loading = false
                                        errorMessage = "Failed to update password"
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Update Password", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (loading) Text("Processing...", color = Color.DarkGray)

                    if (errorMessage.isNotEmpty())
                        Text(errorMessage, color = Color.Red)

                    if (successMessage.isNotEmpty())
                        Text(successMessage, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}
