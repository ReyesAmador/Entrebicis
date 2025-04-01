package cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.recovery_password

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.PasswordRecoveryViewModel
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.passwordRecoveryViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun ValidateCodeScreen(
    email: String,
    onCodeValidated: (String) -> Unit
){
    val context = LocalContext.current
    val viewModel: PasswordRecoveryViewModel = viewModel(
        factory = passwordRecoveryViewModelFactory(context.applicationContext as Application)
    )

    val code by viewModel.code.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val success by viewModel.success.collectAsState()

    LaunchedEffect(email) {
        viewModel.updateEmail(email)
    }

    LaunchedEffect(success) {
        if (success) {
            onCodeValidated(viewModel.code.value)
            viewModel.clearState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_white),
            contentDescription = "Logo Entrebicis",
            modifier = Modifier.size(220.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "VALIDAR\nCODI",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { viewModel.updateCode(it) },
            placeholder = { Text("CODI DE RECUPERACIÓ") },
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

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { viewModel.validateCode() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(2.dp, Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "VALIDA",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(error ?: "", color = Color.Red, fontSize = 14.sp)
        }
    }
}