fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            val a = it.first { c -> c.isDigit() }
            val b = it.last { c -> c.isDigit() }
            "$a$b".toInt()
        }
    }

    val numbers = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .onEach { print("$it -> ") }
            .map { line ->
                val matcher = "(one|two|three|four|five|six|seven|eight|nine|\\d)".toPattern()
                    .matcher(line)
                var start = 0
                val nums = mutableListOf<String>()
                while (matcher.find(start)) {
                    val group = matcher.group()
                    nums.add(numbers.getOrDefault(group, group))
                    start = matcher.start(1) + 1
                }
                print("$nums --> ")
                "${nums.first()}${nums.last()}".toInt()
            }
            .onEach { println(it) }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
