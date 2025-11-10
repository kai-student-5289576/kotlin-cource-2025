package com.example.kai_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.kai_kotlin.ui.theme.KaikotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaikotlinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "World",
                        modifier = Modifier.padding(innerPadding)
                    )
                    SurnameScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KaikotlinTheme {
        Greeting("Android")
    }
}

@Composable
fun SurnameScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.surname),
            fontSize = 28.sp,
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = R.drawable.flag),
            contentDescription = stringResource(id = R.string.flag_desc),
            modifier = Modifier
                .padding(top = 16.dp)
                .width(120.dp)
                .height(80.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.coat_of_arms),
            contentDescription = stringResource(id = R.string.coat_desc),
            modifier = Modifier
                .padding(top = 12.dp)
                .width(100.dp)
                .height(100.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSurnameScreen() {
    KaikotlinTheme {
        SurnameScreen()
    }
}