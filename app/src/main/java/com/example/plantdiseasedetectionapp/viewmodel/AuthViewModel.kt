package com.example.plantdiseasedetectionapp.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantdiseasedetectionapp.secrets.API_KEY_GEMINI
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authstate = MutableLiveData<AuthState>()
    val authstate : LiveData<AuthState> = _authstate


    init {
        checkAuthState()
    }

    fun checkAuthState(){
        if(auth.currentUser != null){
            _authstate.value = AuthState.authenticated
        }else{
            _authstate.value = AuthState.unauthenticated
        }
    }

    fun login(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authstate.value = AuthState.Error("Fields can't be empty")
            return
        }
        _authstate.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    _authstate.value = AuthState.authenticated
                }else{
                    _authstate.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }

    }

    fun signUp(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authstate.value = AuthState.Error("Fields can't be empty")
            return
        }
        _authstate.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    _authstate.value = AuthState.authenticated
                }else{
                    _authstate.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }

    }

    fun signOut(){
        auth.signOut()
        _authstate.value = AuthState.unauthenticated
    }

    private val _apiState = MutableLiveData<ApiState>()
    val apiState : LiveData<ApiState> = _apiState




    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = API_KEY_GEMINI
    )

    fun getImage(bitImage: Bitmap){

        _apiState.value = ApiState.Loading

        viewModelScope.launch{
            val response = generativeModel.generateContent(
                content {
                    image(bitImage)
                    text("Identify the type of disease in the given image.\n" +
                            "Give the output in the following format:-\n" +
                            "Disease Name: Clearly state the name of the disease.\n" +
                            "Causes: Mention the cause(s) of the disease, including the scientific name if applicable and ensure the text is concise.\n" +
                            "Symptoms: List the key symptoms associated with the disease. Use bullet points for clarity.\n" +
                            "Preventions: Provide a list of preventive measures to avoid or mitigate the disease. Use bullet points and ensure the text is concise. \n" +
                            "Suggested Pesticides: List around 3 pesticides that can be used to treat the disease. Use bullet points for clarity. \n"+
                            "Suggested Organic Solutions: List 3 organic solutions that can be used to treat the disease. Use bullet points for clarity."
                    )
                }

            )

            _apiState.value = ApiState.Success(response.text.toString(),bitImage)
            Log.d("xyz", response.text.toString())
        }


    }

    fun FormattedText(text: String) : String {
        val annotatedString = buildAnnotatedString {
            var startIndex = 0
            var endIndex: Int
            while (startIndex < text.length) {
                val startBold = text.indexOf("**", startIndex)
                if (startBold == -1) {
                    // Add the remaining text as normal
                    append(text.substring(startIndex))
                    break
                } else {
                    // Add the text before the bold section
                    append(text.substring(startIndex, startBold))
                    endIndex = text.indexOf("**", startBold + 2)
                    if (endIndex == -1) {
                        // If there's no matching closing asterisks, just add the rest of the text
                        append(text.substring(startBold))
                        break
                    } else {
                        // Add the bold text
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(startBold + 2, endIndex))
                        }
                        startIndex = endIndex + 2
                    }
                }
            }
        }
        return annotatedString.toString()
    }



}



sealed class AuthState{

    object authenticated : AuthState()
    object unauthenticated : AuthState()
    object Loading : AuthState()

    data class Error(val message : String) : AuthState()
}

sealed class ApiState{
    object Loading : ApiState()
    data class Success(val responseText: String, val bitImage: Bitmap) : ApiState()
    object error : ApiState()
}
