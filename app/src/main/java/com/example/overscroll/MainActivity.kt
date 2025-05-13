package com.example.overscroll

import CustomOverscrollEffect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.overscroll.ui.theme.OverscrollTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OverscrollTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListWithOverscroll(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OverscrollListPreview() {
    ListWithOverscroll()
}

@Composable
fun ListWithOverscroll(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val itemsList = (0..50).toList()
    // Create the overscroll controller with the scope and desired orientation
    val verticalOverscroll =
        remember(scope) { CustomOverscrollEffect(scope, orientation = Orientation.Vertical) }
    val verticalLazyOverscroll =
        remember(scope) { CustomOverscrollEffect(scope, orientation = Orientation.Vertical) }
    val itemModifier =
        Modifier
            .padding(4.dp)
            .border(1.dp, shape = RoundedCornerShape(8.dp), color = Color.Black)
            .padding(4.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .background(Color.LightGray)
                .weight(1f)
                .width(300.dp)
                .verticalScroll(rememberScrollState(), overscrollEffect = verticalOverscroll)
        ) {
            repeat(50) {
                Text(
                    "Item $it", modifier = itemModifier, color = Color.Black
                )
            }
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            overscrollEffect = verticalLazyOverscroll,
            modifier = Modifier
                .padding(8.dp)
                .background(Color.LightGray)
                .weight(1f)
                .width(300.dp)
        ) {
            items(itemsList) {
                Text(
                    "Item $it", color = Color.Black, modifier = itemModifier
                )
            }

        }

    }

}

