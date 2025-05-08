package cat.copernic.p3grup1.entrebicis.user_management.presentation.screen

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.LoginViewModel
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.provideLoginViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = provideLoginViewModelFactory(context.applicationContext as Application)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val navController = rememberNavController()
    val savedStateHandle = remember {
        navController.currentBackStackEntry?.savedStateHandle
    }

    val successMessage = savedStateHandle?.get<String>("successMessage")
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val scale = remember { Animatable(1f) }
    var isPressed by remember { mutableStateOf(false) }

    val brushOffset = remember { Animatable(0f) }
    val brushAlpha = remember { Animatable(1f) }



    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFA8E6A3).copy(alpha = brushAlpha.value),
            Color(0xFF4EA72B).copy(alpha = brushAlpha.value)
        ),
        start = Offset(brushOffset.value, 0f),
        end = Offset(brushOffset.value + 200f, 100f)
    )

    val backgroundModifier = if (isPressed) {
        Modifier.background(brush, shape = RoundedCornerShape(8.dp))
    } else {
        Modifier.background(Color.Transparent, shape = RoundedCornerShape(8.dp))
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            brushOffset.snapTo(0f)
            brushAlpha.snapTo(1f)

            brushOffset.animateTo(
                targetValue = 600f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )

            brushAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300)
            )

            isPressed = false
        }
    }

    // Si login es correcto, navega
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar("Contrasenya canviada correctament")
            savedStateHandle.remove<String>("successMessage")
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = Color.White,
                        contentColor = Color.Red
                    ) {
                        Text(
                            text = data.visuals.message,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_white_small),
                contentDescription = "Logo Entrebicis",
                modifier = Modifier
                    .width(287.dp)
                    .height(141.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "LOGIN",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(84.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("EMAIL") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                textStyle = MaterialTheme.typography.labelMedium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                textStyle = MaterialTheme.typography.labelMedium,
                placeholder = { Text("**************") },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Alternar visualització contrasenya"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Contrasenya oblidada?",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onForgotPassword() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        // Animación de pulsación
                        scale.animateTo(
                            targetValue = 0.95f,
                            animationSpec = tween(100)
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(100)
                        )
                    }
                    when {
                        email.isBlank() || password.isBlank() -> {
                            viewModel.clearError()
                            scope.launch {
                                snackbarHostState.showSnackbar("Els camps no poden estar buits")
                            }
                        }

                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            viewModel.clearError()
                            scope.launch {
                                snackbarHostState.showSnackbar("El correu no té un format vàlid")
                            }
                        }

                        else -> {
                            viewModel.clearError()
                            viewModel.login(email, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .pointerInteropFilter {
                        when (it.action) {
                            android.view.MotionEvent.ACTION_DOWN -> isPressed = true
                            android.view.MotionEvent.ACTION_UP,
                            android.view.MotionEvent.ACTION_CANCEL -> isPressed = false
                        }
                        false
                    },
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = backgroundModifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "INICIAR SESSIÓ",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}