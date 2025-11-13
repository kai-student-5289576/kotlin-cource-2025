package com.example.kai_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kai_kotlin.ui.theme.KaikotlinTheme
import kotlinx.coroutines.delay

// Simple data model for menu entries / list items
data class MenuEntry(val id: Int, var title: String, var styleFlags: Int = 0)

class Lab8Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaikotlinTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Lab8MenusScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Lab8MenusScreen() {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showPopupMenu by remember { mutableStateOf(false) }
    var showContextMenu by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var textColor by remember { mutableStateOf(Color.Black) }

    // Options menu items
    var optionsList by remember {
        mutableStateOf(listOf(
            "Опція 1", "Опція 2", "Опція 3", "Опція 4", "Опція 5"
        ))
    }

    // List items with styles
    var listItems by remember {
        mutableStateOf((1..8).map { MenuEntry(it, "Item $it", 0) })
    }
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }
    var selectionMode by remember { mutableStateOf(false) }
    var showListReorderMenu by remember { mutableStateOf(false) }
    val originalList = remember { (1..8).map { MenuEntry(it, "Item $it", 0) } }

    // Toast display
    toastMessage?.let { msg ->
        LaunchedEffect(msg) {
            delay(2000)
            toastMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lab 8: Menus") },
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Text("⋮", fontSize = 24.sp)
                    }
                    // New icon for list reordering menu
                    IconButton(onClick = { showListReorderMenu = true }) {
                        Text("↕", fontSize = 20.sp)
                    }
                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        optionsList.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    toastMessage = "Selected: $option"
                                    showOptionsMenu = false
                                }
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Reorder...") },
                            onClick = {
                                showOptionsMenu = false
                                showPopupMenu = true
                            }
                        )
                    }

                    // Popup menu for reordering
                    DropdownMenu(
                        expanded = showPopupMenu,
                        onDismissRequest = { showPopupMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Move Last Item Up") },
                            onClick = {
                                if (optionsList.size > 1) {
                                    optionsList = optionsList.toMutableList().apply {
                                        val last = removeAt(lastIndex)
                                        add(size - 1, last)
                                    }
                                    toastMessage = "Menu item moved up"
                                }
                                showPopupMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Move First Item Down") },
                            onClick = {
                                if (optionsList.size > 1) {
                                    optionsList = optionsList.toMutableList().apply {
                                        val first = removeAt(0)
                                        add(1, first)
                                    }
                                    toastMessage = "Menu item moved down"
                                }
                                showPopupMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reverse Order") },
                            onClick = {
                                optionsList = optionsList.reversed()
                                toastMessage = "Menu order reversed"
                                showPopupMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Shuffle") },
                            onClick = {
                                optionsList = optionsList.shuffled()
                                toastMessage = "Menu items shuffled"
                                showPopupMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset to Original") },
                            onClick = {
                                optionsList = listOf("Опція 1", "Опція 2", "Опція 3", "Опція 4", "Опція 5")
                                toastMessage = "Menu order reset"
                                showPopupMenu = false
                            }
                        )
                    }

                    // Dropdown for reordering list items (Item 1..8)
                    DropdownMenu(
                        expanded = showListReorderMenu,
                        onDismissRequest = { showListReorderMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Move First Down") },
                            onClick = {
                                if (listItems.size > 1) {
                                    listItems = listItems.toMutableList().apply {
                                        val first = removeAt(0); add(1, first)
                                    }
                                    toastMessage = "First item moved down"
                                }
                                showListReorderMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Move Last Up") },
                            onClick = {
                                if (listItems.size > 1) {
                                    listItems = listItems.toMutableList().apply {
                                        val last = removeAt(lastIndex); add(size - 1, last)
                                    }
                                    toastMessage = "Last item moved up"
                                }
                                showListReorderMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reverse List") },
                            onClick = {
                                listItems = listItems.reversed(); toastMessage = "List reversed"; showListReorderMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Shuffle List") },
                            onClick = {
                                listItems = listItems.shuffled(); toastMessage = "List shuffled"; showListReorderMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset List") },
                            onClick = {
                                listItems = originalList.map { it.copy(styleFlags = 0) }; toastMessage = "List reset"; showListReorderMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Toast display area
            toastMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(12.dp),
                        color = Color.White
                    )
                }
            }

            // Context menu demo text
            Box {
                Text(
                    text = "Long-press here to change my color (Context Menu)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = { showContextMenu = true },
                            onClick = {}
                        )
                        .padding(12.dp),
                    color = textColor,
                    fontSize = 16.sp
                )

                DropdownMenu(
                    expanded = showContextMenu,
                    onDismissRequest = { showContextMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Red", color = Color.Red, fontWeight = FontWeight.Bold) },
                        onClick = {
                            textColor = Color.Red
                            showContextMenu = false
                            toastMessage = "Text color changed to Red"
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Green", color = Color.Green, fontWeight = FontWeight.Bold) },
                        onClick = {
                            textColor = Color.Green
                            showContextMenu = false
                            toastMessage = "Text color changed to Green"
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Blue", color = Color.Blue, fontWeight = FontWeight.Bold) },
                        onClick = {
                            textColor = Color.Blue
                            showContextMenu = false
                            toastMessage = "Text color changed to Blue"
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Yellow", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold) },
                        onClick = {
                            textColor = Color(0xFFFFD700)
                            showContextMenu = false
                            toastMessage = "Text color changed to Yellow"
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Magenta", color = Color.Magenta, fontWeight = FontWeight.Bold) },
                        onClick = {
                            textColor = Color.Magenta
                            showContextMenu = false
                            toastMessage = "Text color changed to Magenta"
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Default (Black)") },
                        onClick = {
                            textColor = Color.Black
                            showContextMenu = false
                            toastMessage = "Text color reset to Black"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contextual Action Mode header
            if (selectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Up / Down for selected items
                    Button(onClick = {
                        if (selectedItems.isEmpty()) {
                            toastMessage = "Select items first"
                        } else {
                            val mutable = listItems.toMutableList()
                            val indices = listItems.mapIndexed { idx, item -> idx to item }
                                .filter { selectedItems.contains(it.second.id) }
                                .map { it.first }
                                .sorted()
                            var changed = false
                            indices.forEach { i ->
                                if (i > 0 && i - 1 !in indices) {
                                    val tmp = mutable[i]; mutable[i] = mutable[i - 1]; mutable[i - 1] = tmp; changed = true
                                }
                            }
                            if (changed) {
                                listItems = mutable
                                toastMessage = "Moved up"
                            }
                        }
                    }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Up", fontSize = 12.sp) }
                    Button(onClick = {
                        if (selectedItems.isEmpty()) {
                            toastMessage = "Select items first"
                        } else {
                            val mutable = listItems.toMutableList()
                            val indices = listItems.mapIndexed { idx, item -> idx to item }
                                .filter { selectedItems.contains(it.second.id) }
                                .map { it.first }
                                .sortedDescending()
                            var changed = false
                            indices.forEach { i ->
                                if (i < mutable.lastIndex && i + 1 !in indices) {
                                    val tmp = mutable[i]; mutable[i] = mutable[i + 1]; mutable[i + 1] = tmp; changed = true
                                }
                            }
                            if (changed) {
                                listItems = mutable
                                toastMessage = "Moved down"
                            }
                        }
                    }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Down", fontSize = 12.sp) }
                    Button(onClick = {
                        listItems = listItems.map {
                            if (selectedItems.contains(it.id)) it.copy(styleFlags = it.styleFlags or 1) else it
                        }
                    }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Bold", fontSize = 12.sp) }
                    Button(onClick = {
                        listItems = listItems.map {
                            if (selectedItems.contains(it.id)) it.copy(styleFlags = it.styleFlags or 2) else it
                        }
                    }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Italic", fontSize = 12.sp) }
                    Button(onClick = {
                        listItems = listItems.map {
                            if (selectedItems.contains(it.id)) it.copy(styleFlags = it.styleFlags or 4) else it
                        }
                    }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Highlight", fontSize = 12.sp) }
                    Button(onClick = {
                        listItems = listItems.map {
                            if (selectedItems.contains(it.id)) it.copy(styleFlags = 0) else it
                        }
                    }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Clear", fontSize = 12.sp) }
                    IconButton(onClick = {
                        selectionMode = false
                        selectedItems = setOf()
                    }) {
                        Text("✕")
                    }
                }
            }

            Text(
                text = "Long-press items for Contextual Action Mode:",
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 14.sp
            )

            // List with contextual action mode
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(listItems) { item ->
                    val isSelected = selectedItems.contains(item.id)
                    val bgColor = when {
                        isSelected -> Color(0xFFBBDEFB)
                        item.styleFlags and 4 != 0 -> Color(0xFFFFE0B2)
                        else -> Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .combinedClickable(
                                onLongClick = {
                                    selectionMode = true
                                    selectedItems = if (isSelected) {
                                        selectedItems - item.id
                                    } else {
                                        selectedItems + item.id
                                    }
                                },
                                onClick = {
                                    if (selectionMode) {
                                        selectedItems = if (isSelected) {
                                            selectedItems - item.id
                                        } else {
                                            selectedItems + item.id
                                        }
                                    }
                                }
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectionMode) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    selectedItems = if (it) {
                                        selectedItems + item.id
                                    } else {
                                        selectedItems - item.id
                                    }
                                }
                            )
                        }
                        Text(
                            text = item.title,
                            fontWeight = if (item.styleFlags and 1 != 0) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (item.styleFlags and 2 != 0) FontStyle.Italic else FontStyle.Normal
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
