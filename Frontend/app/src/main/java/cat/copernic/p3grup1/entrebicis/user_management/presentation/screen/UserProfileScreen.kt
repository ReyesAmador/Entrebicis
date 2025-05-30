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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserProfileScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val viewModel: UserProfileViewModel = viewModel(
        factory = provideUserProfileViewModelFactory(context.applicationContext as Application)
    )
    var novaImatge by remember { mutableStateOf<ByteArray?>(null) }
    val cacheKey = remember { mutableStateOf(System.currentTimeMillis()) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            if (bytes != null) {
                novaImatge = bytes
                cacheKey.value = System.currentTimeMillis()
                Log.d("IMATGE_ANDROID", "✅ Imatge enviada (${bytes.size} bytes)")
            }

        }
    }

    val bitmapSeleccionada = remember(novaImatge) {
        novaImatge?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }

    val usuari by viewModel.usuari.collectAsState()
    val missatgeContrasenya by viewModel.missatgeContrasenya.collectAsState()
    var editMode by remember { mutableStateOf(false) }

    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobil by remember { mutableStateOf("") }
    var poblacio by remember { mutableStateOf("") }
    var nomError by remember { mutableStateOf<String?>(null) }
    var mobilError by remember { mutableStateOf<String?>(null) }
    var poblacioError by remember { mutableStateOf<String?>(null) }

    val actualitzacioExitosa by viewModel.actualitzacioExitosa.collectAsState()
    val errorActualitzacio by viewModel.errorActualitzacio.collectAsState()
    val uploadExitosa by viewModel.uploadExitosa.collectAsState()
    val errorConnexio by viewModel.errorConnexio.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val token = viewModel.getToken()

    var contrasenyaActual by remember { mutableStateOf("") }
    var novaContrasenya by remember { mutableStateOf("") }
    var repetirContrasenya by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showCurrentPassword by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.carregarUsuari()
    }

    LaunchedEffect(uploadExitosa) {
        if (uploadExitosa) {
            cacheKey.value = System.currentTimeMillis()
            viewModel.resetUploadExitosa()
        }
    }

    LaunchedEffect(errorConnexio) {
        errorConnexio?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorConnexio()
        }
    }

    LaunchedEffect(usuari) {
        usuari?.let {
            nom = it.nom
            email = it.email
            mobil = it.mobil
            poblacio = it.poblacio
        }
    }

    LaunchedEffect(actualitzacioExitosa) {
        actualitzacioExitosa?.let { exit ->
            if (exit) {
                editMode = false
                snackbarHostState.showSnackbar("✅ Canvis desats correctament")
            } else {
                snackbarHostState.showSnackbar("❌ Error al desar: ${errorActualitzacio ?: "Desconegut"}")
            }
            viewModel.resetActualitzacioFlags()
        }
    }

    LaunchedEffect(missatgeContrasenya) {
        missatgeContrasenya?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.resetMissatgeContrasenya()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding()
        ) {
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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.logo_white_small),
                        contentDescription = "Logo Entrebicis",
                        modifier = Modifier
                            .width(170.dp)
                            .height(82.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "El teu perfil",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "El teu saldo és de: ${usuari?.saldo ?: "..."} punts",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (bitmapSeleccionada != null) {
                    Image(
                        bitmap = bitmapSeleccionada.asImageBitmap(),
                        contentDescription = "Nova imatge seleccionada",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                }else{
                    // Imagen por coil
                    PerfilImage(
                        imageUrl = "https://entrebicis.ddns.net:8443/api/usuari/imatge?ts=${cacheKey.value}",
                        token = token ?: ""
                    )
                }

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

                ProfileTextField(nom, { nom = it; nomError = null },
                    enabled = editMode, isError = nomError != null, errorMessage = nomError)
                ProfileTextField(email, {}, enabled = false)
                ProfileTextField(mobil, { mobil = it; mobilError= null },
                    enabled = editMode, isError = mobilError != null, errorMessage = mobilError)
                ProfileTextField(poblacio, { poblacio = it; poblacioError = null },
                    enabled = editMode, isError = poblacioError != null, errorMessage = poblacioError)
                ProfileTextField(
                    value = contrasenyaActual,
                    onValueChange = { contrasenyaActual = it },
                    enabled = editMode,
                    modifier = Modifier,
                    visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                            Icon(
                                imageVector = if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )
                ProfileTextField(
                    value = novaContrasenya,
                    onValueChange = { novaContrasenya = it },
                    enabled = contrasenyaActual.isNotBlank(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                ProfileTextField(
                    value = repetirContrasenya,
                    onValueChange = { repetirContrasenya = it },
                    enabled = contrasenyaActual.isNotBlank(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                fun validarCamps(): Boolean {
                    var valid = true

                    //NOM
                    if(nom.isBlank()){
                        nomError = "El nom no pot estar en blanc"
                        valid = false
                    }else if (nom.length > 60){
                        valid = false
                        nomError = "El nom no pot tenir més de 60 caràcters"
                    }else if(!Regex("^[a-zA-ZÀ-ÿ\\s]*$").matches(nom.trim())){
                        valid = false
                        nomError = "El nom només pot contenir lletres i espais"
                    }else{
                        nomError = null
                    }
                    //MOBIL
                    if(mobil.isBlank()){
                        mobilError = "El mòbil no pot estar en blanc"
                        valid = false
                    }else if (!Regex("^\\+?[0-9]{9,15}$").matches(mobil.trim())) {
                        valid = false
                        mobilError = "El mòbil ha de tenir entre 9 i 15 dígits vàlids"
                    }else{
                        mobilError = null
                    }
                    //POBLACIÓ
                    if(poblacio.isBlank()){
                        poblacioError = "La població no pot estar en blanc"
                        valid = false
                    }else if (poblacio.length > 60) {
                        valid = false
                        poblacioError = "La població no pot tenir més de 60 caràcters"
                    }else if(!Regex("^[a-zA-ZÀ-ÿ\\s]*$").matches(poblacio.trim())){
                        valid = false
                        poblacioError = "La població només pot contenir lletres i espais"
                    }else{
                        poblacioError = null
                    }

                    return valid
                }

                Button(
                    onClick = {
                        if (editMode) {
                            val valid = validarCamps()
                            if (!valid) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("❌ Revisa els camps amb error")
                                }
                                return@Button // <- evita continuar si hay error
                            }


                            val usuariActualitzat = Usuari(
                                email,
                                "",
                                nom,
                                usuari?.rol ?: Rol.CICLISTA,
                                "",
                                usuari?.saldo ?: 0.0,
                                usuari?.observacions ?: "",
                                mobil,
                                poblacio
                            )

                            coroutineScope.launch {
                                if (novaImatge != null) {
                                    val success = viewModel.uploadImatgeUsuari(novaImatge!!)
                                    if (success) {
                                        cacheKey.value = System.currentTimeMillis()
                                        novaImatge = null
                                    }
                                }

                                viewModel.actualitzarUsuari(usuariActualitzat)

                                if (contrasenyaActual.isNotBlank()) {
                                    viewModel.canviContrasenya(contrasenyaActual, novaContrasenya, repetirContrasenya)
                                }
                            }
                        }else{
                            editMode = true
                        }
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