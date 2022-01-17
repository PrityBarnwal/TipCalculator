package com.coolapps.tipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coolapps.tipapp.ui.theme.TipAppTheme
import com.coolapps.tipapp.util.calculateTotalPerson
import com.coolapps.tipapp.util.calculateTotalTip
import com.coolapps.tipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp {
                  //  HeaderTop()
                    MainContent()
                }
            }
        }
    }

    @Composable
   fun MyApp(content : @Composable () -> Unit) {
        TipAppTheme {
            Surface(color = MaterialTheme.colors.background) {
                content()
            }
        }
    }
@Preview
@Composable
fun TopHeader(totalPerPers : Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
    color = Color(0xFE7B65AF)
    ){
        Column(
            modifier = Modifier.padding(12.dp)
                ,horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total ="%.2f".format(totalPerPers)
            Text("Total Bill",
            style = MaterialTheme.typography.h5)
            Text(text = "$$total",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent(){
    val splitByState = remember {
        mutableStateOf(1)
    }
  //  val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm(
            splitByState = splitByState,
            tipAmountState = tipAmountState,
          //  range = range,
            totalPerPersonState = totalPerPersonState) {}

    }

}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState : MutableState<Int>,
    tipAmountState : MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValueChange: (String) -> Unit = {},
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()


    TopHeader(totalPerPers = totalPerPersonState.value)


    Surface(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(valueState = totalBillState,
                labelId = "Enter Amount",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())

                    keyboardController?.hide()
                }
            )
            if (validState) {
                Row(
                    modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1 else 1
                                totalPerPersonState.value =
                                    calculateTotalPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )

                            })

                        Text(
                            text = "${splitByState.value}",
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value = splitByState.value + 1

                                    totalPerPersonState.value =
                                        calculateTotalPerson(
                                            totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitByState.value,
                                            tipPercentage = tipPercentage
                                        )
                                }
                            })
                    }
                }

                Row(modifier = modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$ ${tipAmountState.value}",
                        Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            Log.d("Slider", "BillForm: $newVal")
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )
                            totalPerPersonState.value =
                                calculateTotalPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )

                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5,
                        onValueChangeFinished = {

                        })
                }
            } else {
                Box() {

                }
            }
        }
    }

}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipAppTheme {
        MyApp {
            Text(text = "hello again")
        }
    }
}


