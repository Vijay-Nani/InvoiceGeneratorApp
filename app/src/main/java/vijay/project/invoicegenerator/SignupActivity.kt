package vijay.project.invoicegenerator


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FillActivityScreen()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FillActivityScreenPreview() {
    FillActivityScreen()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillActivityScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var FullName by remember { mutableStateOf("") }
    var CompanyName by remember { mutableStateOf("") }
    var Address by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    val context1 = LocalContext.current

    var dobDate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun openDatePicker(onSelect: (String) -> Unit, minDate: Long? = null) {
        val dp = DatePickerDialog(
            context1,
            { _, year, month, day ->
                val c = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onSelect(dateFormat.format(c.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        if (minDate != null) dp.datePicker.minDate = minDate
        dp.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Set the outer background color
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section with Back Arrow and Sign Up
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)

                    .padding(16.dp), // Padding around the row
                contentAlignment = Alignment.TopStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = "Sign Up", fontWeight = FontWeight.Bold,

                        style = MaterialTheme.typography.headlineSmall, color = Color.White
                    )
                }
            }

            // Login Form Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 32.dp,

                            )
                    ) // Apply rounded corners
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Login Title


                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = FullName,
                        onValueChange = { FullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            disabledContainerColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    DOBDateField(
                        label = "Date of Birth",
                        value = dobDate,
                        onClick = {
                            val today = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis

                            openDatePicker({ dobDate = it }, today)
                        }
                    )


                    // Last Name TextField
                    OutlinedTextField(
                        value = CompanyName,
                        onValueChange = { CompanyName = it },
                        label = { Text("Company Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            disabledContainerColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = Address,
                        onValueChange = { Address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            disabledContainerColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    // Email TextField
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


                    // Password TextField
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            disabledContainerColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description =
                                if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        }
                    )


                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            disabledContainerColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button
                    Button(
                        onClick = {
                            when {
                                FullName.isBlank() -> {
                                    errorMessage = "Please enter your full name."
                                }

                                CompanyName.isBlank() -> {
                                    errorMessage = "Please enter your company name."
                                }

                                Address.isBlank() -> {
                                    errorMessage = "Please enter your address."
                                }

                                email.isBlank() -> {
                                    errorMessage = "Please enter your email."
                                }

                                password.isBlank() -> {
                                    errorMessage = "Please enter your password."
                                }

                                confirmPassword.isBlank() -> {
                                    errorMessage = "Please confirm your password."
                                }

                                else -> {
                                    errorMessage = ""


                                    val userData = AccountData(
                                        name = FullName,
                                        companyName = CompanyName,
                                        address = Address,
                                        email = email,
                                        password = CryptoUtils.encrypt(password)
                                    )


                                    val db = FirebaseDatabase.getInstance()
                                    val ref = db.getReference("BusinessAccounts")
                                    ref.child(userData.email.replace(".", ",")).setValue(userData)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Registration Successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                context.startActivity(
                                                    Intent(
                                                        context, SignInActivity::class.java
                                                    )
                                                )
                                                (context as Activity).finish()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "User Registration Failed: ${task.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }.addOnFailureListener { exception ->
                                            Toast.makeText(
                                                context,
                                                "User Registration Failed: ${exception.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(text = "Sign Up", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Footer Text
                    Row {
                        Text(text = "Already have an account? ", color = Color.Black)
                        Text(
                            text = "Login", color = Color.Blue, modifier = Modifier.clickable {
                                context.startActivity(Intent(context, SignInActivity::class.java))
                                (context as Activity).finish()
                            })
                    }

                }
            }
        }
    }
}

data class AccountData
    (
    var name: String = "",
    var dob: String = "",
    var companyName: String = "",
    var address: String = "",
    var email: String = "",
    var password: String = "",
)


@Composable
fun DOBDateField(label: String, value: String, onClick: () -> Unit) {
    Column {
        Text(label, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))

        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select $label") },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { onClick() }
            )
        }
    }
}