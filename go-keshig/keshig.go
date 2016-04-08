package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"mime/multipart"
	"net/http"
	"os"
	"os/exec"

	"github.com/gin-gonic/gin"
)

// Message is http response type
type Message struct {
	Name string
	Body string
	Time int64
}

// GitHubPushEvent is struct of GitHub push event
type GitHubPushEvent struct {
	Ref    string `json:"ref,omitempty"`
	Before string `json:"before,omitempty"`
	After  string `json:"after,omitempty"`
	Pusher struct {
		Name  string `json:"name,omitempty"`
		Email string `json:"email,omitempty"`
	} `json:"pusher,omitempty"`
	Sender struct {
		Login             string `json:"login,omitempty"`
		ID                int    `json:"id,omitempty"`
		AvatarURL         string `json:"avatar_url,omitempty"`
		GravatarID        string `json:"gravatar_id,omitempty"`
		URL               string `json:"url,omitempty"`
		HTMLURL           string `json:"html_url,omitempty"`
		FollowersURL      string `json:"followers_url,omitempty"`
		FollowingURL      string `json:"following_url,omitempty"`
		GistsURL          string `json:"gists_url,omitempty"`
		StarredURL        string `json:"starred_url,omitempty"`
		SubscriptionsURL  string `json:"subscriptions_url,omitempty"`
		OrganizationsURL  string `json:"organizations_url,omitempty"`
		ReposURL          string `json:"repos_url,omitempty"`
		EventsURL         string `json:"events_url,omitempty"`
		ReceivedEventsURL string `json:"received_events_url,omitempty"`
		Type              string `json:"type,omitempty"`
		SiteAdmin         bool   `json:"site_admin,omitempty"`
	} `json:"sender,omitempty"`
}

// PullChanges for communication with git maven
func PullChanges(commitID string) {
	fmt.Println("Triggered")
	cmd := "/root/upload.sh"
	args := []string{commitID}
	if err := exec.Command(cmd, args...).Run(); err != nil {
		fmt.Println("Failed to build template with " + err.Error())
		os.Exit(1)
	}
	fmt.Println("Starting upload")

}

// GetTemplateID - updates management on kurjun instances
func GetTemplateID() string {
	response, err := http.Get("http://peer.noip.me:8081/rest/kurjun/templates/public/get?name=management&type=id")
	if err != nil {
		fmt.Println("Getting management template it exited with error: " + err.Error())
	}
	defer response.Body.Close()
	bodyResp, _ := ioutil.ReadAll(response.Body)
	return string(bodyResp)
}

func UploadTemplate() (string, err error) {
	// Prepare a form that you will submit to that URL.
	var b bytes.Buffer
	w := multipart.NewWriter(&b)
	// Add your image file
	f, err := os.Open("/mnt/lib/lxc/lxc-data/tmpdir/management-subutai-template_4.0.0_amd64.tar.gz")
	if err != nil {
		return nil, err
	}
	defer f.Close()
	fw, err := w.CreateFormFile("template", file)
	if err != nil {
		return nil, err
	}
	if _, err = io.Copy(fw, f); err != nil {
		return
	}
	// Add the other fields
	if fw, err = w.CreateFormField("key"); err != nil {
		return
	}
	if _, err = fw.Write([]byte("KEY")); err != nil {
		return
	}
	// Don't forget to close the multipart writer.
	// If you don't close it, your request will be missing the terminating boundary.
	w.Close()

	// Now that you have a form, you can submit it to your handler.
	req, err := http.NewRequest("POST", url, &b)
	if err != nil {
		return
	}
	// Don't forget to set the content type, this will contain the boundary.
	req.Header.Set("Content-Type", w.FormDataContentType())

	// Submit the request
	client := &http.Client{}
	res, err := client.Do(req)
	if err != nil {
		return
	}

	// Check the response
	if res.StatusCode != http.StatusOK {
		err = fmt.Errorf("bad status: %s", res.Status)
	}
	return
}

// UpdateTemplate is triggering build, test and deploy process for Subutai/base
func UpdateTemplate(c *gin.Context) {
	var updated = Message{Name: "Accepted", Body: "Upload of changes started", Time: 1294706395881547000}
	var pushEvent GitHubPushEvent
	c.BindJSON(&pushEvent)
	//req, _ := json.Marshal(GitHubPushEvent)
	//fmt.Printf("%+v\n", pushEvent)
	req, _ := json.Marshal(pushEvent)

	fmt.Println(string(req))
	fmt.Println("Ref is ", pushEvent.Ref)
	fmt.Println(c.PostForm("ref"))
	c.JSON(200, updated)
	notifyHipchat(string(pushEvent.After))
	PullChanges(string(pushEvent.After))
	//oldTemplateID := GetTemplateID()
}
