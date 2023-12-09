fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.toHistory() }
            .sumOf {
                it.predictNext().extrapolate()
            }
    }

    fun part2(input: List<String>): Int {
        val extrapolated = input
            .map { it.toHistory() }
            .sumOf {
                it.predictNext().extrapolateBackwards()
            }
        extrapolated.println()
        return extrapolated
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

private fun String.toHistory(): List<Int> {
    return split(" ")
        .map { it.trim().toInt() }
}

private fun List<Int>.predictNext(): List<List<Int>> {
    val diffs = mutableListOf<List<Int>>()
    var current = this
    while (!current.all { it == 0 }) {
        val diff = current.windowed(2, 1)
            .map { w ->
                (w.last() - w.first())
            }
        diffs.add(diff)
        current = diff
    }
    val seq = (listOf(this) + diffs)

    seq.forEachIndexed { index, l ->
        l.joinToString(separator = " ", prefix = "".padStart(index)).println()
    }
    print("\n")

    return seq
}

fun List<List<Int>>.extrapolate(): Int {
    return this.reversed()
        .fold(0) { acc, l ->
            (l.lastOrNull() ?: 0) + acc
        }
}

fun List<List<Int>>.extrapolateBackwards(): Int {
    return this.reversed()
        .fold(0) { acc, l ->
            (l.firstOrNull() ?: 0) - acc
        }
}