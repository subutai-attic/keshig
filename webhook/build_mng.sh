#!/bin/bash

SS_ROOT_DIR="/home/ubuntu/Subutai"

MANAGEMENT_ROOT_DIR="${SS_ROOT_DIR}/management"

function build_management {

	pushd $MANAGEMENT_ROOT_DIR > /dev/null

	mvn clean install -DskipTests

	popd > /dev/null


}

function git_pull {

	local branch="$1"

	pushd $MANAGEMENT_ROOT_DIR > /dev/null
		local current_branch=$(git branch | grep "^*" | awk '{print $2'})
		if [ ! "$current_branch" = "$branch" ]; then
			git checkout $branch
			git pull origin $branch
		fi
	popd > /dev/null
}

function init {

	if [ ! -d "$SS_ROOT_DIR" ]; then
		git clone https://github.com/subutai-io/Subutai.git
	fi

	install_deps

}

function install_deps {

	local java_v=$(javac -version 2>&1 | awk  '{print $2}' | awk '{print substr($0,0,3)}')
	printf "Checking javac version\n"
	if [ ! "$java_v" = "1.8" ]; then
		printf "Installed version:$java_v Required version: >=1.8\n"
		apt-get install openjdk-8-jdk -y
	fi
	local mvn=$(which mvn)
	local git_v=$(which git)
	if [ ! "$mvn" = ""]; then
		apt-get instasll maven -y
	fi
	if [ ! "$git_v" = ""]; then
		apt-get install git -y
	fi
}
