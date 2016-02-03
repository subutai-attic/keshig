#!/bin/bash

my_dir="$(dirname "$0")"

mgmt_folder=$1
SNAP_DISTR="../SNAPS/$mgmt_folder"
rdpPort=3499
BOX_VERSION=$(echo $mgmt_folder | awk -F"_" '{print $2}' | awk -F"." '{print $1"."$2}' )
timeshift=$(echo $mgmt_folder | awk -F"_" '{print $3}' )
#echo "$VAGRANT_BOX_VERSION.$timeshift"

VAGRANT_BOX_VERSION="$BOX_VERSION.$timeshift"

pushd $my_dir
source "../qnd_vars.conf"
vagrant destroy -f
vagrant up
vagrant ssh-config > ssh.config

function send_files(){
set -x
line=$1
#my_dir=$2
SNAP_DISTR=$2
if [[ "$(echo $line | grep MGMT)" ]]; then
VM="$line"
#vagrant ssh $VM  -c "mkdir distr; sudo mount -t tmpfs -o size=1G distr /home/ubuntu/distr"
scp -F ssh.config -r distr  ubuntu@$VM:/tmp
scp -c aes128-ctr -C -F ssh.config -r $SNAP_DISTR/subutai-*  ubuntu@$VM:/tmp/distr &
elif  [[ "$(echo $line | grep RH)" ]]; then
VM="$line"
#vagrant ssh $VM  -c "mkdir distr; sudo mount -t tmpfs -o size=1G distr /home/ubuntu/distr"
scp -F ssh.config -r distr  ubuntu@$VM:/tmp
scp -c aes128-ctr -C  -F ssh.config -r $SNAP_DISTR/subutai_*  ubuntu@$VM:/tmp/distr &
fi
set +x
}

function install_files(){
line=$1
if [[ "$(echo $line | grep MGMT)" ]]; then
VM="$line"
vagrant ssh $VM -c "sudo /tmp/distr/setup.sh mgmt" &
elif  [[ "$(echo $line | grep RH)" ]]; then
VM="$line"
vagrant ssh $VM -c "sudo /tmp/distr/setup.sh rh" &
fi
}

function release_hashicorp() {

V_BOX=$1
VAGRANT_BOX_VERSION=$2
ATLAS_TOKEN=$3
USER_NAME=$4
BOX_NAME=$5


if [ ! -f $V_BOX ];then
  echo "File $V_BOX does not exists"
  exit 0
fi

curl https://atlas.hashicorp.com/api/v1/boxes \
        -X POST \
        -d box[name]="$BOX_NAME" \
        -d box[is_private]=false \
        -d access_token="$ATLAS_TOKEN"

curl https://atlas.hashicorp.com/api/v1/box/$USER_NAME/$BOX_NAME/versions \
        -X POST \
        -d version[version]="$VAGRANT_BOX_VERSION" \
        -d access_token="$ATLAS_TOKEN"

curl https://atlas.hashicorp.com/api/v1/box/$USER_NAME/$BOX_NAME/version/$VAGRANT_BOX_VERSION/providers \
-X POST \
-d provider[name]='virtualbox' \
-d access_token="$ATLAS_TOKEN"

V_PATH=$(curl https://atlas.hashicorp.com/api/v1/box/$USER_NAME/$BOX_NAME/version/$VAGRANT_BOX_VERSION/provider/virtualbox/upload?access_token="$ATLAS_TOKEN" | awk -F"," '{print $1}' | awk -F'":"' '{print $2}' | tr -d '"')
echo "V_PATH=$V_PATH"
echo "curl -X PUT --upload-file $V_BOX $V_PATH"
      curl -X PUT --upload-file $V_BOX $V_PATH

curl https://atlas.hashicorp.com/api/v1/box/$USER_NAME/$BOX_NAME/version/$VAGRANT_BOX_VERSION/release \
        -X PUT \
        -d access_token="$ATLAS_TOKEN"
}

while IFS= read -r line; do
echo "send to $line"
send_files $line $SNAP_DISTR
done < <(cat Vagrantfile | grep v.name | awk -F"=" '{print $2}'  | tr -d '"')
wait

while IFS= read -r line; do
echo "installing $line"
install_files $line
done < <(cat Vagrantfile | grep v.name | awk -F"=" '{print $2}'  | tr -d '"')
wait

vagrant halt
while IFS= read -r line; do
rm -rf $line.box
vagrant package $line --output $line.box 
done < <(cat Vagrantfile | grep v.name | awk -F"=" '{print $2}'  | tr -d '"')

while IFS= read -r line; do
if [[ "$(echo $line | grep MGMT)" ]]; then
BOX_NAME="management-host"
else
BOX_NAME="resource-host"
fi
release_hashicorp $line.box $VAGRANT_BOX_VERSION $ATLAS_TOKEN $USER_NAME $BOX_NAME
done < <(cat Vagrantfile | grep v.name | awk -F"=" '{print $2}'  | tr -d '"')
wait

popd
