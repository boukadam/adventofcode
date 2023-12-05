package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min
import kotlin.streams.asStream

data class Data(val seedsLine: String,
                val seedToSoilLines: MutableList<String> = mutableListOf(),
                val soilToFertilizerLines : MutableList<String> = mutableListOf(),
                val fertilizerToWaterLines : MutableList<String> = mutableListOf(),
                val waterToLightLines : MutableList<String> = mutableListOf(),
                val lightToTemperatureLines : MutableList<String> = mutableListOf(),
                val temperatureToHumidityLines : MutableList<String> = mutableListOf(),
                val humidityToLocationLines : MutableList<String> = mutableListOf()
)

data class Category(val target: Long, val source: Long, val pace: Long)

fun main() {

    val input = readInput("v2023/d5/input")
    val data = parseData(input)
    val seedToSoil = parseCategories(data.seedToSoilLines)
    val soilToFertilizer = parseCategories(data.soilToFertilizerLines)
    val fertilizerToWater = parseCategories(data.fertilizerToWaterLines)
    val waterToLight = parseCategories(data.waterToLightLines)
    val lightToTemperature = parseCategories(data.lightToTemperatureLines)
    val temperatureToHumidity = parseCategories(data.temperatureToHumidityLines)
    val humidityToLocation = parseCategories(data.humidityToLocationLines)

    fun getLocation(seed: Long): Long {
        val soil = correspondance(seed, seedToSoil)
        val fertilizer = correspondance(soil, soilToFertilizer)
        val water = correspondance(fertilizer, fertilizerToWater)
        val light = correspondance(water, waterToLight)
        val temperature = correspondance(light, lightToTemperature)
        val humidity = correspondance(temperature, temperatureToHumidity)
        return correspondance(humidity, humidityToLocation)
    }

    fun part1(): Long {
        return parseSeedsPart1(data.seedsLine).asSequence()
            .map { seed -> getLocation(seed) }
            .min()
    }

    fun part2(): Long {
        val values = data.seedsLine.replace("seeds: ", "")
            .split(" ")
            .map { s -> s.toLong() }
        var min = Long.MAX_VALUE
        for (i in values.indices step 2) {
            for (v in values[i]..(values[i] + values[i+1])) {
                val location = getLocation(v)
                min = minOf(min, location)
            }
        }
        return min
    }

    part1().println()
    part2().println()
}

fun correspondance(item: Long, categories: List<Category>): Long {
    for (category in categories) {
        if (item in (category.source..<category.source + category.pace)) {
            return category.target + (item - category.source)
        }
    }
    return item
}

fun parseSeedsPart1(data: String): List<Long> {
    return data.replace("seeds: ", "")
        .split(" ")
        .map { s -> s.toLong() }
}

fun parseCategories(data: List<String>): List<Category> {
    val regexLine = Regex("^(\\d+) (\\d+) (\\d+)\$")
    return data.asSequence()
        .map(regexLine::find)
        .filterNotNull()
        .map { result -> result.destructured }
        .map { tuple -> Category(tuple.component1().toLong(), tuple.component2().toLong(), tuple.component3().toLong())}
        .toList()
}

fun parseData(input: List<String>): Data {
    val iterator = input.iterator()
    val data = Data(iterator.next())
    var currentList : MutableList<String> = mutableListOf()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next.isBlank()) {
            continue
        }
        if (next.contains("map:")) {
            if (next == "seed-to-soil map:") {
                currentList = data.seedToSoilLines
            } else if (next == "soil-to-fertilizer map:") {
                currentList = data.soilToFertilizerLines
            } else if (next == "fertilizer-to-water map:") {
                currentList = data.fertilizerToWaterLines
            } else if (next == "water-to-light map:") {
                currentList = data.waterToLightLines
            } else if (next == "light-to-temperature map:") {
                currentList = data.lightToTemperatureLines
            } else if (next == "temperature-to-humidity map:") {
                currentList = data.temperatureToHumidityLines
            } else if (next == "humidity-to-location map:") {
                currentList = data.humidityToLocationLines
            }
            continue
        }
        currentList.add(next)
    }
    return data
}