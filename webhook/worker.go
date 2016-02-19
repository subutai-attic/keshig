package main

var (
	MaxWorker = 10
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
	return Worker{
		WorkerPool:  workerPool,
		TaskChannel: make(chan Task),
		quit:        make(chan bool),
	}
}

func (w Worker) Start() {
	go func() {
		w.WorkerPool <- w.TaskChannel
		select {
		//start management update
		case task := <-w.TaskChannel:
			if err := task.GitHubPushEvent.UpdateMng(); err != nil {
				log.Errorf("Error updating management container", err.Error())
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
