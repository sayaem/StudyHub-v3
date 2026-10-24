package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChapterEntity
import com.example.data.local.SubjectEntity
import com.example.ui.components.LiveSubjectCardPreview
import com.example.ui.components.SUBJECT_ICONS
import com.example.ui.components.SUBJECT_PALETTE
import com.example.ui.components.getSubjectIcon
import com.example.ui.components.parseColorHex
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.BrandDeepTeal
import com.example.ui.theme.BrandLightBlue
import com.example.ui.theme.BrandPaleTeal
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusScreen(
    subjects: List<SubjectEntity>,
    chapters: List<ChapterEntity>,
    onAddSubject: (
        name: String,
        banglaName: String?,
        colorHex: String,
        iconKey: String,
        category: String,
        chapterCount: Int
    ) -> Unit,
    onDeleteSubject: (SubjectEntity) -> Unit,
    onUpdateChapterStatus: (ChapterEntity, String) -> Unit,
    onAddChapter: (Long, String) -> Unit,
    onDeleteChapter: (ChapterEntity) -> Unit,
    initialAddSubjectRequested: Boolean = false,
    onAddSubjectDismissed: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") } // "All", "Compulsory", "Optional"

    var showAddSheet by remember { mutableStateOf(initialAddSubjectRequested) }
    var selectedSubjectDetail by remember { mutableStateOf<SubjectEntity?>(null) }
    var subjectToDelete by remember { mutableStateOf<SubjectEntity?>(null) }

    val filteredSubjects = subjects.filter { sub ->
        val matchesCategory = selectedCategory == "All" || sub.category == selectedCategory
        val matchesSearch = sub.name.contains(searchQuery, ignoreCase = true) ||
                (sub.banglaName?.contains(searchQuery, ignoreCase = true) == true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header with "+ Add Subject" CTA button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Syllabus",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Button(
                onClick = { showAddSheet = true },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Subject", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search subjects...", color = TextMuted, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandTeal,
                unfocusedBorderColor = BorderSubtleLight,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Compulsory", "Optional").forEach { cat ->
                val isSelected = selectedCategory == cat
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedCategory = cat },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) BrandTeal else MaterialTheme.colorScheme.surface
                    ),
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight) else null
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subject List
        if (filteredSubjects.isEmpty()) {
            // Empty State
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .border(1.dp, BorderSubtleLight, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = BrandTeal,
                        modifier = Modifier
                            .size(56.dp)
                            .background(BrandPaleTeal, CircleShape)
                            .padding(14.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No subjects match your search" else "No subjects added yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Build your study plan one subject at a time.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAddSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create Subject")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredSubjects, key = { it.id }) { subject ->
                    val subjectChapters = chapters.filter { it.subjectId == subject.id }
                    val completedCount = subjectChapters.count { it.status == "COMPLETED" }
                    val progress = if (subjectChapters.isNotEmpty()) {
                        completedCount.toFloat() / subjectChapters.size
                    } else 0f
                    val percent = (progress * 100).toInt()
                    val subjectColor = parseColorHex(subject.colorHex)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedSubjectDetail = subject }
                            .border(1.dp, BorderSubtleLight, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(subjectColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getSubjectIcon(subject.iconKey),
                                    contentDescription = subject.name,
                                    tint = subjectColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = subject.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    if (!subject.banglaName.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${subject.banglaName})",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (subjectChapters.isNotEmpty()) {
                                        "$completedCount / ${subjectChapters.size} chapters"
                                    } else "0 chapters",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )

                                if (subjectChapters.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = subjectColor,
                                        trackColor = subjectColor.copy(alpha = 0.15f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (subjectChapters.isNotEmpty()) "$percent%" else "Empty",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (subjectChapters.isNotEmpty()) subjectColor else TextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Subject Bottom Sheet
    if (showAddSheet) {
        AddSubjectBottomSheet(
            onDismiss = {
                showAddSheet = false
                onAddSubjectDismissed()
            },
            onCreateSubject = { name, bangla, color, icon, category, chaptersCount ->
                onAddSubject(name, bangla, color, icon, category, chaptersCount)
                showAddSheet = false
                onAddSubjectDismissed()
            }
        )
    }

    // Subject Detail Dialog / Sheet
    selectedSubjectDetail?.let { currentSubject ->
        val subjectChapters = chapters.filter { it.subjectId == currentSubject.id }
        SubjectDetailDialog(
            subject = currentSubject,
            chapters = subjectChapters,
            onDismiss = { selectedSubjectDetail = null },
            onUpdateChapterStatus = onUpdateChapterStatus,
            onAddChapter = { title -> onAddChapter(currentSubject.id, title) },
            onDeleteChapter = onDeleteChapter,
            onDeleteSubjectRequested = {
                subjectToDelete = currentSubject
                selectedSubjectDetail = null
            }
        )
    }

    // Delete Subject Confirmation Dialog
    subjectToDelete?.let { deletingSub ->
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Delete ${deletingSub.name}?") },
            text = { Text("Deleting this subject will remove all its tracked chapters. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSubject(deletingSub)
                        subjectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectBottomSheet(
    onDismiss: () -> Unit,
    onCreateSubject: (
        name: String,
        banglaName: String?,
        colorHex: String,
        iconKey: String,
        category: String,
        chapterCount: Int
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var subjectName by remember { mutableStateOf("") }
    var banglaName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(SUBJECT_PALETTE.first()) }
    var selectedIconKey by remember { mutableStateOf(SUBJECT_ICONS.first().first) }
    var selectedCategory by remember { mutableStateOf("Compulsory") }
    var chapterCount by remember { mutableIntStateOf(5) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Text(
                text = "Add Subject",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Enter subject name and choose color theme",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live Preview Card
            Text(
                text = "Live Preview",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            LiveSubjectCardPreview(
                name = subjectName,
                banglaName = banglaName,
                colorHex = selectedColor,
                iconKey = selectedIconKey,
                chapterCount = chapterCount
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subject Name
            OutlinedTextField(
                value = subjectName,
                onValueChange = {
                    subjectName = it
                    errorMessage = null
                },
                label = { Text("Subject Name *") },
                placeholder = { Text("e.g. Mathematics") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it, color = StatusDanger) } },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandTeal,
                    unfocusedBorderColor = BorderSubtleLight
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bangla Name (Optional)
            OutlinedTextField(
                value = banglaName,
                onValueChange = { banglaName = it },
                label = { Text("Bangla Name (Optional)") },
                placeholder = { Text("যেমন: গণিত") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandTeal,
                    unfocusedBorderColor = BorderSubtleLight
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subject Color Swatches
            Text(
                text = "Subject Color",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SUBJECT_PALETTE.forEach { hex ->
                    val color = parseColorHex(hex)
                    val isSelected = selectedColor == hex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = hex }
                            .then(
                                if (isSelected) {
                                    Modifier.border(3.dp, BrandDeepTeal, CircleShape)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Icon Selection
            Text(
                text = "Icon",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SUBJECT_ICONS.forEach { (key, iconVector) ->
                    val isSelected = selectedIconKey == key
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BrandPaleTeal else Color(0xFFF1F5F9))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) BrandTeal else BorderSubtleLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedIconKey = key },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = key,
                            tint = if (isSelected) BrandTeal else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chapter Count Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Chapters (Optional)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Initial chapters will be created automatically",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .border(1.dp, BorderSubtleLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = { if (chapterCount > 0) chapterCount -= 1 },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                    }
                    Text(
                        text = "$chapterCount",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { if (chapterCount < 40) chapterCount += 1 },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (subjectName.isBlank()) {
                            errorMessage = "Subject name is required"
                        } else {
                            onCreateSubject(
                                subjectName,
                                banglaName,
                                selectedColor,
                                selectedIconKey,
                                selectedCategory,
                                chapterCount
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Create Subject", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SubjectDetailDialog(
    subject: SubjectEntity,
    chapters: List<ChapterEntity>,
    onDismiss: () -> Unit,
    onUpdateChapterStatus: (ChapterEntity, String) -> Unit,
    onAddChapter: (String) -> Unit,
    onDeleteChapter: (ChapterEntity) -> Unit,
    onDeleteSubjectRequested: () -> Unit
) {
    var newChapterTitle by remember { mutableStateOf("") }
    var showAddChapterField by remember { mutableStateOf(false) }

    val subjectColor = parseColorHex(subject.colorHex)
    val completedCount = chapters.count { it.status == "COMPLETED" }
    val progress = if (chapters.isNotEmpty()) completedCount.toFloat() / chapters.size else 0f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(subjectColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getSubjectIcon(subject.iconKey),
                        contentDescription = null,
                        tint = subjectColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = subject.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = "$completedCount of ${chapters.size} completed (${(progress * 100).toInt()}%)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = subjectColor,
                    trackColor = subjectColor.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Chapters",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (chapters.isEmpty()) {
                    Text(
                        text = "No chapters added yet.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                } else {
                    chapters.forEach { chapter ->
                        val isDone = chapter.status == "COMPLETED"
                        val isInProgress = chapter.status == "IN_PROGRESS"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val nextStatus = when (chapter.status) {
                                        "NOT_STARTED" -> "IN_PROGRESS"
                                        "IN_PROGRESS" -> "COMPLETED"
                                        else -> "NOT_STARTED"
                                    }
                                    onUpdateChapterStatus(chapter, nextStatus)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        isDone -> Icons.Default.CheckCircle
                                        isInProgress -> Icons.Default.KeyboardArrowUp
                                        else -> Icons.Default.RadioButtonUnchecked
                                    },
                                    contentDescription = chapter.status,
                                    tint = when {
                                        isDone -> StatusSuccess
                                        isInProgress -> StatusWarning
                                        else -> TextMuted
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium,
                                    color = if (isDone) TextMuted else TextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = when (chapter.status) {
                                    "COMPLETED" -> "Done"
                                    "IN_PROGRESS" -> "Ongoing"
                                    else -> "To-Do"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (chapter.status) {
                                    "COMPLETED" -> StatusSuccess
                                    "IN_PROGRESS" -> StatusWarning
                                    else -> TextMuted
                                }
                            )

                            IconButton(
                                onClick = { onDeleteChapter(chapter) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete chapter",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (showAddChapterField) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newChapterTitle,
                            onValueChange = { newChapterTitle = it },
                            placeholder = { Text("Chapter title", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newChapterTitle.isNotBlank()) {
                                    onAddChapter(newChapterTitle)
                                    newChapterTitle = ""
                                    showAddChapterField = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                        ) {
                            Text("Add")
                        }
                    }
                } else {
                    TextButton(onClick = { showAddChapterField = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Chapter")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDeleteSubjectRequested,
                colors = ButtonDefaults.textButtonColors(contentColor = StatusDanger)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete Subject")
            }
        }
    )
}
