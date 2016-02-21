package main

import (
        "os/exec"
        "strings"
        "fmt"
        )

//github webhook json payload
type GitHubPushEvent struct {
        Ref     string      `json:"ref,omitempty"`
        Before  string      `json:"before,omitempty"`
        After   string      `json:"after,omitempty"`
        Created bool        `json:"created,omitempty"`
        Deleted bool        `json:"deleted,omitempty"`
        Forced  bool        `json:"forced,omitempty"`
        BaseRef interface{} `json:"base_ref,omitempty"`
        Compare string      `json:"compare,omitempty"`
        /*Commits []struct {
                ID        string `json:"id,omitempty"`
                Distinct  bool   `json:"distinct,omitempty"`
                Message   string `json:"message,omitempty"`
                Timestamp string `json:"timestamp,omitempty"`
                URL       string `json:"url,omitempty"`
                Author    struct {
                        Name     string `json:"name,omitempty"`
                        Email    string `json:"email,omitempty"`
                        Username string `json:"username,omitempty"`
                } `json:"author,omitempty"`
                Committer struct {
                        Name     string `json:"name,omitempty"`
                        Email    string `json:"email,omitempty"`
                        Username string `json:"username,omitempty"`
                } `json:"committer,omitempty"`
                Added    []interface{} `json:"added,omitempty"`
                Removed  []interface{} `json:"removed,omitempty"`
                Modified []string      `json:"modified,omitempty"`
        } `json:"commits,omitempty"`
        HeadCommit struct {
                ID        string `json:"id,omitempty"`
                Distinct  bool   `json:"distinct,omitempty"`
                Message   string `json:"message,omitempty"`
                Timestamp string `json:"timestamp,omitempty"`
                URL       string `json:"url,omitempty"`
                Author    struct {
                        Name     string `json:"name,omitempty"`
                        Email    string `json:"email,omitempty"`
                        Username string `json:"username,omitempty"`
                } `json:"author,omitempty"`
                Committer struct {
                        Name     string `json:"name,omitempty"`
                        Email    string `json:"email,omitempty"`
                        Username string `json:"username,omitempty"`
                } `json:"committer,omitempty"`
                Added    []interface{} `json:"added,omitempty"`
                Removed  []interface{} `json:"removed,omitempty"`
                Modified []string      `json:"modified,omitempty"`
                WatchersCount    int         `json:"watchers_count,omitempty"`
		MirrorURL        interface{} `json:"mirror_url,omitempty"`
		OpenIssuesCount  int         `json:"open_issues_count,omitempty"`
		Forks            int         `json:"forks,omitempty"`
		OpenIssues       int         `json:"open_issues,omitempty"`
		Watchers         int         `json:"watchers,omitempty"`
		DefaultBranch    string      `json:"default_branch,omitempty"`
		Stargazers       int         `json:"stargazers,omitempty"`
		MasterBranch     string      `json:"master_branch,omitempty"`
	} `json:"repository,omitempty"`
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
	*/
}

//Assemble assembles
func (g *GitHubPushEvent) Assemble() (err error) {
	branch := strings.Split(g.Ref, "/")[2]
	fmt.Println("Working wit branch: " + branch)
	if strings.EqualFold("develop", branch) {
		cmd := exec.Command("/bin/bash", "/github/Keshig/webhook/build_mng.sh", "develop", g.After)
		out, err := cmd.CombinedOutput()
		if err != nil {
			fmt.Println(err.Error())
			return err
		}
		fmt.Println("Output: " + string(out))
	}
	return nil
	
}
