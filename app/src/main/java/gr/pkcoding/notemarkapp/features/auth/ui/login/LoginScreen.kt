package gr.pkcoding.notemarkapp.features.auth.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import gr.pkcoding.notemarkapp.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gr.pkcoding.notemarkapp.ui.adaptive.AdaptiveText
import gr.pkcoding.notemarkapp.ui.adaptive.MaterialTextStyle
import gr.pkcoding.notemarkapp.ui.adaptive.adaptiveValue
import gr.pkcoding.notemarkapp.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // State management
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Validation
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid

    // Light blue background (edge-to-edge)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0EAFF)) // Light blue background
    ) {
        // Surface area με rounded top corners
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = adaptiveValue(60.dp, 80.dp, 100.dp)) // Space for status bar
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(SurfaceLowest)
                .padding(
                    horizontal = adaptiveValue(24.dp, 32.dp, 40.dp),
                    vertical = adaptiveValue(32.dp, 40.dp, 48.dp)
                )
        ) {
            // Header Section
            LoginHeader()

            Spacer(modifier = Modifier.height(adaptiveValue(32.dp, 40.dp, 48.dp)))

            // Input Fields Section
            LoginInputFields(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible })

            Spacer(modifier = Modifier.height(adaptiveValue(32.dp, 40.dp, 48.dp)))

            // Login Button
            LoginButton(
                enabled = isFormValid && !isLoading, isLoading = isLoading, onClick = {
                    isLoading = true
                    onLoginClick(email, password)
                })

            Spacer(modifier = Modifier.height(adaptiveValue(24.dp, 28.dp, 32.dp)))

            // Sign Up Link
            SignUpLink(onClick = onSignUpClick)
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        AdaptiveText(
            text = "Log In", style = MaterialTextStyle.DisplayLarge, textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(adaptiveValue(8.dp, 12.dp, 16.dp)))

        AdaptiveText(
            text = "Capture your thoughts and ideas.",
            style = MaterialTextStyle.BodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoginInputFields(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(adaptiveValue(20.dp, 24.dp, 28.dp))
    ) {
        // Email Field
        InputField(
            label = "Email",
            value = email,
            onValueChange = onEmailChange,
            placeholder = "john.doe@example.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Password Field
        InputField(
            label = "Password",
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Password",
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        painter = painterResource(R.drawable.eye_icon),
                        contentDescription = if (isPasswordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        AdaptiveText(
            text = label,
            style = MaterialTextStyle.BodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = BlueBase,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = BlueBase
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun LoginButton(
    enabled: Boolean, isLoading: Boolean, onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(adaptiveValue(48.dp, 56.dp, 64.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = BlueBase,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(adaptiveValue(8.dp, 12.dp, 16.dp))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp
            )
        } else {
            AdaptiveText(
                text = "Log In", style = MaterialTextStyle.BodyLarge, color = Color.White
            )
        }
    }
}

@Composable
private fun SignUpLink(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = onClick) {
            AdaptiveText(
                text = "Don't have an account?",
                style = MaterialTextStyle.BodyMedium,
                color = BlueBase
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    NoteMarkAppTheme {
        LoginScreen()
    }
}