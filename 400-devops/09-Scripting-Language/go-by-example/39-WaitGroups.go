package main

import (
	"fmt"
	"sync"
	"time"
)

func worker3(id int) {
	fmt.Printf("worker3 %d starting\n", id)

	time.Sleep(time.Second)
	fmt.Printf("worker3 %d done\n", id)
}

func main() {

	var wg sync.WaitGroup

	for i := 1; i <= 5; i++ {
		wg.Add(1)

		i := i

		go func() {
			defer wg.Done()
			worker3(i)
		}()
	}

	wg.Wait()

}
