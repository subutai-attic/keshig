package main

import (
	"fmt"
)

var (
	MaxWorker = 1
	MaxQueue  = 10
)

var TaskQueue chan Task

type Task struct {
	GitHubPushEvent GitHubPushEvent
	//TODO:Release Event
}

type Worker struct {
	WorkerPool  chan chan Task
	TaskChannel chan Task
	quit        chan bool
}

func NewWorker(workerPool chan chan Task) Worker {
	fmt.Println("Creating a new worker")

	return Worker{
		WorkerPool:  workerPool,
		TaskChannel: make(chan Task),
		quit:        make(chan bool),
	}
}

func (w Worker) Start() {

	go func() {

		fmt.Println("Registering current worker into the worker queue")

		w.WorkerPool <- w.TaskChannel

		select {
		//start management update
		case task := <-w.TaskChannel:
			fmt.Println("Received a task")
			if err := task.GitHubPushEvent.Assemble(); err != nil {
				fmt.Println("Error updating mng: " + err.Error())
			}
		//terminate worker
		case <-w.quit:
			return
		}
	}()

}

//send quit via channel
func (w Worker) Stop() {
	go func() {
		w.quit <- true
	}()
}
