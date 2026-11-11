package com.lecteur.iptv.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.lecteur.iptv.data.ServerConfig

@Composable
fun SetupScreen(
    initial: ServerConfig?,
    error: String?,
    onSave: (ServerConfig) -> Unit,
    onCancel: (() -> Unit)?,
    onLogout: (() -> Unit)?,
) {
    var url by remember { mutableStateOf(initial?.url ?: "") }
    var user by remember { mutableStateOf(initial?.username ?: "") }
    var pass by remember { mutableStateOf(initial?.password ?: "") }

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = 520.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Lecteur IPTV", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Text("Saisissez l'adresse de votre serveur", style = MaterialTheme.typography.bodyLarge)

            OutlinedTextField(
                value = url, onValueChange = { url = it }, singleLine = true,
                label = { Text("Adresse du serveur / lien M3U (http://...)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = user, onValueChange = { user = it }, singleLine = true,
                label = { Text("Utilisateur (optionnel)") }, modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = pass, onValueChange = { pass = it }, singleLine = true,
                label = { Text("Mot de passe (optionnel)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error)

            Button(
                onClick = { onSave(ServerConfig(url.trim(), user.trim(), pass.trim())) },
                enabled = url.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Se connecter") }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onCancel != null) OutlinedButton(onClick = onCancel) { Text("Annuler") }
                if (onLogout != null) OutlinedButton(onClick = onLogout) { Text("Supprimer le serveur") }
            }
        }
    }
}
