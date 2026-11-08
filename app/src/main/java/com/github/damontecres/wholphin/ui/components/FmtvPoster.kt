package com.github.damontecres.wholphin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.damontecres.wholphin.data.model.BaseItem
import com.github.damontecres.wholphin.ui.LocalImageUrlService
import org.jellyfin.sdk.model.api.ImageType

/** Free My TV's title page: the big poster on the left (a show's own poster for an episode's page). */
@Composable
fun FmtvPoster(
    item: BaseItem,
    modifier: Modifier = Modifier,
    width: Dp = 170.dp,
) {
    val imageUrlService = LocalImageUrlService.current
    val url =
        remember(item.id) {
            imageUrlService.getItemImageUrl(item, ImageType.PRIMARY, fillWidth = 400, useSeriesForPrimary = true)
        }
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .width(width)
                .height(width * 1.5f)
                .shadow(16.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF262A33)),
    )
}
