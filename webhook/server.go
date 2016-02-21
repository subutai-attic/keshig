package main

import (
	"encoding/json"
	"net/http"
)

func init() {
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
	if err != nil {
		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	//create task
	task := Task{GitHubPushEvent: pushEvent}
	//push task to queue
	TaskQueue <- task
	//http response
	w.WriteHeader(http.StatusOK)
}

//main func
func main() {

	http.HandleFunc("/github/subutai/develop", PushEventHandler)
	http.ListenAndServe(":8181", nil)
}
