#!/bin/bash

set -e


function export_template {

	sudo subutai export management

}

function upload_template {

	printf "Preparing to upload template to local Kurjun\n"
	local token=`/apps/subutai/current/bin/curl -s --data "username=admin&password=secret" --insecure https://10.10.10.1:8443/rest/v1/identity/gettoken`

	/apps/subutai/current/bin/curl -k -F 'package=@/mnt/lib/lxc/lxc-data/tmpdir/management-subutai-template_4.0.0_amd64.tar' -X POST https:10.10.10.1:8443/rest/kurjun/templates/upload/public?sptoken=$token
	printf "Done transfering "
}

export_template

upload_template

SHA="$1"

