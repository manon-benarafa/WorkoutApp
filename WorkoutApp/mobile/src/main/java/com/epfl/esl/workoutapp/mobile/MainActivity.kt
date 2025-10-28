package com.epfl.esl.workoutapp.mobile

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.epfl.esl.workoutapp.mobile.ui.theme.WorkoutAppTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private lateinit var dataClient: DataClient
    private var username by mutableStateOf("")
    private var profilePic by mutableStateOf<Uri?>(null)
    private var uriString by mutableStateOf("")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataClient = Wearable.getDataClient(this)

        enableEdgeToEdge()
        setContent {
            WorkoutAppTheme {
                val navController = rememberNavController()

                var shouldShowBottomMenu by remember { mutableStateOf(false) }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                Surface {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {

                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(text = stringResource(id = R.string.app_name))
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Menu,
                                                contentDescription = getString(
                                                    R.string.menu_icon_content_description
                                                )
                                            )
                                        }
                                    }
                                )
                            },

                            bottomBar = {
                                if (shouldShowBottomMenu) {
                                    NavigationBar {
                                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                                        val currentRoute = navBackStackEntry?.destination?.route

                                        NavigationBarItem(
                                            selected = currentRoute == "homePage",
                                            onClick = {
                                                navController.navigate("homePage")
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Home,
                                                    contentDescription = getString(
                                                        R.string.home_page_content_description
                                                    )
                                                )
                                            },
                                            label = { Text(getString(R.string.home_page_navigation_label)) }
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute?.startsWith("newActivity") ?: false,
                                            onClick = {
                                                navController.navigate("newActivity/$username/$uriString")
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Add,
                                                    contentDescription = getString(
                                                        R.string.new_activity_content_description
                                                    )
                                                )
                                            },
                                            label = {
                                                Text(getString(R.string.new_activity_navigation_label))
                                            }
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute == "profilePage",
                                            onClick = {
                                                navController.navigate("profilePage")
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Filled.LiveTv,
                                                    contentDescription = getString(
                                                        R.string.profile_page_content_description
                                                    )
                                                )
                                            },
                                            label = {
                                                Text(getString(R.string.profile_page_navigation_label))
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = "login",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("login") {
                                    val context = LocalContext.current
                                    LoginProfileScreen(
                                        onEnterButtonClicked = { loginInfo ->
                                            username = loginInfo.username
                                            profilePic = loginInfo.profilePic

                                            if (profilePic == null || username == "") {
                                                Toast.makeText(
                                                    context, "Pick an image and a username!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {


                                                uriString = URLEncoder.encode(
                                                    profilePic.toString(),
                                                    StandardCharsets.UTF_8.toString()
                                                )

                                                shouldShowBottomMenu = true
                                                navController.navigate("newActivity/$username/${uriString}") {
                                                    popUpTo(navController.graph.id) {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                        },
                                        dataClient
                                    )
                                }

                                composable("homePage") {

                                }

                                composable("profilePage") {

                                }
                            }

                        }
                    }
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
    WorkoutAppTheme {
        Greeting("Android")
    }
}