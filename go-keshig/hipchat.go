package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
)

func notifyHipchat(commitID string) {
	// TODO get url from config file
	// TODO separate url, room id and auth token
	url := "https://mountainnomad.hipchat.com/v2/room/2564954/notification?auth_token=A1WrboAmDoRIc2I6j96cctuWi70HoEdibfIfSUQK"
	body, err := json.Marshal(map[string]string{
		"color":          "green",
		"message":        fmt.Sprintf("Build triggered with commit id is %s", commitID),
		"notify":         "false",
		"message_format": "text",
	})
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()
}
