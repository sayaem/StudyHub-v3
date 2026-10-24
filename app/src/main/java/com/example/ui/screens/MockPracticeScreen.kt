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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.components.StudyHubDetailTopBar
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.BrandDeepTeal
import com.example.ui.theme.BrandPaleTeal
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class PracticeQuiz(
    val id: String,
    val subject: String,
    val chapter: String,
    val questionCount: Int,
    val durationMinutes: Int,
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int
)

val SAMPLE_QUIZZES = listOf(
    PracticeQuiz(
        id = "quiz_math_1",
        subject = "Mathematics",
        chapter = "Chapter 3 - Algebra & Polynomials",
        questionCount = 4,
        durationMinutes = 15,
        questions = listOf(
            QuizQuestion("If a + b = 5 and ab = 6, what is a² + b²?", listOf("13", "19", "25", "12"), 0),
            QuizQuestion("What is the degree of the zero polynomial?", listOf("0", "1", "Undefined", "Infinite"), 2),
            QuizQuestion("Find the discriminant of 2x² - 4x + 3 = 0:", listOf("-8", "16", "8", "-16"), 0),
            QuizQuestion("What is the sum of roots of ax² + bx + c = 0?", listOf("-b/a", "b/a", "c/a", "-c/a"), 0)
        )
    ),
    PracticeQuiz(
        id = "quiz_phys_1",
        subject = "Physics",
        chapter = "Chapter 2 - Kinematics & Motion",
        questionCount = 3,
        durationMinutes = 10,
        questions = listOf(
            QuizQuestion("Which quantity has SI unit m/s²?", listOf("Velocity", "Acceleration", "Momentum", "Force"), 1),
            QuizQuestion("What is the acceleration due to gravity on Earth surface approx?", listOf("9.8 m/s²", "8.9 m/s²", "10.8 m/s²", "9.2 m/s²"), 0),
            QuizQuestion("Area under velocity-time graph represents:", listOf("Displacement", "Speed", "Acceleration", "Force"), 0)
        )
    ),
    PracticeQuiz(
        id = "quiz_chem_1",
        subject = "Chemistry",
        chapter = "Chapter 1 - Atomic Structure",
        questionCount = 3,
        durationMinutes = 10,
        questions = listOf(
            QuizQuestion("Which subatomic particle has no electric charge?", listOf("Electron", "Proton", "Neutron", "Positron"), 2),
            QuizQuestion("The maximum number of electrons in 2nd shell (n=2) is:", listOf("2", "8", "18", "32"), 1),
            QuizQuestion("Isotopes have the same number of:", listOf("Neutrons", "Protons", "Mass number", "Nucleons"), 1)
        )
    )
)

@Composable
fun MockPracticeScreen(
    onBack: () -> Unit
) {
    var selectedSubjectTab by remember { mutableStateOf("All") }
    var activeQuiz by remember { mutableStateOf<PracticeQuiz?>(null) }

    var totalTestsCompleted by remember { mutableIntStateOf(0) }
    var totalCorrectQuestions by remember { mutableIntStateOf(0) }
    var totalAttemptedQuestions by remember { mutableIntStateOf(0) }

    val filteredQuizzes = SAMPLE_QUIZZES.filter {
        selectedSubjectTab == "All" || it.subject == selectedSubjectTab
    }

    val accuracyPercent = if (totalAttemptedQuestions > 0) {
        ((totalCorrectQuestions.toFloat() / totalAttemptedQuestions) * 100).toInt()
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StudyHubDetailTopBar(
            title = "Mock Test & Practice",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Subject Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Mathematics", "Physics", "Chemistry").forEach { tab ->
                    val isSelected = selectedSubjectTab == tab
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedSubjectTab = tab },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandTeal else MaterialTheme.colorScheme.surface
                        ),
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleLight) else null
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Performance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtleLight, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your Performance",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PerformanceMetric(
                            value = if (totalAttemptedQuestions > 0) "$accuracyPercent%" else "0%",
                            label = "Accuracy",
                            color = BrandTeal
                        )
                        PerformanceMetric(
                            value = "$totalTestsCompleted",
                            label = "Tests Completed",
                            color = Color(0xFF2563EB)
                        )
                        PerformanceMetric(
                            value = "${totalCorrectQuestions * 10}",
                            label = "Total Marks",
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Available Practice Sets",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredQuizzes, key = { it.id }) { quiz ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtleLight, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandPaleTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (quiz.subject == "Mathematics") Icons.Default.Calculate else Icons.Default.Science,
                                    contentDescription = quiz.subject,
                                    tint = BrandTeal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = quiz.subject,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = quiz.chapter,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${quiz.questionCount} Questions • ${quiz.durationMinutes} min",
                                    fontSize = 11.sp,
                                    color = BrandTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = { activeQuiz = quiz },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Start", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Active Quiz Modal Dialog
    activeQuiz?.let { quiz ->
        var currentQIndex by remember { mutableIntStateOf(0) }
        var selectedOption by remember { mutableIntStateOf(-1) }
        var correctInThisQuiz by remember { mutableIntStateOf(0) }
        var isResultScreen by remember { mutableStateOf(false) }

        val question = quiz.questions.getOrNull(currentQIndex)

        AlertDialog(
            onDismissRequest = { activeQuiz = null },
            title = {
                Text(
                    text = if (isResultScreen) "Practice Completed!" else "${quiz.subject} (${currentQIndex + 1}/${quiz.questions.size})",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (isResultScreen) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Score: $correctInThisQuiz / ${quiz.questions.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandTeal
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (correctInThisQuiz == quiz.questions.size) "Perfect score! Keep it up!" else "Good practice session! Review the concepts.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                } else if (question != null) {
                    Column {
                        Text(
                            text = question.prompt,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        question.options.forEachIndexed { idx, opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedOption == idx) BrandPaleTeal else Color(0xFFF8FAFC))
                                    .clickable { selectedOption = idx }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedOption == idx,
                                    onClick = { selectedOption = idx }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = opt, fontSize = 13.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (isResultScreen) {
                    Button(
                        onClick = {
                            totalTestsCompleted += 1
                            totalCorrectQuestions += correctInThisQuiz
                            totalAttemptedQuestions += quiz.questions.size
                            activeQuiz = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                    ) {
                        Text("Done")
                    }
                } else {
                    Button(
                        onClick = {
                            if (selectedOption != -1) {
                                if (selectedOption == question?.correctIndex) {
                                    correctInThisQuiz += 1
                                }
                                if (currentQIndex + 1 < quiz.questions.size) {
                                    currentQIndex += 1
                                    selectedOption = -1
                                } else {
                                    isResultScreen = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                        enabled = selectedOption != -1
                    ) {
                        Text(if (currentQIndex + 1 < quiz.questions.size) "Next" else "Submit")
                    }
                }
            },
            dismissButton = {
                if (!isResultScreen) {
                    TextButton(onClick = { activeQuiz = null }) {
                        Text("Exit")
                    }
                }
            }
        )
    }
}

@Composable
fun PerformanceMetric(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}
