package gr.pkcoding.notemarkapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import gr.pkcoding.notemarkapp.R
import gr.pkcoding.notemarkapp.ui.adaptive.AdaptiveText
import gr.pkcoding.notemarkapp.ui.adaptive.MaterialTextStyle
import gr.pkcoding.notemarkapp.ui.adaptive.adaptiveValue
import gr.pkcoding.notemarkapp.ui.theme.BlueBase
import gr.pkcoding.notemarkapp.ui.theme.LightBlue
import gr.pkcoding.notemarkapp.ui.theme.SurfaceLowest

@Composable
fun RegisterScreen(
    onRegisterClick: (username: String, email: String, password: String, confirmPassword: String) -> Unit = { _, _, _, _ -> },
    onSignInClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // State management
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Focus states for showing supporting text
    var usernameFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    // Error states
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateUsername(value: String): String? {
        return when {
            value.length < 3 -> "Username must be at least 3 characters."
            value.length > 20 -> "Username can't be longer than 20 characters."
            else -> null
        }
    }

    fun validateEmail(value: String): String? {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            "Invalid email provided"
        } else null
    }

    fun validatePassword(value: String): String? {
        val hasNumberOrSymbol = value.any { it.isDigit() || !it.isLetterOrDigit() }
        return if (value.length < 8 || !hasNumberOrSymbol) {
            "Password must be at least 8 characters and include a number or symbol."
        } else null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        return if (password != confirm) "Passwords do not match" else null
    }

    // Validation check for button state
    val isFormValid = username.length in 3..20 &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            password.length >= 8 &&
            password.any { it.isDigit() || !it.isLetterOrDigit() } &&
            password == confirmPassword

    // Light blue background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        // Surface area με rounded top corners
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = adaptiveValue(60.dp, 80.dp, 100.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(SurfaceLowest)
                .padding(
                    horizontal = adaptiveValue(24.dp, 32.dp, 40.dp),
                    vertical = adaptiveValue(32.dp, 40.dp, 48.dp)
                )
                .verticalScroll(rememberScrollState()) // For keyboard handling
        ) {
            // Header Section
            RegisterHeader()

            Spacer(modifier = Modifier.height(adaptiveValue(32.dp, 40.dp, 48.dp)))

            // Input Fields Section
            RegisterInputFields(
                username = username,
                onUsernameChange = {
                    username = it
                    usernameError = if (!usernameFocused && it.isNotEmpty()) validateUsername(it) else null
                },
                usernameFocused = usernameFocused,
                onUsernameFocusChange = { usernameFocused = it },
                usernameError = usernameError,

                email = email,
                onEmailChange = {
                    email = it
                    emailError = if (it.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                        validateEmail(it)
                    } else null
                },
                emailError = emailError,

                password = password,
                onPasswordChange = {
                    password = it
                    passwordError = if (it.isNotEmpty()) validatePassword(it) else null
                    confirmPasswordError = if (confirmPassword.isNotEmpty()) validateConfirmPassword(it, confirmPassword) else null
                },
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                passwordFocused = passwordFocused,
                onPasswordFocusChange = { passwordFocused = it },
                passwordError = passwordError,

                confirmPassword = confirmPassword,
                onConfirmPasswordChange = {
                    confirmPassword = it
                    confirmPasswordError = if (it.isNotEmpty()) validateConfirmPassword(password, it) else null
                },
                isConfirmPasswordVisible = isConfirmPasswordVisible,
                onConfirmPasswordVisibilityToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                confirmPasswordError = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(adaptiveValue(32.dp, 40.dp, 48.dp)))

            // Register Button
            RegisterButton(
                enabled = isFormValid && !isLoading,
                isLoading = isLoading,
                onClick = {
                    isLoading = true
                    onRegisterClick(username, email, password, confirmPassword)
                }
            )

            Spacer(modifier = Modifier.height(adaptiveValue(24.dp, 28.dp, 32.dp)))

            // Sign In Link
            SignInLink(onClick = onSignInClick)
        }
    }
}

@Composable
private fun RegisterHeader() {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        AdaptiveText(
            text = "Create account",
            style = MaterialTextStyle.DisplayLarge,
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(adaptiveValue(8.dp, 12.dp, 16.dp)))

        AdaptiveText(
            text = "Capture your thoughts and ideas.",
            style = MaterialTextStyle.BodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Left
        )
    }
}

@Composable
private fun RegisterInputFields(
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameFocused: Boolean,
    onUsernameFocusChange: (Boolean) -> Unit,
    usernameError: String?,

    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,

    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    passwordFocused: Boolean,
    onPasswordFocusChange: (Boolean) -> Unit,
    passwordError: String?,

    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isConfirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityToggle: () -> Unit,
    confirmPasswordError: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(adaptiveValue(20.dp, 24.dp, 28.dp))
    ) {
        // Username Field
        InputFieldWithValidation(
            label = "Username",
            value = username,
            onValueChange = onUsernameChange,
            placeholder = "John.doe",
            supportingText = if (usernameFocused) "Use between 3 and 20 characters for your username." else null,
            errorText = usernameError,
            onFocusChange = onUsernameFocusChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        // Email Field
        InputFieldWithValidation(
            label = "Email",
            value = email,
            onValueChange = onEmailChange,
            placeholder = "john.doe@example.com",
            errorText = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Password Field
        InputFieldWithValidation(
            label = "Password",
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Password",
            supportingText = if (passwordFocused) "Use 8+ characters with a number or symbol for better security." else null,
            errorText = passwordError,
            onFocusChange = onPasswordFocusChange,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        painter = painterResource(R.drawable.eye_icon),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Confirm Password Field
        InputFieldWithValidation(
            label = "Repeat password",
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Password",
            errorText = confirmPasswordError,
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onConfirmPasswordVisibilityToggle) {
                    Icon(
                        painter = painterResource(R.drawable.eye_icon),
                        contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
private fun InputFieldWithValidation(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    errorText: String? = null,
    onFocusChange: ((Boolean) -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val isError = errorText != null
    val focusRequester = remember { FocusRequester() }

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
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    onFocusChange?.invoke(focusState.isFocused)
                },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.Transparent,
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else BlueBase,
                errorBorderColor = MaterialTheme.colorScheme.error,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = if (isError) MaterialTheme.colorScheme.error else BlueBase,
                errorCursorColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Supporting text or error text
        if (supportingText != null || errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            AdaptiveText(
                text = errorText ?: supportingText ?: "",
                style = MaterialTextStyle.BodySmall,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RegisterButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
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
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            AdaptiveText(
                text = "Create account",
                style = MaterialTextStyle.BodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SignInLink(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = onClick) {
            AdaptiveText(
                text = "Already have an account?",
                style = MaterialTextStyle.BodyMedium,
                color = BlueBase
            )
        }
    }
}