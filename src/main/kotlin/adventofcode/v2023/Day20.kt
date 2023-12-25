package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedDeque

private val stack: Queue<Signal> = ConcurrentLinkedDeque()

private data class Signal(val value: Int, val from: Module, val to: Module)

private enum class ModuleType {
    FLIP_FLOP,
    CONJUNCTION,
    BROADCAST
}

private interface Module {
    val name: String
    val previous: MutableList<Module>
    val next: MutableList<Module>

    fun handle(signal: Signal)
    fun reset()

}

private class FlipFlop(
    override val name: String,
    override val previous: MutableList<Module> = mutableListOf(),
    override val next: MutableList<Module> = mutableListOf()
) : Module {

    private var state = 0

    override fun handle(signal: Signal) {
        if (signal.value == 0) {
            state = (state + 1) % 2
            next.forEach { child -> stack.add(Signal(state, this, child)) }
        }
    }

    override fun reset() {
        state = 0
    }

}

private class Conjunction(
    override val name: String,
    override val previous: MutableList<Module> = mutableListOf(),
    override val next: MutableList<Module> = mutableListOf()
) : Module {

    private val state = mutableMapOf<String, Int>()
    var pulses = 0

    fun initState() {
        previous.forEach { i -> state[i.name] = 0 }
    }

    private fun getNextPulse(): Int {
        return when (state.values.all { p -> p == 1 }) {
            true -> 0
            else -> 1
        }
    }

    override fun handle(signal: Signal) {
        state[signal.from.name] = signal.value
        val pulse = getNextPulse()
        pulses += pulse
        next.forEach { child -> stack.add(Signal(pulse, this, child)) }
    }

    override fun reset() {
        pulses = 0
        initState()
    }

}

private class Broadcast(
    override val name: String,
    override val previous: MutableList<Module> = mutableListOf(),
    override val next: MutableList<Module> = mutableListOf()
) : Module {

    override fun handle(signal: Signal) {
        next.forEach { child -> stack.add(Signal(signal.value, this, child)) }
    }

    override fun reset() {
        // nothing to do
    }

}

private class Untyped(
    override val name: String,
    override val previous: MutableList<Module> = mutableListOf(),
    override val next: MutableList<Module> = mutableListOf()
) : Module {
    override fun handle(signal: Signal) {
        // Nothing to do
    }

    override fun reset() {
        // Nothing to do
    }

}

fun main() {

    fun part1(broadcaster: Module): Int {
        var lowCount = 0
        var highCount = 0
        repeat(1000) {
            val (stepLowCount, stepHighCount) = pressButton(broadcaster)
            lowCount += stepLowCount
            highCount += stepHighCount
        }
        return lowCount * highCount
    }

    fun part2(modules: Map<String, Module>): Long {
        val broadcaster = modules.getValue("broadcaster")
        val previous = modules.getValue("rx").previous
            .first().previous
            .filterIsInstance<Conjunction>()

        val counts = previous.associate { p -> p.name to 0L }.toMutableMap()
        for (p in previous) {
            var i = 0L
            modules.forEach { (_, m) -> m.reset() }
            while (p.pulses == 0) {
                pressButton(broadcaster)
                i++
            }
            counts[p.name] = i
        }

        return counts.values.fold(1L) { v, acc -> acc * v }
    }

    val input = readInput("v2023/d20/input")
    val modules = parse(input)

    part1(modules.getValue("broadcaster")).println()
    part2(modules).println()
}

private fun pressButton(broadcaster: Module): Pair<Int, Int> {
    var lowCount = 0
    var highCount = 0
    stack.add(Signal(0, Untyped("button"), broadcaster))
    while (stack.isNotEmpty()) {
        val signal = stack.remove()
        if (signal.value == 0) {
            lowCount++
        } else {
            highCount++
        }
        signal.to.handle(signal)
    }
    return Pair(lowCount, highCount)
}

private fun parse(input: List<String>): Map<String, Module> {
    val modulesByKey = input.asSequence()
        .map { line -> line.split(" -> ") }
        .map { split ->
            val next = split[1].split(", ")
            return@map when {
                split[0] == "broadcaster" -> split[0] to Pair(ModuleType.BROADCAST, next)
                split[0].startsWith("&") -> split[0].substring(1) to Pair(ModuleType.CONJUNCTION, next)
                split[0].startsWith("%") -> split[0].substring(1) to Pair(ModuleType.FLIP_FLOP, next)
                else -> error("")
            }
        }
        .toMap()

    val modules = modulesByKey.asSequence()
        .map { e ->
            when (e.value.first) {
                ModuleType.FLIP_FLOP -> e.key to FlipFlop(e.key)
                ModuleType.CONJUNCTION -> e.key to Conjunction(e.key)
                ModuleType.BROADCAST -> e.key to Broadcast(e.key)
            }
        }
        .toMap()
        .toMutableMap()

    modules["rx"] = Untyped("rx")

    modulesByKey.forEach { (name, pair) ->
        pair.second
            .forEach { child -> modules.getValue(name).next.add(modules.getOrDefault(child, Untyped(child))) }
    }

    modules
        .forEach { (name, _) ->
            val inputs = modulesByKey.filter { e -> e.value.second.contains(name) }.map { i -> modules.getValue(i.key) }
            val module = modules.getValue(name)
            module.previous.addAll(inputs)
            if (module is Conjunction) {
                module.initState()
            }
        }

    return modules
}
