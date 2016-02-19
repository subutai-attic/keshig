package main

import (
	"log"
	"net/http"
	"io/ioutil"
	"fmt"
	"encoding/json"

)

//Handle git push
func PushEventHandler(w http.ResponseWriter, r *http.Request) {

	switch r.Method {

	case "POST":
		var pushEvent GitHubPushEventStruct

		body, err:= ioutil.ReadAll(r.Body)

		if err != nil {
			log.Println("Error while decoding json")
		}
		err = json.Unmarshal(body, &pushEvent)
		branchRef:=pushEvent.Ref
		fmt.Println(branchRef)
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
	http.ListenAndServe(":8181", nil)
}