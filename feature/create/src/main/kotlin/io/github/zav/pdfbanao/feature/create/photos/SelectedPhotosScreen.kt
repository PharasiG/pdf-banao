package io.github.zav.pdfbanao.feature.create.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.compose.AsyncImage
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.preview.PreviewLightDarkHindiBigFont
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme

/**
 * Connects [SelectedPhotosScreen] to its [SelectedPhotosViewModel].
 *
 * @param photoUris the picked photos' addresses, from the navigation route.
 * @param onBack goes back to the screen before (Home).
 */
@Composable
fun SelectedPhotosRoute(
    photoUris: List<String>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SelectedPhotosViewModel =
        hiltViewModel<SelectedPhotosViewModel, SelectedPhotosViewModel.Factory> { factory ->
            factory.create(photoUris)
        },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // Ignores a second quick tap on the back arrow, which would otherwise close Home too.
    val goBack = dropUnlessResumed(block = onBack)
    SelectedPhotosScreen(
        state = state,
        onEvent = { event ->
            when (event) {
                SelectedPhotosEvent.BackClick -> goBack()
            }
        },
        modifier = modifier,
    )
}

/**
 * Draws the photos the user picked, as a simple list: a small picture and its page number.
 * No logic here: it only shows [state] and sends [SelectedPhotosEvent]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedPhotosScreen(
    state: SelectedPhotosUiState,
    onEvent: (SelectedPhotosEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SelectedPhotosTopBar(
                photoCount = state.photoUris.size,
                onBackClick = { onEvent(SelectedPhotosEvent.BackClick) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        PhotoList(
            photoUris = state.photoUris,
            someLeftOut = state.someLeftOut,
            innerPadding = innerPadding,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/** The bar at the top: the back arrow, and how many photos were picked. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedPhotosTopBar(
    photoCount: Int,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = pluralStringResource(
                    R.plurals.selected_photos_title,
                    photoCount,
                    photoCount,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

/**
 * The list of photos, in page order.
 *
 * @param innerPadding the space taken by the top bar and the phone's own bars. Rows scroll behind
 *   the see-through bar at the bottom of the phone, and the last row can still scroll above it.
 */
@Composable
private fun PhotoList(
    photoUris: List<String>,
    someLeftOut: Boolean,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    // On tablets, keep the rows a comfortable width instead of stretching them.
    val rowModifier = Modifier
        .widthIn(max = 640.dp)
        .fillMaxWidth()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
            bottom = innerPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (someLeftOut) {
            item {
                LeftOutNote(modifier = rowModifier)
            }
        }
        itemsIndexed(photoUris) { index, photoUri ->
            PhotoRow(
                pageNumber = index + 1,
                photoUri = photoUri,
                modifier = rowModifier,
            )
        }
    }
}

/** One photo: a small picture and "Page N". TalkBack reads the row as one item: "Page N". */
@Composable
private fun PhotoRow(
    pageNumber: Int,
    photoUri: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PhotoThumbnail(photoUri = photoUri)
        Text(
            text = stringResource(R.string.page_number, pageNumber),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/**
 * A small picture of the whole photo, the right way up.
 *
 * Coil loads it at the picture's size, not the photo's full size, so 100 photos fit in the memory
 * of a cheap phone. It also turns sideways photos the right way, using the photo's EXIF data.
 * The picture is decoration: the row's "Page N" text says what it is.
 */
@Composable
private fun PhotoThumbnail(
    photoUri: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = photoUri,
        contentDescription = null,
        modifier = modifier
            .size(80.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentScale = ContentScale.Fit,
    )
}

/** Says that only the first [MAX_PHOTOS] photos were kept. */
@Composable
private fun LeftOutNote(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = pluralStringResource(R.plurals.selected_photos_left_out, MAX_PHOTOS, MAX_PHOTOS),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@PreviewLightDarkHindiBigFont
@Composable
private fun SelectedPhotosScreenPreview() {
    PdfBanaoTheme {
        SelectedPhotosScreen(
            state = SelectedPhotosUiState(
                // Previews can't load real photos, so the pictures show as empty boxes.
                photoUris = List(3) { "content://preview/photo$it" },
                someLeftOut = true,
            ),
            onEvent = {},
        )
    }
}
