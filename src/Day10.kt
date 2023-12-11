import TileType.*

fun main() {
    fun part1(input: List<String>): Int {
        val steps = input.parse().computeSteps()
        steps.println()
        return steps.values.max()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
//    check(part2(testInput) == 2)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}

private fun Map<Pair<Int, Int>, TileType>.computeSteps(): Map<Pair<Int, Int>, Int> {
    val start = entries.first { (_, type) -> type == TileType.START }.key
    val steps = mutableMapOf<Pair<Int, Int>, Int>()
    var next = listOf(start)
    var counter = 0
    while (next.any { !steps.keys.contains(it) }) {
        next.forEach { steps[it] = counter }
        next = next
            .flatMap { computeNext(it) }
            .filter { !steps.keys.contains(it) }
        counter += 1
    }
    return steps
}

private fun Map<Pair<Int, Int>, TileType>.computeNext(current: Pair<Int, Int>): List<Pair<Int, Int>> {
    val (row, column) = current
    val type = this[current]!!
    return when (type) {
        VERTICAL -> listOf(Pair(row - 1, column), Pair(row + 1, column))
        HORIZONTAL -> listOf(Pair(row, column - 1), Pair(row, column + 1))
        NORTH_EAST -> listOf(Pair(row - 1, column), Pair(row, column + 1))
        NORTH_WEST -> listOf(Pair(row - 1, column), Pair(row, column - 1))
        SOUTH_WEST -> listOf(Pair(row + 1, column), Pair(row, column - 1))
        SOUTH_EAST -> listOf(Pair(row + 1, column), Pair(row, column + 1))
        GROUND -> emptyList()
        START -> buildList {
            val north = Pair(row - 1, column)
            val west = Pair(row, column - 1)
            val south = Pair(row + 1, column)
            val east = Pair(row, column + 1)
            this@computeNext[north]?.let { type ->
                if (type in listOf(VERTICAL, SOUTH_EAST, SOUTH_WEST)) {
                    add(north)
                }
            }
            this@computeNext[west]?.let { type ->
                if (type in listOf(HORIZONTAL, NORTH_EAST, SOUTH_EAST)) {
                    add(west)
                }
            }
            this@computeNext[south]?.let { type ->
                if (type in listOf(VERTICAL, NORTH_EAST, NORTH_WEST)) {
                    add(south)
                }
            }
            this@computeNext[east]?.let { type ->
                if (type in listOf(HORIZONTAL, NORTH_WEST, SOUTH_WEST)) {
                    add(east)
                }
            }
        }
    }.filter { keys.contains(it) }
}

private fun List<String>.parse(): Map<Pair<Int, Int>, TileType> {
    return flatMapIndexed { row, line ->
        line.mapIndexedNotNull { column, type ->
            val tileType = when (type) {
                'S' -> TileType.START
                '|' -> VERTICAL
                '-' -> TileType.HORIZONTAL
                'L' -> TileType.NORTH_EAST
                'J' -> TileType.NORTH_WEST
                '7' -> TileType.SOUTH_WEST
                'F' -> TileType.SOUTH_EAST
                '.' -> TileType.GROUND
                else -> throw RuntimeException("unknown map tile $type")
            }
            Pair(row, column) to tileType
        }
    }.associate { it }
}

private enum class TileType {
    VERTICAL, HORIZONTAL, NORTH_EAST, NORTH_WEST, SOUTH_WEST, SOUTH_EAST, GROUND, START
}
