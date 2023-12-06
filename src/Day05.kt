import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val almanac = input.toAlmanac()
        val locations = almanac.seeds.asSequence()
            .onEach { print("seed($it)") }
            .map { almanac.seedToSoil(it) }
            .onEach { print("->soil($it)") }
            .map { almanac.soilToFertilizer(it) }
            .onEach { print("->fertilizer($it)") }
            .map { almanac.fertilizerToWater(it) }
            .onEach { print("->water($it)") }
            .map { almanac.waterToLight(it) }
            .onEach { print("->light($it)") }
            .map { almanac.lightToTemperature(it) }
            .onEach { print("->temp($it)") }
            .map { almanac.temperatureToHumidity(it) }
            .onEach { print("->humidity($it)") }
            .map { almanac.humidityToLocation(it) }
            .onEach { println("->location($it)") }
            .toList()

        println(locations)
        return locations.min().toInt()
    }

    fun part2(input: List<String>): Long {
        val almanac = input.toAlmanac()
        var min = Long.MAX_VALUE
        val ranges = almanac.seeds.windowed(2, 2)
        ranges
            .forEachIndexed { index, seedRange ->
                val rangeStart = seedRange.first()
                val len = seedRange.last()
                val rangeEnd = rangeStart + len
                var seed = rangeStart
                var p = 0
                print("[${index + 1}/${ranges.size}] seed-range: $seed..<$rangeEnd\t($len)\t")
                while (seed < rangeEnd) {
                    val soil = almanac.seedToSoil(seed)
                    val fertilizer = almanac.soilToFertilizer(soil)
                    val water = almanac.fertilizerToWater(fertilizer)
                    val light = almanac.waterToLight(water)
                    val temp = almanac.lightToTemperature(light)
                    val humidity = almanac.temperatureToHumidity(temp)
                    val location = almanac.humidityToLocation(humidity)
                    min = min(min, location)
                    val newP = (100 * (seed - rangeStart) / len).toInt()
                    if (newP >= p + 10) {
                        p = newP
                        print("...$p%")
                    }
                    seed += 1
                }
                println("...100%\t\tmin:$min")
            }
        return min
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

private fun List<String>.toAlmanac(): Almanac {
    val seeds = "seeds: (.*)".toRegex()
        .matchEntire(first { it.startsWith("seeds:") })
        ?.let { m ->
            val (seedsStr) = m.destructured
            seedsStr.split(" ").map { it.trim().toLong() }
        } ?: throw RuntimeException("can't parse seeds")

    fun mapSrcDst(line: String): Pair<LongRange, LongRange> {
        return "(\\d+) (\\d+) (\\d+)".toRegex()
            .matchEntire(line)
            ?.let { m ->
                val (dstStartStr, srcStartStr, lenStr) = m.destructured
                val srcStart = srcStartStr.toLong()
                val dstStart = dstStartStr.toLong()
                val len = lenStr.toLong()
                srcStart..<srcStart + len to dstStart..<dstStart + len
            } ?: throw RuntimeException("can't parse line: $line")
    }

    val seedToSoil = asSequence().dropWhile { it != "seed-to-soil map:" }.drop(1).takeWhile { it.isNotBlank() }
        .map { mapSrcDst(it) }
        .associate { it }
    val soilToFertilizer =
        asSequence().dropWhile { it != "soil-to-fertilizer map:" }.drop(1).takeWhile { it.isNotBlank() }
            .map { mapSrcDst(it) }
            .associate { it }
    val fertilizerToWater =
        asSequence().dropWhile { it != "fertilizer-to-water map:" }.drop(1).takeWhile { it.isNotBlank() }
            .map { mapSrcDst(it) }
            .associate { it }
    val waterToLight = asSequence().dropWhile { it != "water-to-light map:" }.drop(1).takeWhile { it.isNotBlank() }
        .map { mapSrcDst(it) }
        .associate { it }
    val lightToTemperature =
        asSequence().dropWhile { it != "light-to-temperature map:" }.drop(1).takeWhile { it.isNotBlank() }
            .map { mapSrcDst(it) }
            .associate { it }
    val temperatureToHumidity =
        asSequence().dropWhile { it != "temperature-to-humidity map:" }.drop(1).takeWhile { it.isNotBlank() }
            .map { mapSrcDst(it) }
            .associate { it }
    val humidityToLocation =
        asSequence().dropWhile { it != "humidity-to-location map:" }.drop(1).takeWhile { it.isNotBlank() }
            .map { mapSrcDst(it) }
            .associate { it }

    return Almanac(
        seeds = seeds,
        seedToSoil = seedToSoil,
        soilToFertilizer = soilToFertilizer,
        fertilizerToWater = fertilizerToWater,
        waterToLight = waterToLight,
        lightToTemperature = lightToTemperature,
        temperatureToHumidity = temperatureToHumidity,
        humidityToLocation = humidityToLocation,
    )
}

private class Almanac(
    val seeds: List<Long>,
    private val seedToSoil: Map<LongRange, LongRange>,
    private val soilToFertilizer: Map<LongRange, LongRange>,
    private val fertilizerToWater: Map<LongRange, LongRange>,
    private val waterToLight: Map<LongRange, LongRange>,
    private val lightToTemperature: Map<LongRange, LongRange>,
    private val temperatureToHumidity: Map<LongRange, LongRange>,
    private val humidityToLocation: Map<LongRange, LongRange>,
) {

    val seedRange: Sequence<Long>
        get() = seeds.asSequence().windowed(2, 2)
            .flatMap { window ->
                val start = window.first()
                (start..<start + window.last()).map { it }
            }

    private fun Map<LongRange, LongRange>.dst(src: Long): Long {
        val (srcRange, dstRange) = entries.firstOrNull { (k, _) -> k.contains(src) } ?: return src
        return src - srcRange.first + dstRange.first
    }

    fun seedToSoil(seed: Long): Long {
        return seedToSoil.dst(seed)
    }

    fun soilToFertilizer(seed: Long): Long {
        return soilToFertilizer.dst(seed)
    }

    fun fertilizerToWater(seed: Long): Long {
        return fertilizerToWater.dst(seed)
    }

    fun waterToLight(seed: Long): Long {
        return waterToLight.dst(seed)
    }

    fun lightToTemperature(seed: Long): Long {
        return lightToTemperature.dst(seed)
    }

    fun temperatureToHumidity(seed: Long): Long {
        return temperatureToHumidity.dst(seed)
    }

    fun humidityToLocation(seed: Long): Long {
        return humidityToLocation.dst(seed)
    }
}