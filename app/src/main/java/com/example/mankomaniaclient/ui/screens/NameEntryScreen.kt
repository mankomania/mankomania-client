package com.example.mankomaniaclient.ui.screens

// Layout & Design
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke

// Material 3 Komponenten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text

// Compose Basics
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.example.mankomaniaclient.R
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.mankomaniaclient.RulesActivity


@Composable
fun NameEntryScreen(
    playerName: String,
    onNameChange: (String) -> Unit,
    onCreateLobby: (String) -> Unit,
    onJoinLobby: (String) -> Unit
) {
    val isNameValid = playerName.isNotBlank()
    var nameConfirmed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_money),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your name",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            OutlinedTextField(
                value = playerName,
                onValueChange = onNameChange,
                placeholder = { Text("Player name") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier
                    .width(250.dp)
                    .padding(bottom = 24.dp),
                keyboardActions = KeyboardActions(
                    onDone = { nameConfirmed = true }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            val context = LocalContext.current

            StyledButton(
                text = "Rules",
                onClick = {
                    val intent = Intent(context, RulesActivity::class.java)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (nameConfirmed) {
                StyledButton(text = "Create Lobby", onClick = { onCreateLobby(playerName) })
                Spacer(modifier = Modifier.height(12.dp))
                StyledButton(text = "Join Lobby", onClick = { onJoinLobby(playerName) })
            }
        }
    }
}


    @Composable
    fun StyledButton(
        text: String,
        onClick: () -> Unit,
        enabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color(0xFF0E1512)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE7C77B),
                contentColor = Color.Black,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.DarkGray
            ),
            modifier = Modifier
                .widthIn(min = 180.dp)
                .height(48.dp)
        ) {
            Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }

