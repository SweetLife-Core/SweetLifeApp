package com.amikom.sweetlife.ui.screen.minicourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amikom.sweetlife.R

data class MiniCourse(
    val title: String,
    val imageUrl: String,
    val link: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniCourseScreen(courses: List<MiniCourse> = sampleCourses()) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mini Course") })
        }
    ) { safeDrawingPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(safeDrawingPadding)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(courses) { course ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        uriHandler.openUri(course.link)
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        // Replace with AsyncImage if using Coil/Glide
                        Image(
                            painter = painterResource(id = R.drawable.bapak), // placeholder image
                            contentDescription = course.title,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(course.title, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

fun sampleCourses() = listOf(
    MiniCourse(
        title = "Healthy Eating 101",
        imageUrl = "",
        link = "https://youtube.com"
    ),
    MiniCourse(
        title = "Diabetes Management",
        imageUrl = "",
        link = "https://youtube.com"
    )
)