package com.epfl.esl.workoutapp.mobile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.esl.workoutapp.mobile.ui.theme.WorkoutAppTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable

@Composable
fun LoginProfileScreen(
    onEnterButtonClicked: ((LoginInfo)) -> Unit,
    dataClient: DataClient,
    modifier: Modifier = Modifier,
    loginProfileViewModel: LoginProfileViewModel = viewModel()
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val context = LocalContext.current

        val username by loginProfileViewModel.username
            .observeAsState(initial="")
        val password by loginProfileViewModel.password
            .observeAsState(initial="")
        val profilePic by loginProfileViewModel.profilePic
            .observeAsState(initial=null)

        var isEditingMode by remember { mutableStateOf(true) }

        val resultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
//                    imageUri = uri
                    loginProfileViewModel.updateProfilePic(uri)
                }
            }
        )

        if (isEditingMode) {
            LoginProfileContentEditing(
                username = username,
                password = password,
                onUsernameChange = { newUsername ->
                    loginProfileViewModel.updateUsername(newUsername)
                },
                onPasswordChange = { newPassword ->
                    loginProfileViewModel.updatePassword(newPassword)
                },
                onContinueButtonClicked = { isEditingMode = false},
                onPickImageButtonClicked = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    resultLauncher.launch(intent)
                },
                modifier = modifier,
                profilePic = profilePic,
            )
        } else {
            LoginProfileContentDisplaying(
                username = username,
                onUpdateButtonClicked = {
                    isEditingMode = true
                },
                onLogOutButtonClicked = {
                    loginProfileViewModel.updateUsername("")
                    loginProfileViewModel.updatePassword("")
                    loginProfileViewModel.updateProfilePic(null)
                    isEditingMode = true
                },
                onEnterButtonClicked = { loginInfo ->
                    loginProfileViewModel
                        .sendDataToWear(context.applicationContext, dataClient)
                    onEnterButtonClicked(loginInfo)
                },
                modifier = modifier,
                profilePic = profilePic
            )
        }
    }
}

@Composable
fun LoginProfileContentEditing(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onContinueButtonClicked: () -> Unit,
    onPickImageButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    profilePic: Uri? = Uri.EMPTY,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    }
}

@Composable
fun LoginProfileContentDisplaying(
    username: String,
    onUpdateButtonClicked: () -> Unit,
    onLogOutButtonClicked: () -> Unit,
    onEnterButtonClicked: ((LoginInfo)) -> Unit,
    modifier: Modifier = Modifier,
    profilePic: Uri? = Uri.EMPTY,
) {

}

@Preview
@Composable
fun LoginProfileScreenPreview() {
    WorkoutAppTheme {
        val context = LocalContext.current
        val dataClient = Wearable.getDataClient(context)
        LoginProfileScreen(onEnterButtonClicked = {}, dataClient = dataClient)
    }
}