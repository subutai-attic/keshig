package main

import "github.com/gin-gonic/gin"

func main() {
	r := gin.Default()
	v1 := r.Group("/github/subutai")
	{
		v1.POST("/develop", UpdateTemplate)
	}
	r.Run(":8081")
}
