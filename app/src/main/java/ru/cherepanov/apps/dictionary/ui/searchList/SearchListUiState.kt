package ru.cherepanov.apps.dictionary.ui.searchList

import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef

data class SearchListUiState(
    val wordTitle: String = "",
    val shortDefs: List<FormattedWordDef> = emptyList(),
    val defId: DefId? = null,
    val title: String? = null
)