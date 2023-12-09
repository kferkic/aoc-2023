fun main() {
    fun part1(input: List<String>): Int {
        val desertMap = input.toDesertMap()
        val instructions = desertMap.instructions
        val nodes = desertMap.nodes
        var steps = 0
        var currentNode = nodes["AAA"]
        while (true) {
            val direction = instructions[steps % instructions.length]
            steps += 1
            currentNode = when (direction) {
                'L' -> nodes[currentNode?.left]
                else -> nodes[currentNode?.right]
            }
            if (currentNode?.name == "ZZZ") {
                break
            }
        }
        return steps
    }

    fun part2(input: List<String>): Long {
        val desertMap = input.toDesertMap()
        val instructions = desertMap.instructions
        val nodes = desertMap.nodes
        var steps = 0L
        var currentNodes = nodes.values
            .filter { it.name.endsWith("A") }
        println("initial nodes: $currentNodes")
        while (true) {
            val idx = steps % instructions.length
            val direction = instructions[idx.toInt()]
            steps += 1

            print("$steps ($direction): ${currentNodes.joinToString { it.name }}")

            currentNodes = currentNodes
                .map { currentNode ->
                    when (direction) {
                        'L' -> nodes[currentNode.left]!!
                        'R' -> nodes[currentNode.right]!!
                        else -> throw RuntimeException("node not found")
                    }
                }

            println("  -->  ${currentNodes.joinToString { it.name }}")
            if (currentNodes.all { it.name.endsWith("Z") }) {
                break
            }
        }
        return steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 6)
    check(part2(readInput("Day08_test_part2")) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}


private fun List<String>.toDesertMap(): DesertMap {
    val instructions = first()
    val nodes = mapNotNull { line ->
        "([A-Z0-9]{3}) = \\(([A-Z0-9]{3}), ([A-Z0-9]{3})\\)"
            .toRegex()
            .matchEntire(line)
            ?.let { m ->
                val (node, left, right) = m.destructured
                DesertNode(node, left, right)
            }
    }.associateBy { it.name }
    return DesertMap(instructions, nodes)
}

private data class DesertMap(
    val instructions: String,
    val nodes: Map<String, DesertNode>,
)

private data class DesertNode(
    val name: String,
    val left: String,
    val right: String,
)