#!/bin/bash

SS_ROOT_DIR="/github/Subutai"

MANAGEMENT_ROOT_DIR="${SS_ROOT_DIR}/management"

function build_management {

	pushd $MANAGEMENT_ROOT_DIR > /dev/null

	mvn clean install -DskipTests

	popd > /dev/null
}


function upload_to_rh {

	sshpass -p "ubuntu" scp -P3071 /github/Keshig/webhook/create_mng.sh ubuntu@dilshat.ddns.net:/home/ubuntu
	sshpass -p "ubuntu" scp -P3021 ${MANAGEMENT_ROOT_DIR}/server/server-karaf/target/subutai-*.tar.gz ubuntu@dilshat.ddns.net:/home/ubuntu
	sshpass -p "ubuntu" ssh -p 3071 ubuntu@dilshat.ddns.net "chmod a+x /home/ubuntu/create_mng.sh"
	sshpass -p "ubuntu" ssh -p 3071 ubuntu@dilshat.ddns.net "/home/ubuntu/create_mng.sh $SHA"

}

function git_pull {

	local branch="$1"
	local sha="$2"

	pushd $MANAGEMENT_ROOT_DIR > /dev/null
	#	local current_branch=$(git branch | grep "^*" | awk '{print $2'})
	#	if [ ! "$current_branch" = "$branch" ]; then
			printf "Fetching from GitHub\n"
			git fetch origin
			printf "Checking out $sha \n"
			git checkout $sha
	#	fi
	popd > /dev/null
}
function init {

	if [ ! -d "$SS_ROOT_DIR" ]; then
		pushd "/github" > /dev/null
		git clone https://github.com/subutai-io/Subutai.git
		popd > /dev/null
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

#git commit SHA
SHA="$2"
#fetch checkout
git_pull $1 $2
build_management
upload_to_rh
