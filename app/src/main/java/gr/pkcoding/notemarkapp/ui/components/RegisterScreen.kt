package gr.pkcoding.notemarkapp.features.auth.ui.register

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import gr.pkcoding.notemarkapp.R
import gr.pkcoding.notemarkapp.ui.adaptive.AdaptiveText
import gr.pkcoding.notemarkapp.ui.adaptive.MaterialTextStyle
import gr.pkcoding.notemarkapp.ui.adaptive.adaptiveValue
import gr.pkcoding.notemarkapp.ui.theme.BlueBase
import gr.pkcoding.notemarkapp.ui.theme.LightBlue
import gr.pkcoding.notemarkapp.ui.theme.SurfaceLowest
import gr.pkcoding.notemarkapp.ui.theme.NoteMarkAppTheme
import gr.pkcoding.notemarkapp.features.auth.ui.viewmodel.AuthViewModel
import gr.pkcoding.notemarkapp.features.auth.ui.viewmodel.RegisterFormData
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthIntent
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthState
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthFields
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthEffect
import gr.pkcoding.notemarkapp.feature.auth.presentation.model.isLoading
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    onSignInClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onShowSnackbar: (message: String, isError: Boolean) -> Unit = { _, _ -> },
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Collect states
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Get current form data
    val formData = viewModel.getRegisterFormData()

    // Handle effects (navigation, snackbars, etc.)
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AuthEffect.NavigateToMain -> {
                    onRegisterSuccess()
                }
                is AuthEffect.NavigateToLogin -> {
                    onSignInClick()
                }
                is AuthEffect.ShowSnackbar -> {
                    onShowSnackbar(effect.message, effect.isError)
                }
                is AuthEffect.ShowSuccessMessage -> {
                    onShowSnackbar(effect.message, false)
                }
                else -> {
                    // Handle other effects if needed
                }
            }
        }
    }

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
                formData = formData,
                uiState = uiState,
                onFieldChange = { field, value ->
                    viewModel.processIntent(AuthIntent.UpdateField(field, value))
                },
                onPasswordVisibilityToggle = { field ->
                    viewModel.processIntent(AuthIntent.TogglePasswordVisibility(field))
                },
                onFieldFocusChange = { field, hasFocus ->
                    viewModel.onFieldFocusChanged(field, hasFocus)
                }
            )

            Spacer(modifier = Modifier.height(adaptiveValue(32.dp, 40.dp, 48.dp)))

            // Register Button
            RegisterButton(
                enabled = isFormValid(formData) && uiState.fieldsEnabled,
                isLoading = state.isLoading,
                onClick = {
                    viewModel.processIntent(
                        AuthIntent.Register(
                            username = formData.username,
                            email = formData.email,
                            password = formData.password,
                            confirmPassword = formData.confirmPassword
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(adaptiveValue(24.dp, 28.dp, 32.dp)))

            // Sign In Link
            SignInLink(
                onClick = {
                    viewModel.processIntent(AuthIntent.NavigateToLogin)
                }
            )
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
    formData: RegisterFormData,
    uiState: gr.pkcoding.notemarkapp.feature.auth.presentation.model.AuthUiState,
    onFieldChange: (String, String) -> Unit,
    onPasswordVisibilityToggle: (String) -> Unit,
    onFieldFocusChange: (String, Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(adaptiveValue(20.dp, 24.dp, 28.dp))
    ) {
        // Username Field
        InputFieldWithValidation(
            label = "Username",
            value = formData.username,
            onValueChange = { onFieldChange(AuthFields.USERNAME, it) },
            placeholder = "John.doe",
            supportingText = if (uiState.focusedField == AuthFields.USERNAME)
                "Use between 3 and 20 characters for your username." else null,
            errorText = uiState.getFieldError(AuthFields.USERNAME),
            onFocusChange = { hasFocus -> onFieldFocusChange(AuthFields.USERNAME, hasFocus) },
            enabled = uiState.fieldsEnabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        // Email Field
        InputFieldWithValidation(
            label = "Email",
            value = formData.email,
            onValueChange = { onFieldChange(AuthFields.EMAIL, it) },
            placeholder = "john.doe@example.com",
            errorText = uiState.getFieldError(AuthFields.EMAIL),
            onFocusChange = { hasFocus -> onFieldFocusChange(AuthFields.EMAIL, hasFocus) },
            enabled = uiState.fieldsEnabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Password Field
        InputFieldWithValidation(
            label = "Password",
            value = formData.password,
            onValueChange = { onFieldChange(AuthFields.PASSWORD, it) },
            placeholder = "Password",
            supportingText = if (uiState.focusedField == AuthFields.PASSWORD)
                "Use 8+ characters with a number or symbol for better security." else null,
            errorText = uiState.getFieldError(AuthFields.PASSWORD),
            onFocusChange = { hasFocus -> onFieldFocusChange(AuthFields.PASSWORD, hasFocus) },
            enabled = uiState.fieldsEnabled,
            visualTransformation = if (uiState.isPasswordVisible(AuthFields.PASSWORD))
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityToggle(AuthFields.PASSWORD) }) {
                    Icon(
                        painter = painterResource(R.drawable.eye_icon),
                        contentDescription = if (uiState.isPasswordVisible(AuthFields.PASSWORD))
                            "Hide password" else "Show password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Confirm Password Field
        InputFieldWithValidation(
            label = "Repeat password",
            value = formData.confirmPassword,
            onValueChange = { onFieldChange(AuthFields.CONFIRM_PASSWORD, it) },
            placeholder = "Password",
            errorText = uiState.getFieldError(AuthFields.CONFIRM_PASSWORD),
            onFocusChange = { hasFocus -> onFieldFocusChange(AuthFields.CONFIRM_PASSWORD, hasFocus) },
            enabled = uiState.fieldsEnabled,
            visualTransformation = if (uiState.isPasswordVisible(AuthFields.CONFIRM_PASSWORD))
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityToggle(AuthFields.CONFIRM_PASSWORD) }) {
                    Icon(
                        painter = painterResource(R.drawable.eye_icon),
                        contentDescription = if (uiState.isPasswordVisible(AuthFields.CONFIRM_PASSWORD))
                            "Hide password" else "Show password"
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
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val isError = errorText != null

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
                .onFocusChanged { focusState ->
                    onFocusChange?.invoke(focusState.isFocused)
                },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            enabled = enabled,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.Transparent,
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else BlueBase,
                errorBorderColor = MaterialTheme.colorScheme.error,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = if (isError) MaterialTheme.colorScheme.error else BlueBase,
                errorCursorColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
        enabled = enabled && !isLoading,
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

/**
 * Form validation logic
 */
private fun isFormValid(formData: RegisterFormData): Boolean {
    return formData.username.length in 3..20 &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(formData.email).matches() &&
            formData.password.length >= 8 &&
            formData.password.any { it.isDigit() || !it.isLetterOrDigit() } &&
            formData.password == formData.confirmPassword
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    NoteMarkAppTheme {
        RegisterScreen()
    }
}