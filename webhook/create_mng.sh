#!/bin/bash


function clone_export {

subutai clone master management

pushd /home/ubuntu > /dev/null
cp subutai-4.0.0-RC6.tar.gz management.tar.gz /mnt/lib/lxc/lxc-data/management-home/subutai/
lxc-attach -n management -- mkdir -p /apps/subutai-mng /var/lib/apps/subutai-mng/current
lxc-attach -n management -- tar -C /apps/subutai-mng/ -xzf /home/subutai/subutai-4.0.0-RC6.tar.gz
lxc-attach -n management -- mv /apps/subutai-mng/subutai-4.0.0-RC6 /apps/subutai-mng/current
lxc-attach -n management -- tar -xzf /home/subutai/management.tar.gz
lxc-attach -n management -- rm -f /var/lib/dhcp/*
lxc-attach -n management -- apt update
lxc-attach -n management -- apt install --force-yes -y curl

rm -f /mnt/lib/lxc/lxc-data/management-home/subutai/subutai-4.0.0-RC6.tar.gz
rm -f /mnt/lib/lxc/lxc-data/management-home/subutai/management.tar.gz

popd > /dev/null

subutai export management

}

function set_version {
first="subutai.template.version = 4.0.0"
second="4.0.0-$SHA"

}
function upload_template {

        printf "Preparing to upload template to local Kurjun\n"
        local token=`/apps/subutai/current/bin/curl -s --data "username=admin&password=secret" --insecure https://10.10.10.1:8443/rest/v1/identity/gettoken`
        /apps/subutai/current/bin/curl -k -F 'package=@/mnt/lib/lxc/lxc-data/tmpdir/management-subutai-template_4.0.0_amd64.tar.gz' -X POST https:10.10.10.1:8443/rest/kurjun/templates/upload/public?sptoken=$token
        printf "Done transfering "

}

SHA=$1

clone_export

upload_template
