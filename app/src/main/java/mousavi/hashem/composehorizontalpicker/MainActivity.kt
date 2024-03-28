package mousavi.hashem.composehorizontalpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mousavi.hashem.composehorizontalpicker.ui.theme.ComposeHorizontalPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeHorizontalPickerTheme {
                MainScreen()
            }
        }
    }
}
