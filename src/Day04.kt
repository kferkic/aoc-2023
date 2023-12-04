fun main() {
    fun part1(input: List<String>): Int {
        return input
            .sumOf {
                it.toCard().points
            }
    }

    fun Card.copies(cards: Map<Int, Card>): List<Card> {
        return listOf(this) + ((id + 1)..(id + matches))
            .mapNotNull { cards[it] }
            .flatMap { it.copies(cards) }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { it.toCard() }.associateBy { it.id }
        return cards.values
            .flatMap {
                it.copies(cards)
            }.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

private fun String.toCard(): Card {
    return "Card\\s+(\\d+): ([\\d ]+) \\| ([\\d ]+)".toRegex()
        .matchEntire(this)
        ?.let { m ->
            val (id, winningStr, numsStr) = m.destructured
            val winning = winningStr.split(" ").mapNotNull { it.toIntOrNull() }
            val nums = numsStr.split(" ").mapNotNull { it.toIntOrNull() }
            Card(
                id = id.toInt(),
                winningNumbers = winning.toSet(),
                numbers = nums,
            )
        } ?: throw RuntimeException("Cant parse line: $this")
}

private data class Card(
    val id: Int,
    val winningNumbers: Set<Int>,
    val numbers: List<Int>,
)

private val Card.points: Int
    get() = numbers
        .filter { winningNumbers.contains(it) }
        .fold(0) { acc, _ ->
            when (acc) {
                0 -> 1
                else -> acc * 2
            }
        }

private val Card.matches: Int
    get() = numbers
        .filter { winningNumbers.contains(it) }
        .size

