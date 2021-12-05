package main

import (
	"bufio"
	"fmt"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

func main() {
	filePath, err := filepath.Abs("src/main/resources/d3/input.txt")
	file, err := os.Open(filePath)
	if err != nil {
		fmt.Println(err)
	}

	var report []string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		measure := scanner.Text()
		report = append(report, measure)
	}
	processDay3Part1(report)
	processDay3Part2(report)
}

func processDay3Part1(report []string) {
	commonBits := mostCommonBits(report)
	gamma, _ := strconv.ParseInt(commonBits, 2, 64)
	fmt.Println("gamma   rate : ", strconv.FormatInt(gamma, 2), " (decimal : ", gamma, ")")
	epsilon := 0xFFF ^ gamma
	fmt.Println("epsilon rate : ", strconv.FormatInt(epsilon, 2), " (decimal : ", epsilon, ")")
	fmt.Println("Multiplication : ", gamma * epsilon)
	fmt.Println()
}

func processDay3Part2(report []string) {
	oxygen := processingDay3Part2("oxygen", report, filterOxygenRate)
	carbon := processingDay3Part2("CO2", report, filterCarbonRate)
	fmt.Println("Multiplication : ", oxygen * carbon)
}

func filterOxygenRate(input string, pos int, commonBit string) bool {
	return string(input[pos]) == commonBit
}

func filterCarbonRate(input string, pos int, commonBit string) bool {
	return string(input[pos]) != commonBit
}

func processingDay3Part2(rateName string, report []string, filter func(input string, pos int, commonBit string) bool) int64 {
	var pos = 0
	for ok := true; ok; ok = pos < 12 && len(report) > 1 {
		commonBit := mostCommonBit(report, pos)
		report = Filter(report, func(input string) bool {
			return filter(input, pos, commonBit)
		})
		pos++
	}
	rate, _ := strconv.ParseInt(report[0], 2, 64)
	fmt.Println(rateName, " rate : ", strconv.FormatInt(rate, 2), " (decimal : ", rate, ")")
	return rate
}

func Filter(arr []string, cond func(string) bool) []string {
	var result []string
	for i := range arr {
		if cond(arr[i]) {
			result = append(result, arr[i])
		}
	}
	return result
}

func mostCommonBits(list []string) string {
	sums := make([]int, 12)
	for _, v := range list {
		for pos, char := range v {
			bit, _ := strconv.Atoi(string(char))
			sums[pos] = bit + sums[pos]
		}
	}
	var mostCommonBits [12]string
	for pos, sum := range sums {
		if sum >= len(list) / 2 {
			mostCommonBits[pos] = "1"
		} else {
			mostCommonBits[pos] = "0"
		}
	}
	return strings.Join(mostCommonBits[:], "")
}

func mostCommonBit(list []string, position int) string {
	var sum = 0.
	for _, v := range list {
		bit, _ := strconv.ParseFloat(string(v[position]), 64)
		sum = bit + sum
	}

	var halfListSize = float64(len(list)) / 2.
	if sum >= halfListSize {
		return "1"
	} else {
		return "0"
	}
}
