//package com.audit.pass.app.utils
//
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//
//
//@Composable
//fun SampleAlertDialog(
//    title: String,
//    content: String,
//    cancelText: String = "取消",
//    confirmText: String = "继续",
//    onConfirmClick: () -> Unit,
//    //onCancelClick: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    AlertDialog(
//        title = {
//            Text(text = title)
//        },
//        text = {
//            Text(text = content)
//        },
//        onDismissRequest = onDismiss,
//        confirmButton = {
//            TextButton(onClick = {
//                onConfirmClick.invoke()
//                onDismiss.invoke()
//            }) {
//                Text(text = confirmText, color = Color.Black)
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = { onDismiss.invoke() }) {
//                Text(text = cancelText)
//            }
//        },
//        modifier = Modifier
//            .padding(horizontal = 30.dp)
//            .fillMaxWidth()
//            .wrapContentHeight()
//    )
//}
