package ru.cherepanov.apps.dictionary.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef

@Entity(tableName = "definitions")
data class WordDefEntity(
    @PrimaryKey
    @Embedded(prefix = "id_")
    val id: Id,
    val data: String,
    val isFavorite: Boolean,
    val isFull: Boolean,
    val updatedAt: Long
) {
    data class Id(
        val title: String,
        val langNum: Int,
        val senseNum: Int,
        val glossNum: Int
    )
}

@Serializable
private data class WordDefData(
    val syllables: String,
    val pos: String,
    val gloss: String,
    val examples: List<String>,
    val synonyms: String,
    val antonyms: String,
    val hyponyms: String?,
    val hypernyms: String?,
    val etymology: String?,
    val phras: List<String>?,
    val ipa: String?,
    val lang: String
)

fun WordDefEntity.mapToWordDef(): WordDef {
    val wordDefData = Json.decodeFromString<WordDefData>(data)
    return WordDef(
        id = id.mapToDefId(),
        syllables = wordDefData.syllables,
        pos = wordDefData.pos,
        gloss = wordDefData.gloss,
        examples = wordDefData.examples,
        synonyms = wordDefData.synonyms,
        antonyms = wordDefData.antonyms,
        hypernyms = wordDefData.hypernyms,
        hyponyms = wordDefData.hyponyms,
        etymology = wordDefData.etymology,
        phras = wordDefData.phras,
        ipa = wordDefData.ipa,
        lang = wordDefData.lang,
        isFavorite = isFavorite,
        isFull = isFull
    )
}

private fun WordDef.mapToWordDefData(): WordDefData {
    return WordDefData(
        syllables = syllables,
        pos = pos,
        gloss = gloss,
        examples = examples,
        synonyms = synonyms,
        antonyms = antonyms,
        hyponyms = hyponyms,
        hypernyms = hypernyms,
        etymology = etymology,
        phras = phras,
        ipa = ipa,
        lang = lang
    )
}

fun WordDef.mapToEntity(updatedAt: Long): WordDefEntity {
    return WordDefEntity(
        id = id.mapToEntityId(),
        isFavorite = isFavorite,
        isFull = isFull,
        data = Json.encodeToString(mapToWordDefData()),
        updatedAt = updatedAt
    )
}

fun DefId.mapToEntityId() = WordDefEntity.Id(
    title = title,
    langNum = langNum ?: -1,
    senseNum = senseNum ?: -1,
    glossNum = glossNum ?: -1
)

fun  WordDefEntity.Id.mapToDefId() = DefId(
    title = title,
    langNum = langNum.takeUnless { it == -1 },
    senseNum = senseNum.takeUnless { it == -1 },
    glossNum = glossNum.takeUnless { it == -1 }
)
