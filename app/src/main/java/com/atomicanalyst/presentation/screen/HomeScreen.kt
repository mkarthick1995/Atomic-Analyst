package com.atomicanalyst.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.atomicanalyst.R

@Suppress("FunctionNaming")
@Composable
fun HomeScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(text = stringResource(R.string.app_name))
    }
}
