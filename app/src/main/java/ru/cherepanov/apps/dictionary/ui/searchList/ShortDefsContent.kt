package ru.cherepanov.apps.dictionary.ui.searchList

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.composable.DefList
import ru.cherepanov.apps.dictionary.ui.base.composable.DefTitle
import ru.cherepanov.apps.dictionary.ui.base.composable.FavoriteIcon
import ru.cherepanov.apps.dictionary.ui.base.composable.Language
import ru.cherepanov.apps.dictionary.ui.base.composable.preview.formattedWordDefStub

@Composable
fun ShortDefContent(
    modifier: Modifier,
    topContentPadding: Dp,
    bottomContentPadding: Dp,
    scrollState: LazyListState,
    shortDefs: List<FormattedWordDef>,
    addToFavorites: (DefId) -> Unit,
    removeFromFavorites: (DefId) -> Unit,
    onClick: (DefId) -> Unit = {},
    bringIntoViewShortDefId: DefId? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var isBroughtIntoView by rememberSaveable(bringIntoViewShortDefId) {
        mutableStateOf(false)
    }
    if (bringIntoViewShortDefId != null && !isBroughtIntoView) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val shortDefIndex = shortDefs.indexOfFirst {
                    it.id == bringIntoViewShortDefId
                }
                scrollState.animateScrollToItem(shortDefIndex)
                isBroughtIntoView = true
            }
        }
    }

    DefList(
        modifier = modifier,
        scrollState = scrollState,
        contentPadding = PaddingValues(
            top = 8.dp + topContentPadding,
            bottom = bottomContentPadding
        ),
        shortDefs = shortDefs
    ) {
        ShortDefItem(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            wordDef = it,
            isFavoriteDef = { isFavorite ->
                if (isFavorite) {
                    addToFavorites(it.id)
                } else {
                    removeFromFavorites(it.id)
                }
            }
        ) { onClick(it.id) }
        Spacer(modifier = Modifier.height(12.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun ShortDefItem(
    modifier: Modifier = Modifier,
    wordDef: FormattedWordDef = formattedWordDefStub(),
    isFavoriteDef: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) = with(wordDef) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            )
        ) {
            if (num != null || pos != null) {
                Row {
                    DefTitle(
                        modifier = Modifier.weight(0.6f),
                        num = num,
                        title = syllables
                    )
                    Pos(
                        modifier = Modifier.weight(0.4f),
                        pos = pos ?: AnnotatedString("")
                    )
                }
                TextBlockSpacer()
            }
            Text(text = gloss)
            Examples(examples = examplesText)
            if (synonyms != null || antonyms != null) {
                TextBlockSpacer(height = 4.dp)
            }
            RelatedWords(
                labelRes = R.string.short_syn_label,
                relatedWords = synonyms
            )
            RelatedWords(
                labelRes = R.string.short_ant_label,
                relatedWords = antonyms
            )
            Row {
                Language(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    lang = lang
                )
                Spacer(modifier = Modifier.weight(1f))
                FavoriteButton(isFavorite, isFavoriteDef)
            }
        }
    }
}

@Composable
private fun FavoriteButton(isFavorite: Boolean, onCheckedChange: (Boolean) -> Unit) {
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = onCheckedChange
    ) {
        FavoriteIcon(isFavorite)
    }
}


@Composable
private fun Pos(modifier: Modifier, pos: AnnotatedString) {
    Text(
        modifier = modifier,
        text = pos,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Light,
        textAlign = TextAlign.Right
    )
}

@Composable
private fun Examples(
    examples: AnnotatedString?
) {
    examples ?: return
    TextBlockSpacer()
    Text(
        text = examples,
        fontStyle = FontStyle.Italic,
        color = Color.Gray,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        maxLines = 3
    )
}

@Composable
private fun RelatedWords(
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int,
    relatedWords: AnnotatedString?
) {
    relatedWords ?: return
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Text(
            text = stringResource(id = labelRes),
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = relatedWords,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun TextBlockSpacer(height: Dp = 8.dp) = Spacer(modifier = Modifier.height(height = height))

