package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdmissionTargetEntity
import com.example.ui.components.StudyHubDetailTopBar
import com.example.ui.theme.*
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
    var selectedFilter by remember { mutableStateOf("All") }
    var showAddSheet by remember { mutableStateOf(initialAddRequested) }
    var selectedTargetDetail by remember { mutableStateOf<AdmissionTargetEntity?>(null) }
    var targetToDelete by remember { mutableStateOf<AdmissionTargetEntity?>(null) }

    val todayDate = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
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
        } catch (e: Exception) { "No date" }
    }

    val filteredTargets = targets.filter {
        when (selectedFilter) {
            "Applications" -> it.applicationStatus.contains("APPLICATION") || it.applicationStatus == "IN_PROGRESS"
            "Exams" -> !it.examDate.isNullOrBlank()
            else -> true
        }
    }

    if (selectedTargetDetail != null) {
        ApplicationDetailsScreen(
            target = selectedTargetDetail!!,
            daysLeft = getDaysLeft(selectedTargetDetail!!.applicationDeadline),
            onBack = { selectedTargetDetail = null },
            onEdit = { showAddSheet = true },
            onDelete = { targetToDelete = selectedTargetDetail }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddSheet = true },
                    containerColor = BrandTeal,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add admission target")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                StudyHubDetailTopBar(title = "Admission Command Center", onBackClick = onBack)

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp)) {
                    val totalTargets = targets.size
                    val upcomingDeadlines = targets.count { getDaysLeft(it.applicationDeadline).contains("left") }
                    val applicationsInProgress = targets.count { it.applicationStatus == "IN_PROGRESS" || it.applicationStatus == "APPLICATION_OPEN" }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OverviewCard("Total", "$totalTargets", Modifier.weight(1f))
                        OverviewCard("Upcoming", "$upcomingDeadlines", Modifier.weight(1f))
                        OverviewCard("Active", "$applicationsInProgress", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("All", "Applications", "Exams", "Reminders").forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Card(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { selectedFilter = filter },
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                            ) {
                                Text(filter, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("My Targets (${filteredTargets.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredTargets.isEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No admission targets tracked yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { showAddSheet = true }, colors = ButtonDefaults.buttonColors(containerColor = BrandTeal), shape = RoundedCornerShape(12.dp)) { Text("Add University") }
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filteredTargets, key = { it.id }) { target ->
                                val daysLeftStr = getDaysLeft(target.applicationDeadline)
                                Card(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable { selectedTargetDetail = target }.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                                    shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                        Text(target.institutionName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Deadline: ${target.applicationDeadline}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddAdmissionTargetSheet(onDismiss = { showAddSheet = false; onAddDismissed() }, onSave = { target -> onSaveTarget(target); showAddSheet = false; onAddDismissed() })
    }
}

@Composable
private fun OverviewCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdmissionTargetSheet(onDismiss: () -> Unit, onSave: (AdmissionTargetEntity) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("2025-11-25") }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { if (name.isNotBlank()) onSave(AdmissionTargetEntity(institutionName = name, applicationDeadline = deadline)) }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        }
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
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        StudyHubDetailTopBar(title = "Application Details", onBackClick = onBack, actions = {
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
        })
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp).verticalScroll(rememberScrollState())) {
            Card(modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(target.institutionName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))
                            Text("${target.category} • ${target.cycleLabel}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Days remaining: $daysLeft", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}


