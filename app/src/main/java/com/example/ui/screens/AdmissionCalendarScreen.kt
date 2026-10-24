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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
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
import com.example.data.local.AdmissionTargetEntity
import com.example.ui.components.StudyHubDetailTopBar
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandDeepTeal
import com.example.ui.theme.BrandLightBlue
import com.example.ui.theme.BrandPaleTeal
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmissionCalendarScreen(
    targets: List<AdmissionTargetEntity>,
    onSaveTarget: (AdmissionTargetEntity) -> Unit,
    onDeleteTarget: (AdmissionTargetEntity) -> Unit,
    onBack: () -> Unit,
    initialAddRequested: Boolean = false,
    onAddDismissed: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Applications", "Exams", "Reminders"
    var showAddSheet by remember { mutableStateOf(initialAddRequested) }
    var selectedTargetDetail by remember { mutableStateOf<AdmissionTargetEntity?>(null) }
    var targetToDelete by remember { mutableStateOf<AdmissionTargetEntity?>(null) }

    val todayDate = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    fun getDaysLeft(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return "No date"
        return try {
            val d = dateFormat.parse(dateStr) ?: return "No date"
            val diffMs = d.time - todayDate.time
            val days = diffMs / (1000 * 60 * 60 * 24)
            when {
                days < 0 -> "Passed"
                days == 0L -> "Today"
                days == 1L -> "1 day left"
                else -> "$days days left"
            }
        } catch (e: Exception) {
            "No date"
        }
    }

    val filteredTargets = targets.filter {
        when (selectedFilter) {
            "Applications" -> it.applicationStatus.contains("APPLICATION") || it.applicationStatus == "IN_PROGRESS"
            "Exams" -> !it.examDate.isNullOrBlank()
            else -> true
        }
    }

    // Details Sub-screen view if selected
    if (selectedTargetDetail != null) {
        val detail = selectedTargetDetail!!
        ApplicationDetailsScreen(
            target = detail,
            daysLeft = getDaysLeft(detail.applicationDeadline),
            onBack = { selectedTargetDetail = null },
            onEdit = {
                // Open add/edit sheet with prefilled target
                showAddSheet = true
            },
            onDelete = {
                targetToDelete = detail
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            StudyHubDetailTopBar(
                title = "Admission Calendar",
                onBackClick = onBack,
                actions = {
                    Button(
                        onClick = { showAddSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Target", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Filter Tabs: All, Applications, Exams, Reminders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Applications", "Exams", "Reminders").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedFilter = filter },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BrandTeal else MaterialTheme.colorScheme.surface
                            ),
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight) else null
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "My Targets (${filteredTargets.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredTargets.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .border(1.dp, BorderSubtleLight, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(BrandPaleTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No admission targets tracked yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Track application deadlines, admit cards, and exam dates in one place.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Add University")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredTargets, key = { it.id }) { target ->
                            val daysLeftStr = getDaysLeft(target.applicationDeadline)
                            val isUrgent = daysLeftStr.contains("days") && (daysLeftStr.split(" ").firstOrNull()?.toIntOrNull() ?: 100) <= 7

                            val categoryColor = when (target.category) {
                                "Medical" -> Color(0xFFEC4899)
                                "Engineering" -> BrandTeal
                                "General University" -> Color(0xFFF97316)
                                else -> BrandBlue
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedTargetDetail = target }
                                    .border(1.dp, BorderSubtleLight, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(categoryColor.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (target.category) {
                                                    "Medical" -> Icons.Default.MedicalServices
                                                    "Engineering" -> Icons.Default.Engineering
                                                    else -> Icons.Default.School
                                                },
                                                contentDescription = target.category,
                                                tint = categoryColor,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = target.institutionName,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            Text(
                                                text = "${target.category} • ${target.cycleLabel}${target.programUnit?.let { " • $it" } ?: ""}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isUrgent) Color(0xFFFEE2E2) else BrandLightBlue,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = daysLeftStr,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUrgent) StatusDanger else BrandBlue
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Application Deadline: ${target.applicationDeadline}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                        )

                                        Text(
                                            text = target.applicationStatus.replace("_", " ").lowercase()
                                                .replaceFirstChar { it.uppercase() },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrandTeal
                                        )
                                    }

                                    if (target.prepProgress > 0) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { target.prepProgress / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = categoryColor,
                                            trackColor = categoryColor.copy(alpha = 0.15f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Target Sheet
    if (showAddSheet) {
        AddAdmissionTargetSheet(
            onDismiss = {
                showAddSheet = false
                onAddDismissed()
            },
            onSave = { target ->
                onSaveTarget(target)
                showAddSheet = false
                onAddDismissed()
            }
        )
    }

    // Delete Target Confirmation
    targetToDelete?.let { tgt ->
        AlertDialog(
            onDismissRequest = { targetToDelete = null },
            title = { Text("Delete ${tgt.institutionName}?") },
            text = { Text("This will remove this admission target from your calendar and reminder timeline.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTarget(tgt)
                        targetToDelete = null
                        selectedTargetDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { targetToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ApplicationDetailsScreen(
    target: AdmissionTargetEntity,
    daysLeft: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StudyHubDetailTopBar(
            title = "Application Details",
            onBackClick = onBack,
            actions = {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusDanger)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Target Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtleLight, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(BrandPaleTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = target.institutionName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "${target.category} • ${target.cycleLabel}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(BrandPaleTeal, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = target.applicationStatus.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Days remaining: $daysLeft",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BrandDeepTeal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Important Dates
            Text(
                text = "Important Dates",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtleLight, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DateRow(label = "Application Open", date = target.applicationOpenDate ?: "Not specified")
                    DateRow(label = "Application Deadline", date = target.applicationDeadline, isHighlight = true)
                    DateRow(label = "Admit Card Available", date = target.admitCardDate ?: "Not announced")
                    DateRow(label = "Admission Exam", date = target.examDate ?: "Not announced")
                    if (!target.resultDate.isNullOrBlank()) {
                        DateRow(label = "Result Publication", date = target.resultDate)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Status & Application Notes
            Text(
                text = "Application Status & Notes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtleLight, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Status: ${target.applicationStatus.replace("_", " ")}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    if (!target.applicationFee.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Application Fee: ${target.applicationFee}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    if (!target.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Notes: ${target.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DateRow(label: String, date: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = if (isHighlight) BrandTeal else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = if (isHighlight) MaterialTheme.colorScheme.onSurface else TextSecondary,
                fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        Text(
            text = date,
            fontSize = 13.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) BrandTeal else MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdmissionTargetSheet(
    onDismiss: () -> Unit,
    onSave: (AdmissionTargetEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General University") }
    var programUnit by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("2025-11-25") }
    var examDate by remember { mutableStateOf("2025-12-20") }
    var fee by remember { mutableStateOf("1000 BDT") }
    var status by remember { mutableStateOf("PLANNING") }
    var notes by remember { mutableStateOf("") }

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
                text = "Add Admission Target",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Enter institution and milestone dates",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("University / Institution Name *") },
                placeholder = { Text("e.g. Dhaka University, BUET") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Category", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("General University", "Medical", "Engineering", "Other").forEach { cat ->
                    val isSelected = category == cat
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { category = cat },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandTeal else Color(0xFFF1F5F9)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (cat == "General University") "General" else cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = programUnit,
                onValueChange = { programUnit = it },
                label = { Text("Program / Faculty / Unit (Optional)") },
                placeholder = { Text("e.g. A Unit, Science Faculty") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = { Text("Application Deadline (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = examDate,
                onValueChange = { examDate = it },
                label = { Text("Exam Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = fee,
                onValueChange = { fee = it },
                label = { Text("Application Fee") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                        if (name.isNotBlank() && deadline.isNotBlank()) {
                            onSave(
                                AdmissionTargetEntity(
                                    institutionName = name.trim(),
                                    category = category,
                                    programUnit = programUnit.trim().ifBlank { null },
                                    cycleLabel = "2025",
                                    applicationDeadline = deadline.trim(),
                                    examDate = examDate.trim().ifBlank { null },
                                    applicationFee = fee.trim().ifBlank { null },
                                    applicationStatus = status,
                                    notes = notes.trim().ifBlank { null }
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Save Target", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
