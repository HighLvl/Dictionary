package ru.cherepanov.apps.dictionary.ui.base

object RomanNumeral {
    private val numberNumeral = mapOf(
        (0 to ""),
        (1 to "I"),
        (2 to "II"),
        (3 to "III"),
        (4 to "IV"),
        (5 to "V"),
        (6 to "VI"),
        (7 to "VII"),
        (8 to "VIII"),
        (9 to "IX"),
        (10 to "X"),
        (40 to "XL"),
        (50 to "L"),
        (90 to "XC"),
        (100 to "C"),
        (400 to "CD"),
        (500 to "D"),
        (900 to "CM"),
        (1000 to "M")
    )

    fun value(input: Int): String {
        return numberNumeral.keys.filter { it != 0 }.sortedDescending()
            .fold((0 to "")) { result, next -> convert(input, result, next) }
            .second
    }

    private fun convert(
        input: Int,
        previous: Pair<Int, String>,
        divisor: Int
    ): Pair<Int, String> {
        require(divisor != 0)
        var s = previous.second
        val digit = (input - previous.first) / divisor
        if (divisor == 1) {
            s += numberNumeral[digit]
        } else {
            for (i in 1..digit) {
                s += numberNumeral[divisor]
            }
        }
        return Pair(previous.first + digit * divisor, s)
    }
}