package main

import (
	"fmt"
)

type Dispatcher struct {
	maxWorker  int
	WorkerPool chan chan Task
}

func NewDispatcher(maxWorker int) *Dispatcher {

	pool := make(chan chan Task, maxWorker)

	return &Dispatcher{WorkerPool: pool,
		maxWorker: maxWorker}
}

func (d *Dispatcher) Run() {
	fmt.Printf("Starting dispatcher\n")
	fmt.Printf("Adding workers : %d \n", d.maxWorker)
	//init with N number of workers
	for i := 0; i < d.maxWorker; i++ {
		//create worker
		worker := NewWorker(d.WorkerPool)
		//start the worker
		worker.Start()
	}
	go d.dispatch()
}

func (d *Dispatcher) dispatch() {

	fmt.Println("Listening on channel for tasks...")

	for {
		select {
		case task := <-TaskQueue:

			fmt.Printf("Dispatching a task\n")
			fmt.Printf("Task details:%+v", task.GitHubPushEvent.Ref)

			go func(task Task) {

				fmt.Printf("Trying to obtain free worker channel\n")
				taskChannel := <-d.WorkerPool

				fmt.Printf("Dispatching task to a worker\n")
				taskChannel <- task

			}(task)
		}
	}
}
