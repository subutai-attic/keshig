#!/bin/bash

SS_ROOT_DIR="/github/Subutai"

MANAGEMENT_ROOT_DIR="${SS_ROOT_DIR}/management"

function build_management {

	pushd $MANAGEMENT_ROOT_DIR > /dev/null

	mvn clean install -DskipTests

	popd > /dev/null


}

function upload_files {
	
	sshpass -p "ubuntu" ssh -P3021 root@dilshat.ddns.net "rm /root/update_mng.sh subutai-*.tar.gz"
	sshpass -p "ubuntu" scp -P3021 update_mng.sh root@dilshat.ddns.net:/root
	sshpass -p "ubuntu" scp -P3021 ${MANAGEMENT_ROOT_DIR}/server/server-karaf/target/subutai-*.tar.gz root@dilshat.ddns.net:/root
	sshpass -p "ubuntu" ssh -P3021 root@dilshat.ddns.net "chmod a+x /root/update_mng.sh"
	sshpass -p "ubuntu" ssh -P3021 root@dilshat.ddns.net "/root/update_mng.sh"
	sshpass -p "ubuntu" ssh -P3021 root@dilshat.ddns.net "rm /root/update_mng.sh subutai-*.tar.gz"

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
function export_template {

	sudo subutai export management

}

function upload_template {
	local token="$1"
	curl -k -F package=@/mnt/lib/lxc/lxc-data/tmpdir/management-subutai-template_4.0.0_amd64.tar -X POST https:10.10.10.1:8443/rest/kurjun/templates/upload/public?sptoken=$token

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

git_pull $1 $2

build_management

upload_files

export_template


