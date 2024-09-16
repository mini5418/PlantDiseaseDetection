package com.example.plantdiseasedetectionapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plantdiseasedetectionapp.viewmodel.ApiState
import com.example.plantdiseasedetectionapp.viewmodel.AuthViewModel

@Composable
fun CureScreen(viewModel: AuthViewModel, navController: NavController){

    val state = viewModel.apiState.observeAsState()

    when(state.value){
        is ApiState.Loading -> Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF2EA15C))
        }
        is ApiState.Success -> {
            val successState = state.value as ApiState.Success
            val annotatedResponse : String = viewModel.FormattedText(successState.responseText)
            Column (
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(horizontal = 21.dp, vertical = 21.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(231.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center

                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        bitmap = successState.bitImage.asImageBitmap(),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(27.dp))


                Text(text = annotatedResponse)


            }
        }
        else -> Unit
    }


}