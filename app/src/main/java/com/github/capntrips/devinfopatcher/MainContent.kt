package com.github.capntrips.devinfopatcher

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.capntrips.devinfopatcher.ui.theme.DevinfoPatcherTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainContent(viewModel: MainViewModelInterface) {
    val uiState by viewModel.uiState.collectAsState()
    Column {
        DataCard (title = stringResource(R.string.device)) {
            DataRow(
                label = stringResource(R.string.model),
                value = "${Build.MODEL} (${Build.DEVICE})"
            )
            DataRow(
                label = stringResource(R.string.build_number),
                value = Build.ID
            )
            DataRow(
                label = stringResource(R.string.slot_suffix),
                value = uiState.slotSuffix
            )
        }
        Spacer(Modifier.height(16.dp))
        SlotCard(
            title = stringResource(R.string.slot_a),
            slotStateFlow = uiState.slotA,
            isActive = uiState.slotSuffix == "_a"
        )
        Spacer(Modifier.height(16.dp))
        SlotCard(
            title = stringResource(R.string.slot_b),
            slotStateFlow = uiState.slotB,
            isActive = uiState.slotSuffix == "_b"
        )
    }
}

@Composable
fun SlotCard(
    title: String,
    slotStateFlow: StateFlow<SlotStateInterface>,
    isActive: Boolean
) {
    // TODO: hoist state?
    val slot by slotStateFlow.collectAsState()
    val isRefreshing by slot.isRefreshing.collectAsState()
    DataCard (
        title = title,
        button = {
            if (!isRefreshing) {
                if (!isActive) {
                    if (!slot.successful) {
                        PatchButton(slot)
                    }
                }
            }
        }
    ) {
        HasStatusDataRow(
            label = stringResource(R.string.retry_count),
            value = slot.retryCount.toString(),
            hasStatus = slot.retryCount > 0
        )
        HasStatusDataRow(
            label = stringResource(R.string.unbootable),
            value = stringResource(if (slot.unbootable) R.string.yes else R.string.no),
            hasStatus = !slot.unbootable
        )
        HasStatusDataRow(
            label = stringResource(R.string.successful),
            value = stringResource(if (slot.successful) R.string.yes else R.string.no),
            hasStatus = slot.successful
        )
        HasStatusDataRow(
            label = stringResource(R.string.active),
            value = stringResource(if (slot.active) R.string.yes else R.string.no),
            hasStatus = if (isActive) slot.active else !slot.active
        )
        DataRow(
            label = stringResource(R.string.fastboot_ok),
            value = stringResource(if (slot.fastbootOk) R.string.yes else R.string.no)
        )
    }
}

@Composable
fun PatchButton(slot: SlotStateInterface) {
    val context = LocalContext.current
    Button(
        modifier = Modifier.padding(0.dp),
        shape = RoundedCornerShape(4.0.dp),
        onClick = { slot.patch(context) }
    ) {
        Text(stringResource(R.string.patch))
    }
}

@Composable
fun DataCard(
    title: String,
    button: @Composable (() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(0.dp, 9.dp, 8.dp, 9.dp),
                text = title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            if (button != null) {
                button()
            }
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

// TODO: Remove when card is supported in material3: https://m3.material.io/components/cards/implementation/android
@Composable
fun Card(
    shape: Shape = RoundedCornerShape(4.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    border: BorderStroke? = null,
    tonalElevation: Dp = 2.dp,
    shadowElevation: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp, (13.788).dp, 18.dp, 18.dp),
            content = content
        )
    }
}

@Composable
fun DataRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    valueStyle: TextStyle = MaterialTheme.typography.titleSmall
) {
    Row {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.width(8.dp))
        SelectionContainer(Modifier.alignByBaseline()) {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = value,
                color = valueColor,
                style = valueStyle
            )
        }
    }
}

@Composable
fun HasStatusDataRow(
    label: String,
    value: String,
    hasStatus: Boolean
) {
    DataRow(
        label = label,
        value = value,
        valueColor = if (hasStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    )
}

@ExperimentalMaterial3Api
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainContentPreviewDark() {
    MainContentPreviewLight()
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun MainContentPreviewLight() {
    DevinfoPatcherTheme {
        Scaffold {
            val viewModel: MainViewModelPreview = viewModel()
            MainContent(viewModel)
        }
    }
}
