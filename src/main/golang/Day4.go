package main

import (
	"bufio"
	"fmt"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

type Point struct {
	number		int
	marked		bool
}

func main() {
	filePath, err := filepath.Abs("src/main/resources/d4/input.txt")
	file, err := os.Open(filePath)
	if err != nil {
		fmt.Println(err)
	}

	var draw []int
	var boards [][5][5]Point
	scanner := bufio.NewScanner(file)
	var i = 0
	for scanner.Scan() {
		line := scanner.Text()
		if i == 0 {
			for _, n := range strings.Split(line, ",") {
				number, _ := strconv.Atoi(n)
				draw = append(draw, number)
			}
			i++
			continue
		}
		boards = append(boards, parseBoard(scanner))
		i++
	}

	var winners []int
	for t, d := range draw {
		fmt.Println("Draw", t, ":", d, "...")
		for b, _ := range boards {
			for x, h := range boards[b] {
				for y, cell := range h {
					if d == cell.number {
						boards[b][x][y].marked = true
					}
				}
			}
			if !contains(winners, b) && validateBoard(boards[b]) {
				fmt.Println("Winner ", len(winners) + 1, "score", computeScore(boards[b]) * d)
				fmt.Println()
				winners = append(winners, b)
			}
		}
		if len(winners) == len(boards) {
			break
		}
	}

}

func parseBoard(scanner* bufio.Scanner) [5][5]Point {
	var board [5][5]Point
	var i = 0
	for i < 5 && scanner.Scan() {
		line := scanner.Text()
		if line == "" {
			i++
			continue
		}
		split := strings.Split(line, " ")
		for j, n := range deleteEmpty(split) {
			number, _ := strconv.Atoi(n)
			board[i][j] = Point{number, false}
		}
		i++
	}
	return board
}

func deleteEmpty(s []string) []string {
	var r []string
	for _, str := range s {
		if str != "" {
			r = append(r, str)
		}
	}
	return r
}

func validateBoard(board [5][5]Point) bool {
	for i := range []int{0,1,2,3,4} {
		var nbMarkedRow = 0
		var nbMarkedCol = 0
		for j := range []int{0,1,2,3,4} {
			if board[i][j].marked {
				nbMarkedRow++
			}
			if board[j][i].marked {
				nbMarkedCol++
			}

			if nbMarkedRow == 5 || nbMarkedCol == 5 {
				return true
			}
		}
	}
	return false
}

func computeScore(board [5][5]Point) int {
	var score = 0
	for _, h := range board {
		for _, cell := range h {
			if !cell.marked {
				score += cell.number
			}
		}
	}
	return score
}

func printBoard(board [5][5]Point) {
	for _, h := range board {
		fmt.Println(h)
	}
}

func contains(s []int, e int) bool {
	for _, a := range s {
		if a == e {
			return true
		}
	}
	return false
}