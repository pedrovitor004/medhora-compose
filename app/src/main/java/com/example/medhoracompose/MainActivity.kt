package com.example.medhoracompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.medhoracompose.navigation.MedHoraNavHost
import com.example.medhoracompose.ui.theme.MedHoraComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedHoraComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val app = application as MedHoraApplication
                    val carregado by app.carregado.collectAsState()
                    val erro = app.erroCarregamento
                    if (erro != null || !carregado) {
                        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                            if (erro != null) Text(erro) else CircularProgressIndicator()
                        }
                    } else {
                        MedHoraNavHost(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
