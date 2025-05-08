package com.example.overscroll

import CustomOverscrollEffect
import OffsetOverscrollEffect
import OffsetXOverscrollEffect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.withoutEventHandling
import androidx.compose.foundation.withoutVisualEffect
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.overscroll.ui.theme.OverscrollTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OverscrollTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*Greetings(
                        namesListWrapper = NamesListWrapper(),
                        modifier = Modifier.padding(innerPadding)
                    )*//* OverScrollBox(modifier = Modifier.padding(innerPadding))*/
//                    OverscrollSample(Modifier.padding(innerPadding))
                    ListWithOverscroll(Modifier.padding(innerPadding))
//                    OverscrollRenderedOnTopOfLazyListDecorations(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Immutable
data class NamesListWrapper(
    val names: List<String> = listOf(
        "Android",
        "Compose",
        "Kotlin",
        "Java",
        "OverScroll",
        "LazyColumn",
        "LazyRow",
        "LazyVerticalGrid",
        "LazyHorizontalGrid",
        "LazyVerticalStaggeredGrid",
        "LazyHorizontalStaggeredGrid",
        "LazyItems"
    )
)

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    OverscrollTheme {

//        OverscrollSample()
        ListWithOverscroll()
//        OverscrollRenderedOnTopOfLazyListDecorations()
    }
}

@Composable
fun ListWithOverscroll(
    modifier: Modifier = Modifier,
    namesListWrapper: NamesListWrapper = NamesListWrapper()
) {
    val offset = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val itemsList = (0..150).toList()
    // Create the overscroll controller
    val overscroll = remember(scope) { OffsetOverscrollEffect(scope) }
    val horizontalOverscroll = remember(scope) { CustomOverscrollEffect(
        scope = scope,
        orientation = Orientation.Horizontal
    ) }
    val verticalOverscroll = remember(scope) { CustomOverscrollEffect(scope, orientation = Orientation.Vertical) }
    val verticalLazyOverscroll = remember(scope) { CustomOverscrollEffect(scope, orientation = Orientation.Vertical) }
    val xOverscroll = remember(scope) { OffsetXOverscrollEffect(scope) }
    // let's build a scrollable that scroll until -512 to 512
    val scrollStateRange = (-512f).rangeTo(512f)
    val scrollStateRange1 = (-512f).rangeTo(512f)
    val itemModifier = Modifier
        .padding(4.dp)
        .background(
            Color.LightGray,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        )
        .border(1.dp, shape = RoundedCornerShape(8.dp), color = Color.Red)
        .padding(4.dp)
        .wrapContentSize()

    Column {
        Row(
            modifier = modifier
                .height(200.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState(), overscrollEffect = verticalOverscroll)
                    .overscroll(verticalOverscroll)

            ) {
                Text(text = "Column", fontWeight = Bold, style = TextStyle(fontSize = 15.sp))
                repeat(50) {
                    Text(
                        "Item $it",
                        modifier = itemModifier,

                        color = Color.Black
                    )
                }
            }
            LazyColumn(
                overscrollEffect = verticalLazyOverscroll,
                modifier = Modifier
                    .background(Color.Gray)
                    .padding(8.dp)
                    .weight(1f)
                    .width(200.dp)

            ) {
                item {
                    Text(
                        text = "Lazy Column",
                        fontWeight = Bold,
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
                items(namesListWrapper.names + namesListWrapper.names + namesListWrapper.names) {
                    Text(
                        "Item is $it",
                        color = Color.Black,
                        modifier = itemModifier
                    )
                }
            }

            /*LazyHorizontalGrid(
                rows = GridCells.Adaptive(50.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                overscrollEffect = horizontalOverscroll,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
                    *//*.scrollable(
                     orientation = Orientation.Horizontal,
                     state = rememberScrollableState { delta ->
                         offset.value = (offset.value + delta).coerceIn(scrollStateRange)
                         delta
                     },
                     overscrollEffect = overscroll
                 )
                 .overscroll(newOverscroll)*//*
         ) {
             item {
                 Text("Offset ${offset.value}", color = Color.Black, modifier = itemModifier.padding(4.dp))
             }
             items(itemsList) {
                 Text(
                     "Item is $it",
                     color = Color.Black,
                     modifier = itemModifier.padding(4.dp)*//*.overscroll(overscroll)*//*
                 )
             }

             item {
                 Text("Offset ${offset.value}",color = Color.Black, modifier = itemModifier.padding(4.dp))
             }
         }
         LazyVerticalGrid(
             columns = GridCells.Adaptive(50.dp),
             horizontalArrangement = Arrangement.spacedBy(6.dp),
             verticalArrangement = Arrangement.spacedBy(6.dp),
             overscrollEffect = verticalOverscroll,
             modifier = Modifier
                 .padding(5.dp)
                 .weight(1f)
                 *//*.scrollable(
                     orientation = Orientation.Vertical,
                     state = rememberScrollableState { delta ->
                         offset.value = (offset.value + delta).coerceIn(scrollStateRange)
                         delta
                     },
                     overscrollEffect = overscroll
                 )*//*
 //                .overscroll(overscroll)
         ) {
             item {
                 Text("Offset ${offset.value}", color = Color.Black, modifier = itemModifier.padding(4.dp))
             }
             items(itemsList) {
                 Text(
                     "Item is $it",
                     color = Color.Black,
                     modifier = itemModifier.padding(4.dp)*//*.overscroll(overscroll)*//*
                 )
             }

             item {
                 Text("Offset ${offset.value}",color = Color.Black, modifier = itemModifier.padding(4.dp))
             }
         }*/
        }
        Text(text = "Lazy Row", fontWeight = Bold, style = TextStyle(fontSize = 15.sp))
        LazyRow(
            overscrollEffect = horizontalOverscroll,
            modifier = Modifier
                .padding(8.dp)
        ) {

            items(namesListWrapper.names) {
                Text(text = "Item is $it", modifier = itemModifier.padding(8.dp), color = Color.Black)
            }
        }
    }

}

