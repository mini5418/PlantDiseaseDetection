package com.example.plantdiseasedetectionapp.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plantdiseasedetectionapp.navGraph.Navs
import com.example.plantdiseasedetectionapp.viewmodel.AuthState
import com.example.plantdiseasedetectionapp.viewmodel.AuthViewModel

@Composable
fun SignupScreen(navController: NavController, authViewModel: AuthViewModel){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    val authstate = authViewModel.authstate.observeAsState()
    
    LaunchedEffect(authstate.value) {
        when(authstate.value){
            is AuthState.authenticated -> navController.navigate(Navs.Homescreen)
            is AuthState.Error -> Toast.makeText(context,
                (authstate.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
    

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Signup Screen", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            authViewModel.signUp(email, password)
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2EA15C),
                contentColor = Color.White
            )) {
            Text(text = "Create Account")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate(Navs.Loginscreen)
        }) {
            Text("Don't have an account? Login")
        }
    }
}