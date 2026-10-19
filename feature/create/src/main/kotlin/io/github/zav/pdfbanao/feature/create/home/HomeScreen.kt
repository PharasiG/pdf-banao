package io.github.zav.pdfbanao.feature.create.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.component.BigActionCard
import io.github.zav.pdfbanao.core.ui.component.ToolTile
import io.github.zav.pdfbanao.core.ui.preview.PreviewLightDarkHindiBigFont
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme
import io.github.zav.pdfbanao.feature.create.photos.MAX_PHOTOS

/**
 * Connects [HomeScreen] to its [HomeViewModel], and opens the Android photo picker when the
 * ViewModel asks for it.
 *
 * @param onPhotosPick called with the picked photos' addresses (content URIs), in the order the
 *   user picked them. Not called if the user closes the picker without picking anything.
 */
@Composable
fun HomeRoute(
    onPhotosPick: (photoUris: List<String>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // The photo picker needs no storage permission: the app only gets the photos the user picks.
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_PHOTOS),
    ) { uris ->
        if (uris.isNotEmpty()) onPhotosPick(uris.map(Uri::toString))
    }
    LaunchedEffect(state.openPhotoPicker) {
        if (state.openPhotoPicker) {
            photoPicker.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                    // Newer pickers number the photos as the user taps them (PRD F1: tap order).
                    isOrderedSelection = true,
                ),
            )
            viewModel.onEvent(HomeEvent.PhotoPickerOpened)
        }
    }

    HomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

/**
 * Draws the Home screen (PRD §5): three big cards, "Recent PDFs", then the Tools grid.
 * No logic here: it only shows [state] and sends [HomeEvent]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val comingSoonText = stringResource(R.string.coming_soon)
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(state.showComingSoon) {
        if (state.showComingSoon) {
            snackbarHostState.showSnackbar(comingSoonText)
            currentOnEvent(HomeEvent.ComingSoonShown)
        }
    }

    val onButtonClick = { button: HomeButton -> onEvent(HomeEvent.ButtonClick(button)) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopBar(
                onSettingsClick = { onButtonClick(HomeButton.Settings) },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        HomeContent(
            onButtonClick = onButtonClick,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
        )
    }
}

/** The bar at the top: the app's name, and the Settings button on the right. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.settings),
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

/** Everything below the top bar. */
@Composable
private fun HomeContent(
    onButtonClick: (HomeButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // On tablets, keep the content a comfortable width instead of stretching it.
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MainActions(onButtonClick = onButtonClick)
            SectionTitle(
                text = stringResource(R.string.home_recent_pdfs),
                modifier = Modifier.padding(top = 12.dp),
            )
            NoRecentPdfs()
            SectionTitle(
                text = stringResource(R.string.home_tools),
                modifier = Modifier.padding(top = 12.dp),
            )
            ToolsGrid(onButtonClick = onButtonClick)
        }
    }
}

/** The three big cards at the top. */
@Composable
private fun MainActions(
    onButtonClick: (HomeButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BigActionCard(
            title = stringResource(R.string.home_photos_to_pdf),
            description = stringResource(R.string.home_photos_to_pdf_hint),
            icon = painterResource(R.drawable.ic_photo_library),
            onClick = { onButtonClick(HomeButton.PhotosToPdf) },
            modifier = Modifier.fillMaxWidth(),
            highlighted = true,
        )
        BigActionCard(
            title = stringResource(R.string.home_scan),
            description = stringResource(R.string.home_scan_hint),
            icon = painterResource(R.drawable.ic_photo_camera),
            onClick = { onButtonClick(HomeButton.Scan) },
            modifier = Modifier.fillMaxWidth(),
        )
        BigActionCard(
            title = stringResource(R.string.home_my_pdfs),
            description = stringResource(R.string.home_my_pdfs_hint),
            icon = painterResource(R.drawable.ic_folder),
            onClick = { onButtonClick(HomeButton.MyPdfs) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** A section heading. TalkBack users can jump from heading to heading. */
@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        style = MaterialTheme.typography.titleLarge,
    )
}

/** The "Recent PDFs" spot while there are none. Task 3.1 shows the real list here. */
@Composable
private fun NoRecentPdfs(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_picture_as_pdf),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = stringResource(R.string.home_no_pdfs_yet),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

private data class Tool(
    val button: HomeButton,
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
)

private val tools = listOf(
    Tool(HomeButton.IdCard, R.string.tool_id_card, R.drawable.ic_id_card),
    Tool(HomeButton.MakeSmaller, R.string.tool_make_smaller, R.drawable.ic_compress),
    Tool(HomeButton.Join, R.string.tool_join, R.drawable.ic_merge),
    Tool(HomeButton.TakeOutPages, R.string.tool_take_out_pages, R.drawable.ic_content_cut),
    Tool(HomeButton.ArrangePages, R.string.tool_arrange_pages, R.drawable.ic_swap_vert),
    Tool(HomeButton.PdfToPhotos, R.string.tool_pdf_to_photos, R.drawable.ic_image),
    Tool(HomeButton.Lock, R.string.tool_lock, R.drawable.ic_lock),
    Tool(HomeButton.Unlock, R.string.tool_unlock, R.drawable.ic_lock_open),
)

private const val TOOLS_PER_ROW = 2

/** The Tools grid, two tiles per row. Tiles in a row are the same height. */
@Composable
private fun ToolsGrid(
    onButtonClick: (HomeButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        tools.chunked(TOOLS_PER_ROW).forEach { row ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { tool ->
                    ToolTile(
                        label = stringResource(tool.label),
                        icon = painterResource(tool.icon),
                        onClick = { onButtonClick(tool.button) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                // Keeps a lone last tile the same width as the others.
                repeat(TOOLS_PER_ROW - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@PreviewLightDarkHindiBigFont
@Composable
private fun HomeScreenPreview() {
    PdfBanaoTheme {
        HomeScreen(state = HomeUiState(), onEvent = {})
    }
}
