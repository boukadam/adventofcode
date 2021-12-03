package main

import (
	"bufio"
	"fmt"
	"os"
	"path/filepath"
	"regexp"
	"strconv"
)

type Instruction struct {
	Command 	string
	Units		int
}

type Position struct {
	Horizontal	int
	Depth		int
	Aim			int
}

func main() {
	filePath, err := filepath.Abs("src/main/resources/d2/input.txt")
	file, err := os.Open(filePath)
	if err != nil {
		fmt.Println(err)
	}

	r := regexp.MustCompile("^(forward|up|down)\\s(\\d)$")
	scanner := bufio.NewScanner(file)
	var instructions []Instruction
	for scanner.Scan() {
		submatch := r.FindStringSubmatch(scanner.Text())
		units, _ := strconv.Atoi(submatch[2])
		instructions = append(instructions, Instruction{submatch[1], units})
	}

	var position = Position{0, 0, -1}
	processDay2(instructions, position)
	
	position = Position{0, 0, 0}
	processDay2(instructions, position)
}

func processDay2(instructions []Instruction, position Position) {
	for _, instruction := range instructions {
		position = processInstruction(instruction, position)
	}
	fmt.Println("Position ", position)
	fmt.Println("Multiplication ", position.Horizontal * position.Depth)
}

func processInstruction(instruction Instruction, position Position) Position {
	switch instruction.Command {
	case "forward":
		position.Horizontal += instruction.Units
		if position.Aim >= 0 {
			position.Depth += position.Aim * instruction.Units
		}
	case "up":
		if position.Aim >= 0 {
			position.Aim -= instruction.Units
		} else {
			position.Depth -= instruction.Units
		}
	case "down":
		if position.Aim >= 0 {
			position.Aim += instruction.Units
		} else {
			position.Depth += instruction.Units
		}
	}
	return position
}
