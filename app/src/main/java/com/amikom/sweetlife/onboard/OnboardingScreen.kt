package com.amikom.sweetlife.onboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    onFinished: () -> Unit
) {

    val pages: List<OnboardingModel> = listOf(
        OnboardingModel.FirstPages,
        OnboardingModel.SecondPages,
        OnboardingModel.ThirdPages,
    )

    val pagerState: PagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }

    val buttonState: State<List<String>> = remember {
        derivedStateOf {
            when(pagerState.currentPage) {
                0 -> listOf("", "Next")
                1 -> listOf("Back", "Next")
                2 -> listOf("Back", "Start")
                else -> listOf("", "")
            }
        }
    }

    val scope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp, 32.dp, 16.dp, 27.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IndicatorUI(
                    pageSize = pages.size,
                    currentPage = pagerState.currentPage
                )
            }
        },
        bottomBar = {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if(buttonState.value[0].isNotEmpty()) {
                    ButtonUI (
                        text = buttonState.value[0],
                        backgroundColor = Color.Transparent,
                        textColor = Color.Gray
                        ) {
                        scope.launch {
                            if(pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                ButtonUI(
                    text = buttonState.value[1],
                    isNext = true,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    scope.launch {
                        if(pagerState.currentPage < pages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinished()
                        }
                    }
                }
            }
        }
    },
        content = {
            Column(Modifier.padding(it)) {
                HorizontalPager(state = pagerState) { index ->
                    OnboardingGraphUI(onboardingModel = pages[index])
                }
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnBoardingScreen() {

    }
}