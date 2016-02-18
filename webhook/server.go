package main

import (
	"io"
	"log"
	"net/http"
)

func PushEventHandler(w http.ResponseWriter, r *http.Request) {

	switch r.Method {

	case "POST":
		var push_event GitHubPushEventStruct
		err := decoder.Decode(&push_event)
		if err != nil {
			log.Println("Error while decoding json")
		}

		go buildSubutai()
		//
	default:
		http.Error(w, "InvalidRequest", 405)

	}

}

//invoke script to build subutai
func buildSubutai() {

}

//publish management template with
//updates to global Kurjun
func publishToKurjun() {

}

//main func
func main() {

	http.HandleFunc("/github/subutai/develop", PushEventHandler)
	http.ListenAndServe(":80", nil)
}
