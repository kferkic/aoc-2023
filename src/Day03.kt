fun main() {
    fun part1(input: List<String>): Int {
        return Engine.fromLines(input)
            .parts
            .sumOf { it.number }
    }

    fun part2(input: List<String>): Int {
        return Engine.fromLines(input)
            .gears
            .sumOf { it.ratio }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

private fun String.findEngineSymbols(row: Int): Sequence<EngineSymbol> {
    return "([^\\d.])".toRegex()
        .findAll(this)
        .mapNotNull { m ->
            val group = m.groups[1] ?: return@mapNotNull null
            EngineSymbol(
                symbol = group.value.first(),
                row = row,
                column = group.range.first,
            )
        }
}

private class Engine(
    private val symbols: Map<Pair<Int, Int>, EngineSymbol>,
    private val numbers: List<SchematicNumber>,
) {

    val parts = numbers.filter { n ->
        n.neighbors.any { symbols.containsKey(it) }
    }
    val gears: List<Gear>

    init {
        val nums = numbers
            .flatMap { n ->
                n.columns.map { c -> Pair(n.row, c) to n }
            }
            .associate { it }
        gears = symbols.values.asSequence()
            .filter { it.symbol == '*' }
            .mapNotNull { s ->
                val numNeighbors = s.neighbors
                    .mapNotNull { nums[it] }
                    .distinct()
                when (numNeighbors.size) {
                    2 -> Gear(numNeighbors.first().number * numNeighbors.last().number)
                    else -> null
                }
            }
            .toList()
    }

    companion object {
        fun fromLines(lines: List<String>): Engine {
            val schematic = lines.asSequence()
                .flatMapIndexed { row: Int, line: String ->
                    sequence {
                        yieldAll(line.findEngineSymbols(row))
                        yieldAll(line.findNumbers(row))
                    }
                }
                .toList()
            val symbols = schematic.mapNotNull { it as? EngineSymbol }
                .associateBy { Pair(it.row, it.column) }

            val numbers = schematic.mapNotNull { it as? SchematicNumber }

            return Engine(symbols, numbers)
        }

    }
}

private data class Gear(
    val ratio: Int,
)

private data class EngineSymbol(
    val symbol: Char,
    val row: Int,
    val column: Int,
)

private val EngineSymbol.neighbors: List<Pair<Int, Int>>
    get() = listOf(
        Pair(row - 1, column - 1),
        Pair(row - 1, column),
        Pair(row - 1, column + 1),
        Pair(row, column - 1),
        Pair(row, column + 1),
        Pair(row + 1, column - 1),
        Pair(row + 1, column),
        Pair(row + 1, column + 1),
    )


private fun String.findNumbers(row: Int): Sequence<SchematicNumber> {
    return "(\\d+)".toRegex()
        .findAll(this)
        .mapNotNull { m ->
            val group = m.groups[1] ?: return@mapNotNull null
            SchematicNumber(
                number = group.value.toInt(),
                row = row,
                columns = group.range,
            )
        }
}

private data class SchematicNumber(
    val number: Int,
    val row: Int,
    val columns: IntRange,
)

private val SchematicNumber.neighbors: List<Pair<Int, Int>>
    get() {
        val rowAbove = columns.map { c -> Pair(row - 1, c) }
        val rowBelow = columns.map { c -> Pair(row + 1, c) }
        val sidesRange = IntRange(row - 1, row + 1)
        val leftSide = sidesRange.map { r -> Pair(r, columns.first - 1) }
        val rightSide = sidesRange.map { r -> Pair(r, columns.last + 1) }
        return rowAbove + rowBelow + leftSide + rightSide
    }