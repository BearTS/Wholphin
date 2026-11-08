package com.github.damontecres.wholphin.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.github.damontecres.wholphin.data.model.BaseItem
import com.github.damontecres.wholphin.ui.LocalImageUrlService
import com.github.damontecres.wholphin.ui.components.QuickDetails
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

private val CardBg = Color(0xFF111318)
private val Ink = Color(0xFFF3F4F6)
private val Soft = Color(0xFFC9CDD3)
private val Dim = Color(0xFFA3A8B2)

/**
 * Free My TV's home header: the greeting and date, then the focused title opened into a wide card (its picture on
 * the right fading into the dark, what it is, its details and summary, and how far you are into it).
 */
@Composable
fun FmtvHomeHeader(
    item: BaseItem?,
    modifier: Modifier = Modifier,
    greeting: Boolean = true,
) {
    Column(modifier = modifier.padding(start = 8.dp, top = 4.dp, bottom = 14.dp)) {
        if (greeting) {
            Greeting()
            Spacer(Modifier.height(12.dp))
        }
        WideCard(item)
    }
}

@Composable
private fun Greeting() {
    val (hello, date) =
        remember {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val hello =
                when (hour) {
                    in 5..11 -> "Good morning"
                    in 12..16 -> "Good afternoon"
                    else -> "Good evening"
                }
            hello to DateFormat.getDateInstance(DateFormat.FULL).format(Date())
        }
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(text = hello, color = Ink, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(text = date, color = Dim, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
    }
}

@Composable
private fun WideCard(item: BaseItem?) {
    val imageUrlService = LocalImageUrlService.current
    val picture =
        remember(item?.id) {
            imageUrlService.getItemImageUrl(item, ImageType.BACKDROP, fillWidth = 720)
                ?: imageUrlService.getItemImageUrl(item, ImageType.THUMB, fillWidth = 720)
        }
    val isEpisode = item?.type == BaseItemKind.EPISODE
    val eyebrow =
        remember(item?.id) {
            when (item?.type) {
                BaseItemKind.EPISODE -> "EPISODE"
                BaseItemKind.SERIES -> "SHOW"
                BaseItemKind.MOVIE -> "MOVIE"
                BaseItemKind.SEASON -> "SEASON"
                else -> item?.type?.name?.replace('_', ' ') ?: ""
            }
        }
    val title =
        remember(item?.id) {
            when (item?.type) {
                BaseItemKind.EPISODE, BaseItemKind.SEASON -> item?.data?.seriesName
                else -> item?.title
            }
        }
    val played = item?.data?.userData?.playedPercentage?.takeIf { it > 0.0 && it < 100.0 }
    Box(
        modifier =
            Modifier
                .width(560.dp)
                .height(190.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CardBg),
    ) {
        if (picture != null) {
            AsyncImage(
                model = picture,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.align(Alignment.CenterEnd).width(360.dp).fillMaxHeight(),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                0.38f to CardBg,
                                0.52f to CardBg.copy(alpha = 0.85f),
                                0.78f to CardBg.copy(alpha = 0f),
                            ),
                        ),
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp).width(320.dp),
        ) {
            if (eyebrow.isNotEmpty()) {
                Text(text = eyebrow, color = Soft, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            }
            Text(
                text = title ?: "",
                color = Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 26.sp,
            )
            val episodeName = if (isEpisode) item?.name else null
            if (episodeName != null) {
                Text(text = episodeName, color = Soft, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            QuickDetails(
                item?.ui?.quickDetails,
                item?.timeRemainingOrRuntime,
                endsAt = item?.data?.endDate,
                textStyle = MaterialTheme.typography.labelMedium,
            )
            item?.data?.overview?.let {
                Text(
                    text = it,
                    color = Color(0xFFDDE0E4),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = if (isEpisode) 2 else 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (played != null) {
            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(4.dp).background(Color(0x26FFFFFF))) {
                Box(Modifier.fillMaxWidth((played / 100.0).toFloat()).fillMaxHeight().background(Ink))
            }
        }
    }
}
