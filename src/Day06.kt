fun main() {
    fun part1(input: List<String>): Int {
        return input.toRaceRecords().asSequence()
            .map { it.computeWaysToWin(1) }
            .reduce { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Int {
        return input.toRaceRecord().computeWaysToWin(1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

private fun List<String>.toRaceRecord(): RaceRecord {
    val time = first().replace("Time:", "")
        .replace("\\s".toRegex(), "")
        .toLong()
    val distance = last().replace("Distance:", "")
        .replace("\\s".toRegex(), "")
        .toLong()
    return RaceRecord(
        time = time,
        distance = distance,
    )
}

private fun List<String>.toRaceRecords(): List<RaceRecord> {
    val times = "Time:\\s([\\d ]+)".toRegex()
        .matchEntire(first { it.startsWith("Time:") })
        ?.let { m ->
            val (times) = m.destructured
            times.split(" ")
                .filter { it.isNotBlank() }
                .map { it.trim().toLong() }
        } ?: throw RuntimeException("Can't parse time-line")
    val distances = "Distance:\\s([\\d ]+)".toRegex()
        .matchEntire(first { it.startsWith("Distance:") })
        ?.let { m ->
            val (distances) = m.destructured
            distances.split(" ")
                .filter { it.isNotBlank() }
                .map { it.trim().toLong() }
        } ?: throw RuntimeException("Can't parse distance-line")

    return times.mapIndexed { index, time ->
        RaceRecord(
            time = time,
            distance = distances[index],
        )
    }
}

private fun RaceRecord.computeWaysToWin(speedInc: Int): Int {
    var waysToWin = 0
    var buttonTime = 1
    while (buttonTime < time) {
        val speed = speedInc * buttonTime
        val timeAfterButtonRelease = time - buttonTime
        if (speed * timeAfterButtonRelease > distance) {
            waysToWin += 1
        }
        buttonTime += 1
    }
    return waysToWin
}

private data class RaceRecord(
    val time: Long,
    val distance: Long,
)
