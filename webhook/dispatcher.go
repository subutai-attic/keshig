package main

type Dispatcher struct {
	maxWorker int 
	WorkerPool chan chan Task
	
}

func NewDispatcher(maxWorker int) *Dispatcher {

	pool := make(chan chan Task, maxWorker)

	return &Dispatcher{WorkerPool: pool}
}

func (d *Dispatcher) Run() {
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
	for {
		select {
		case task := <-TaskQueue:
			go func(task Task) {
			taskChannel :=<-d.WorkerPool
				taskChannel <- task
			}(task)
		}
	}
}
