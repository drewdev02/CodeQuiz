package com.code.Quiz.core.ui.component.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.R
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextMuted

private val ActiveNavColor = Color(0xFF818CF8)

enum class BottomBarTab(
    val title: String,
    @get:DrawableRes val iconRes: Int
) {
    COURSES(
        title = "Cursos",
        iconRes = R.drawable.ic_nav_courses
    ),
    LIBRARY(
        title = "Biblioteca",
        iconRes = R.drawable.ic_nav_library
    ),
    PROGRESS(
        title = "Progreso",
        iconRes = R.drawable.ic_nav_progress
    )
}

@Composable
fun CodeQuizBottomBar(
    currentTab: BottomBarTab,
    onTabSelected: (BottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkSurfaceBorder)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomBarTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) ActiveNavColor else TextMuted,
                        label = "bottomBarContentColor"
                    )
                    val pillColor by animateColorAsState(
                        targetValue = if (isSelected) DarkSurfaceElevated else Color.Transparent,
                        label = "bottomBarPillColor"
                    )
                    val pillShape = RoundedCornerShape(16.dp)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = ActiveNavColor),
                                onClick = { onTabSelected(tab) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
                        ) {
                            // Icon container with optional pill background when selected
                            Box(
                                modifier = Modifier
                                    .clip(pillShape)
                                    .background(pillColor)
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = tab.iconRes),
                                    contentDescription = tab.title,
                                    tint = contentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = contentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Bottom Bar Preview - Courses Selected", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizBottomBarCoursesPreview() {
    CodeQuizTheme {
        CodeQuizBottomBar(
            currentTab = BottomBarTab.COURSES,
            onTabSelected = {}
        )
    }
}

@Preview(name = "Bottom Bar Preview - Library Selected", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizBottomBarLibraryPreview() {
    CodeQuizTheme {
        CodeQuizBottomBar(
            currentTab = BottomBarTab.LIBRARY,
            onTabSelected = {}
        )
    }
}

@Preview(name = "Bottom Bar Preview - Progress Selected", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizBottomBarProgressPreview() {
    CodeQuizTheme {
        CodeQuizBottomBar(
            currentTab = BottomBarTab.PROGRESS,
            onTabSelected = {}
        )
    }
}
