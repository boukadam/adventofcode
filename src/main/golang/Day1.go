package main

import (
	"bufio"
	"fmt"
	"os"
	"path/filepath"
	"strconv"
)

func main() {

	filePath, err := filepath.Abs("src/main/resources/d1/input.txt")
	file, err := os.Open(filePath)
	if err != nil {
		fmt.Println(err)
	}

	var measures []int
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		measure, _ := strconv.Atoi(scanner.Text())
		measures = append(measures, measure)
	}

	processDay1(measures)

	var threeMesures []int
	for i, _ := range measures[0: len(measures) - 2] {
		threeMesures = append(threeMesures, measures[i] + measures[i+1] + measures[i+2])
	}

	processDay1(threeMesures)
}

func processDay1(list []int) {
	var count = 0
	var prec = list[0]
	for _, val := range list[1:] {
		if prec < val {
			count++
		}
		prec = val
	}

	fmt.Println("Nb increased : ", count)
}