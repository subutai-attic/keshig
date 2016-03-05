package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

func initDispatcher() {
	fmt.Println("Init...")
	dispatcher := NewDispatcher(MaxWorker)
	dispatcher.Run()
}

//Handle git push
func PushEventHandler(w http.ResponseWriter, r *http.Request) {

	if r.Method != "POST" {
		w.WriteHeader(http.StatusMethodNotAllowed)
	}
	//check for git headers and secret
	var pushEvent GitHubPushEvent

	err := json.NewDecoder(r.Body).Decode(&pushEvent)
	//respond with bad request on error
	fmt.Printf("Ref: %+v\n", pushEvent.Ref)
	if err != nil {
		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		fmt.Println(err.Error())
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	//http response
	w.WriteHeader(http.StatusOK)

	fmt.Printf("Creating task\n")
	//create task
	task := Task{GitHubPushEvent: pushEvent}
	//push task to queue
	fmt.Printf("Pushing task to queue\n")

	//TaskQueue <- task
	go task.GitHubPushEvent.Assemble()
}

//main func
func main() {
	initDispatcher()
	http.HandleFunc("/github/subutai/develop", PushEventHandler)
	http.ListenAndServe(":8181", nil)
}
