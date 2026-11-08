package com.github.damontecres.wholphin.ui.nav

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.github.damontecres.wholphin.R
import com.github.damontecres.wholphin.data.model.JellyfinServer
import com.github.damontecres.wholphin.data.model.JellyfinUser
import com.github.damontecres.wholphin.preferences.UserPreferences
import com.github.damontecres.wholphin.services.SetupDestination
import com.github.damontecres.wholphin.ui.components.TimeDisplay
import com.github.damontecres.wholphin.ui.preferences.PreferenceScreenOption
import com.github.damontecres.wholphin.ui.setup.UserIconCardImage
import com.github.damontecres.wholphin.ui.tryRequestFocus

/** Free My TV's colors for the tabs: white when focused, a soft pill when selected. */
private val TabInk = Color(0xFFF3F4F6)
private val TabDim = Color(0xFF8E95A1)
private val TabDark = Color(0xFF15171A)
private val TabSelected = Color(0x2EFFFFFF)

/**
 * Free My TV's navigation: the sections as tabs along the top (Home, your libraries, Discover, Favorites), with
 * search, settings and your profile at the right, instead of the side rail. The tabs show on the top-level pages;
 * a title's page fills the screen and Back returns. On Home, Back first moves to the tabs.
 */
@Composable
fun TopNav(
    destination: Destination,
    preferences: UserPreferences,
    user: JellyfinUser,
    server: JellyfinServer,
    onClearBackdrop: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NavDrawerViewModel =
        hiltViewModel(
            LocalView.current.findViewTreeViewModelStoreOwner()!!,
            key = "${server.id}_${user.id}",
        ),
) {
    LaunchedEffect(destination) { viewModel.updateSelectedIndex() }
    val serviceState by viewModel.serviceState.collectAsState()
    val state by viewModel.state.collectAsState()
    val selectedIndex = state.selectedIndex
    val context = LocalContext.current

    val libraryDestinations =
        remember(serviceState) {
            (serviceState.items + serviceState.moreItems).mapNotNull { (it as? ServerNavDrawerItem)?.destination }
        }
    val topLevel =
        destination is Destination.Home ||
            destination is Destination.Search ||
            destination is Destination.Favorites ||
            destination is Destination.Discover ||
            destination in libraryDestinations
    val showClock = preferences.appPreferences.interfacePreferences.showClock

    if (!topLevel) {
        Box(modifier = modifier) {
            DestinationContent(
                destination = destination,
                preferences = preferences,
                onClearBackdrop = onClearBackdrop,
                modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 16.dp),
            )
            if (showClock) TimeDisplay()
        }
        return
    }

    val tabsFocus = remember { FocusRequester() }
    var tabsFocused by remember { mutableStateOf(false) }
    BackHandler(enabled = !tabsFocused && destination is Destination.Home) {
        tabsFocus.tryRequestFocus()
    }

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(start = 32.dp, end = if (showClock) 120.dp else 32.dp, top = 16.dp)
                        .onFocusChanged { tabsFocused = it.hasFocus },
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    item {
                        TopTab(
                            text = stringResource(R.string.home),
                            selected = selectedIndex == -1,
                            onClick = {
                                viewModel.setIndex(-1)
                                if (destination is Destination.Home) {
                                    viewModel.navigationManager.reloadHome()
                                    onClearBackdrop.invoke()
                                } else {
                                    viewModel.navigationManager.goToHome()
                                }
                            },
                            modifier = if (selectedIndex == -1 || selectedIndex < -1) Modifier.focusRequester(tabsFocus) else Modifier,
                        )
                    }
                    itemsIndexed(serviceState.items + serviceState.moreItems) { index, item ->
                        TopTab(
                            text = item.name(context),
                            selected = selectedIndex == index,
                            onClick = { viewModel.onClickDrawerItem(index, item) },
                            modifier = if (selectedIndex == index) Modifier.focusRequester(tabsFocus) else Modifier,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                TopIcon(
                    icon = Icons.Default.Search,
                    description = stringResource(R.string.search),
                    selected = selectedIndex == -2,
                    onClick = {
                        viewModel.setIndex(-2)
                        viewModel.navigationManager.navigateToFromDrawer(Destination.Search())
                    },
                )
                TopIcon(
                    icon = Icons.Default.Settings,
                    description = stringResource(R.string.settings),
                    selected = false,
                    onClick = {
                        viewModel.navigationManager.navigateTo(Destination.Settings(PreferenceScreenOption.BASIC))
                    },
                )
                val userImageUrl = remember(user) { viewModel.getUserImage(user) }
                Surface(
                    onClick = { viewModel.navigateToSetup(SetupDestination.UserList(server)) },
                    shape = ClickableSurfaceDefaults.shape(CircleShape),
                    colors =
                        ClickableSurfaceDefaults.colors(
                            containerColor = Color.Transparent,
                            focusedContainerColor = TabInk,
                        ),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1.1f),
                    modifier = Modifier.size(40.dp),
                ) {
                    UserIconCardImage(
                        id = user.id,
                        name = user.name,
                        imageUrl = userImageUrl,
                        modifier = Modifier.fillMaxSize().padding(3.dp),
                    )
                }
            }
            DestinationContent(
                destination = destination,
                preferences = preferences,
                onClearBackdrop = onClearBackdrop,
                modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 16.dp),
            )
        }
        if (showClock) TimeDisplay()
    }
}

@Composable
private fun TopTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(CircleShape),
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = if (selected) TabSelected else Color.Transparent,
                contentColor = if (selected) TabInk else TabDim,
                focusedContainerColor = TabInk,
                focusedContentColor = TabDark,
            ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
        modifier = modifier.height(36.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp).height(36.dp)) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun TopIcon(
    icon: ImageVector,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(CircleShape),
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = if (selected) TabSelected else Color(0x14FFFFFF),
                contentColor = TabInk,
                focusedContainerColor = TabInk,
                focusedContentColor = TabDark,
            ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.1f),
        modifier = Modifier.size(40.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = description, modifier = Modifier.size(20.dp))
        }
    }
}
