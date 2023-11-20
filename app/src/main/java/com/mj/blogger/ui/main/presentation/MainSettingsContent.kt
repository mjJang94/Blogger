package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState

@Composable
fun MainSettingsContent(state: MainContentState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        SettingTitleLabel()

        LogoutLabel(
            onClick = state.logout
        )

        ResignLabel(
            onClick = state.resign
        )
    }
}

@Composable
private fun SettingTitleLabel() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp),
        text = stringResource(R.string.setting_label),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun LogoutLabel(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 16.dp),
            text = stringResource(R.string.setting_logout),
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
        )

        Image(
            modifier = Modifier.padding(end = 16.dp),
            painter = painterResource(id = R.drawable.ic_baseline_chevron_right)
        )
    }
}

@Composable
private fun ResignLabel(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 16.dp),
            text = stringResource(R.string.setting_resign),
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
        )

        Image(
            modifier = Modifier.padding(end = 16.dp),
            painter = painterResource(id = R.drawable.ic_baseline_chevron_right)
        )
    }
}


@Composable
@Preview
private fun MainSettingsContentPreview() {
    BloggerTheme {
        MainSettingsContent(
            state = rememberPreviewMainContentState(),
        )
    }
}