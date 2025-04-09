package cat.copernic.p3grup1.entrebicis.user_management.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.core.theme.Secondary
import cat.copernic.p3grup1.entrebicis.core.theme.Tertiary

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled : Boolean = false,
    modifier: Modifier = Modifier
){
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Tertiary.copy(alpha = 0.5f),
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Primary,
                focusedTextColor = Secondary,
                unfocusedTextColor = Secondary,
                disabledTextColor = Secondary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}