import CamelHandType.*

fun main() {
    fun part1(input: List<String>): Int {
        return input.asSequence()
            .map { it.toCamelHand(false) }
            .sorted()
            .mapIndexed { index, camelHand ->
                (index + 1) * camelHand.bid
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { it.toCamelHand(true) }
            .sorted()
            .mapIndexed { index, camelHand ->
                (index + 1) * camelHand.bid
            }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

private fun String.toCamelHand(withJoker: Boolean): CamelHand {
    val (cardsStr, bid) = split(" ")
    return CamelHand(cardsStr.toCards(withJoker), bid.toInt())
}

private fun String.toCards(withJoker: Boolean): List<CamelCard> {
    return map { c ->
        val value = when (c) {
            '2' -> 2
            '3' -> 3
            '4' -> 4
            '5' -> 5
            '6' -> 6
            '7' -> 7
            '8' -> 8
            '9' -> 9
            'T' -> 10
            'J' -> 11
            'Q' -> 12
            'K' -> 13
            'A' -> 14
            else -> throw RuntimeException("Unknown card: $c")
        }
        when {
            c == 'J' && withJoker -> CamelCard(c, 1, true)
            else -> CamelCard(c, value)
        }
    }
}

private data class CamelHand(
    val cards: List<CamelCard>,
    val bid: Int,
) : Comparable<CamelHand> {

    val type: CamelHandType
        get() {
            val cardsGroup = cards.groupBy { it.name }
            val type = when (cardsGroup.size) {
                1 -> FIVE_OF_A_KIND
                2 -> when {
                    cardsGroup.any { (_, v) -> v.size == 4 } -> FOUR_OF_A_KIND
                    else -> FULL_HOUSE
                }

                3 -> when {
                    cardsGroup.any { (_, v) -> v.size == 3 } -> THREE_OF_A_KIND
                    else -> TWO_PAIR
                }

                4 -> ONE_PAIR
                else -> HIGH_CARD
            }
            return if (cards.any { it.joker }) {
                val jokers = cardsGroup['J']?.size ?: 0
                when (type) {
                    HIGH_CARD -> ONE_PAIR
                    ONE_PAIR -> THREE_OF_A_KIND

                    TWO_PAIR -> when (jokers) {
                        2 -> FOUR_OF_A_KIND
                        else -> FULL_HOUSE
                    }

                    THREE_OF_A_KIND -> FOUR_OF_A_KIND
                    FULL_HOUSE -> FIVE_OF_A_KIND
                    FOUR_OF_A_KIND -> FIVE_OF_A_KIND
                    FIVE_OF_A_KIND -> FIVE_OF_A_KIND
                }
            } else {
                type
            }
        }

    override fun compareTo(other: CamelHand): Int {
        val compareHands = type.value - other.type.value
        return if (compareHands != 0) {
            compareHands
        } else {
            cards.zip(other.cards)
                .map { (a, b) ->
                    a.value - b.value
                }
                .first { it != 0 }
        }
    }

    override fun toString(): String {
        val cards = cards.joinToString(separator = "") { it.name.toString() }
        return "CamelHand(cards=$cards, bid=$bid)"
    }

}

private enum class CamelHandType(
    val value: Int,
) {
    HIGH_CARD(1),
    ONE_PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    FULL_HOUSE(5),
    FOUR_OF_A_KIND(6),
    FIVE_OF_A_KIND(7),
}

private data class CamelCard(
    val name: Char,
    val value: Int,
    val joker: Boolean = false,
)
