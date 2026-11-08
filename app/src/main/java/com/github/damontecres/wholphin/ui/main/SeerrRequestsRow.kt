package com.github.damontecres.wholphin.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.github.damontecres.wholphin.data.model.DiscoverItem

/** One request on the home screen's Seerr row: the title, where it is, and how much has downloaded. */
@Immutable
data class SeerrRequestCard(
    val item: DiscoverItem,
    val title: String,
    val state: String,
    val color: Long,
    val progress: Float,
)

/**
 * Free My TV's "Coming to your Jellyfin": your Seerr requests as small cards (poster, title, state in color and a
 * progress bar from Radarr or Sonarr). OK opens the title's Seerr page.
 */
@Composable
fun SeerrRequestsRow(
    title: String,
    requests: List<SeerrRequestCard>,
    onClick: (DiscoverItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        Text(
            text = title,
            color = Color(0xFFF3F4F6),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 8.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            modifier = Modifier.fillMaxWidth().focusRestorer(),
        ) {
            items(requests, key = { "${it.item.type}_${it.item.id}" }) { card ->
                RequestCard(card = card, onClick = { onClick(card.item) })
            }
        }
    }
}

@Composable
private fun RequestCard(
    card: SeerrRequestCard,
    onClick: () -> Unit,
) {
    val ink = Color(card.color)
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(14.dp)),
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = Color(0x14FFFFFF),
                focusedContainerColor = Color(0x29FFFFFF),
            ),
        border =
            ClickableSurfaceDefaults.border(
                focusedBorder = Border(BorderStroke(2.dp, Color(0xFFF3F4F6)), shape = RoundedCornerShape(14.dp)),
            ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
        modifier = Modifier.width(240.dp).height(80.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = card.item.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(54.dp).fillMaxHeight().clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
            ) {
                Text(
                    text = card.title,
                    color = Color(0xFFF3F4F6),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = card.state,
                    color = ink,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0x24FFFFFF))) {
                    Box(Modifier.fillMaxWidth(card.progress.coerceIn(0.02f, 1f)).fillMaxHeight().background(ink))
                }
            }
        }
    }
}
