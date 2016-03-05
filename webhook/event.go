package main

import (
	"fmt"
	"os/exec"
	"strings"
)

//github webhook json payload
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

//Assemble assembles
func (g *GitHubPushEvent) Assemble() (err error) {

	branch := strings.Split(g.Ref, "/")[2]

	fmt.Println("Working with branch: " + branch)

	if strings.EqualFold("develop", branch) {
		cmd := exec.Command("/bin/bash", "/github/Keshig/webhook/build_mng.sh", "develop", g.After)
		out, err := cmd.CombinedOutput()
		if err != nil {
			fmt.Println(err.Error())
			return err
		}
		fmt.Println("Output: " + string(out))
	} else {
		fmt.Printf("Target is not a desired branch. Skipping ...")
		return nil
	}
	return nil

}
