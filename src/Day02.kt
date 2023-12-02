import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.toGame() }
            .filter { it.isPossible(red = 12, green = 13, blue = 14) }
            .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.toGame() }
            .map { it.minSet() }
            .sumOf {
                it.red * it.green * it.blue
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

private fun String.toGame(): Game {
    val (game, info) = split(":")
    val gameId = game.split(" ").last().toInt()
    val gameInfo = info
        .split(";")
        .map {
            val red = "(\\d+) red".toRegex().find(it)?.let { mr ->
                mr.groups[1]?.value?.toInt()
            } ?: 0
            val green = "(\\d+) green".toRegex().find(it)?.let { mr ->
                mr.groups[1]?.value?.toInt()
            } ?: 0
            val blue = "(\\d+) blue".toRegex().find(it)?.let { mr ->
                mr.groups[1]?.value?.toInt()
            } ?: 0
            GameInfo(
                red = red,
                green = green,
                blue = blue,
            )
        }
    return Game(id = gameId, info = gameInfo)
}

private fun Game.minSet(): GameInfo {
    return info
        .reduce { acc, gameInfo ->
            GameInfo(
                red = max(acc.red, gameInfo.red),
                green = max(acc.green, gameInfo.green),
                blue = max(acc.blue, gameInfo.blue),
            )
        }
}

private fun Game.isPossible(
    red: Int,
    green: Int,
    blue: Int,
): Boolean {
    val hasImpossibleInfo = info.any { !it.isPossible(red, green, blue) }
    return !hasImpossibleInfo
}

private fun GameInfo.isPossible(
    red: Int,
    green: Int,
    blue: Int,
): Boolean {
    return this.red <= red && this.green <= green && this.blue <= blue
}

data class Game(
    val id: Int,
    val info: List<GameInfo>,
)

data class GameInfo(
    val red: Int,
    val green: Int,
    val blue: Int,
)
