package cat.copernic.p3grup1.entrebicis.user_management.presentation.screen

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.core.enums.Rol
import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.user_management.presentation.components.PerfilImage
import cat.copernic.p3grup1.entrebicis.user_management.presentation.components.ProfileTextField
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.UserProfileViewModel
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.provideUserProfileViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserProfileScreen(
    onBack: () -> Unit
){
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val viewModel: UserProfileViewModel = viewModel(
        factory = provideUserProfileViewModelFactory(context.applicationContext as Application)
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            viewModel.setImatgeBase64(base64)
            Log.d("IMATGE_ANDROID", "Imatge seleccionada - Base64 length: ${base64.length}")
            Log.d("IMATGE_ANDROID", "Primeros 100 carácteres: ${base64.take(100)}")
        }
    }

    val usuari by viewModel.usuari.collectAsState()
    var editMode by remember { mutableStateOf(false) }

    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobil by remember { mutableStateOf("") }
    var poblacio by remember { mutableStateOf("") }
    val imatgeBase64 by viewModel.imatgeBase64.collectAsState()
    val usuariOriginal = remember { mutableStateOf<Usuari?>(null) }

    val actualitzacioExitosa by viewModel.actualitzacioExitosa.collectAsState()
    val errorActualitzacio by viewModel.errorActualitzacio.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.carregarUsuari()
    }

    LaunchedEffect(usuari) {
        usuari?.let {
            nom = it.nom
            email = it.email
            mobil = it.mobil
            poblacio = it.poblacio
            usuariOriginal.value = it.copy()
        }
    }

    LaunchedEffect(actualitzacioExitosa) {
        actualitzacioExitosa?.let { exit ->
            if (exit) {
                snackbarHostState.showSnackbar("✅ Canvis desats correctament")
            } else {
                snackbarHostState.showSnackbar("❌ Error al desar: ${errorActualitzacio ?: "Desconegut"}")
                // 🔁 Restaurar campos
                usuariOriginal.value?.let {
                    nom = it.nom
                    email = it.email
                    mobil = it.mobil
                    poblacio = it.poblacio
                    viewModel.setImatgeBase64(it.imatge) // opcional: restaurar imagen
                }
            }
            viewModel.resetActualitzacioFlags()
        }
    }

Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) }
)
{ paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
        // CABECERA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .height(184.dp),
            contentAlignment = Alignment.Center
        ) {

            // Botón de atrás
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Tornar enrere",
                    tint = Color.White
                )
            }

            Text(
                "El teu perfil",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "El teu saldo és de: ${usuari?.saldo ?: "..."} punts",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Imagen perfil desde Base64
            PerfilImage(base64 = imatgeBase64 ?: usuari?.imatge)

            Spacer(modifier = Modifier.height(8.dp))

            if (editMode) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("SELECCIONAR FOTO", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            ProfileTextField(nom, { nom = it }, enabled = editMode)
            ProfileTextField(email, {}, enabled = false)
            ProfileTextField(mobil, { mobil = it }, enabled = editMode)
            ProfileTextField(poblacio, { poblacio = it }, enabled = editMode)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (editMode) {
                        val imatgeFinal = viewModel.imatgeBase64.value ?: usuari?.imatge ?: ""
                        val usuariActualitzat = Usuari(
                            email,
                            "",
                            nom,
                            usuari?.rol ?: Rol.CICLISTA,
                            imatgeFinal,
                            usuari?.saldo ?: 0.0,
                            usuari?.observacions ?: "",
                            mobil,
                            poblacio
                        )
                        Log.d("IMATGE_ANDROID", "Enviant usuari amb imatge: ${imatgeFinal.take(100)}")
                        viewModel.actualitzarUsuari(usuariActualitzat)
                    }
                    editMode = !editMode
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(42.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    if (editMode) "GUARDAR" else "EDITAR",
                    color = Color.White
                )
            }
        }
    }
}
}