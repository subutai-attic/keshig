package main

import (
	"fmt"
	"net/http"

	"gopkg.in/gcfg.v1"
)

var client *http.Client

type keshigConfig struct {
	Repository    bool
	Branches      []string
	Allowinsecure bool
	URL           string
	Sslport       string
	Kurjun        string
}

const defaultConfig = `
	[keshig]
	Repository = github.com/subutai-io/keshig
  branches = master, dev
  url = cdn.subut.ai
  sslport = 8338
  allowinsecure = false
`

var (
	// Config describes configuration options for Keshig server
	Config keshigConfig
)

func init() {
	err := gcfg.ReadStringInto(&Config, defaultConfig)
	if err != nil {
		fmt.Println("Error during default config load" + err.Error())
	}

	err = gcfg.ReadFileInto(&Config, "keshig.gcfg")
	if err != nil {
		fmt.Println("Error during config load" + err.Error())
	}

}

// func InitAgentDebug() {
// 	if config.Agent.Debug {
// 		log.Level(log.DebugLevel)
// 	}
// }

// func CheckKurjun() (client *http.Client) {
// 	_, err := net.DialTimeout("tcp", Management.Host+":"+Cdn.Sslport, time.Duration(2)*time.Second)
// 	tr := &http.Transport{TLSClientConfig: &tls.Config{InsecureSkipVerify: true}}
// 	client = &http.Client{Transport: tr}
// 	if !log.Check(log.InfoLevel, "Trying local repo", err) {
// 		Cdn.Kurjun = "https://" + Management.Host + ":" + Cdn.Sslport + "/rest/kurjun"
// 	} else {
// 		Cdn.Kurjun = "https://" + Cdn.Url + ":" + Cdn.Sslport + "/kurjun/rest"
// 		if !Cdn.Allowinsecure {
// 			client = &http.Client{}
// 		}
// 	}
// 	return
// }
