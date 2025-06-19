package gr.pkcoding.notemarkapp.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Quick access extensions
@Composable
fun Int.toDp(): Dp = adaptiveValue(
    compact = this.dp,
    medium = (this * 1.2f).dp,
    expanded = (this * 1.5f).dp
)

@Composable
fun Int.toSp(): TextUnit = adaptiveValue(
    compact = this.sp,
    medium = (this * 1.15f).sp,
    expanded = (this * 1.3f).sp
)

// Grid helpers
@Composable
fun adaptiveColumns(baseColumns: Int = 1): Int = adaptiveValue(
    compact = baseColumns,
    medium = baseColumns + 1,
    expanded = baseColumns + 2
)

@Composable
fun adaptiveRows(baseRows: Int = 1): Int = adaptiveValue(
    compact = baseRows,
    medium = baseRows,
    expanded = baseRows + 1
)