//
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.compose.ui.test.onNodeWithTag
//import androidx.compose.ui.test.performClick
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.androidapp.database.viewmodel.DayViewModel
//import com.example.androidapp.navigation.navigablescreen.ChatNotes
//import com.example.androidapp.navigation.navigablescreen.NavigableScreen
//import com.example.androidapp.sounds.ClickSoundManager
//import io.mockk.mockk
//import io.mockk.mockkObject
//import io.mockk.verify
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import kotlin.properties.Delegates
//
//@RunWith(AndroidJUnit4::class)
//class SoundOnClickTest {
//
//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<ChatNotesTestScreen>()
//
//    lateinit var upPress: () -> Unit
//
//    @Before
//    fun setup() {
//        noteForeignId = 1L // Przykładowe ID
//        mDayViewModel = mockk(relaxed = true) // Mockowanie DayViewModel
//        upPress = mockk(relaxed = true) // Mockowanie funkcji upPress
//
//        // Initialize ChatNotes with mock instances for its required arguments
//        composeTestRule.activity.setContent {
//            ChatNotes(noteForeignId, mDayViewModel, upPress)
//        }
//
//        mockkObject(ClickSoundManager) // Mockowanie obiektu zarządzającego dźwiękiem
//    }
//
//    @Test
//    fun clickIsPlayedOnSend() {
//        composeTestRule.onNodeWithTag("sendButton").performClick()
//
//        verify { ClickSoundManager.playClickSound() }
//    }
//}
//
//@Composable
//fun ChatNotesTestScreen(chatNotes: ChatNotes) {
//    SendButton(upPress = chatNotes.upPress)
//}
//
//@Composable
//fun SendButton(upPress: () -> Unit) {
//    IconButton(
//        onClick = {
//            if (noteText.isNotBlank()) {
//                noteText = ""
//                upPress()
//            }
//        },
//        modifier = Modifier.testTag("sendButton")
//    ) {
//        Icon(Icons.Default.Send, contentDescription = "Send")
//    }
//}}