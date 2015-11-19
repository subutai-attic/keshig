#!/bin/bash

my_dir="$(dirname "$0")"
source "$my_dir/qnd_vars.conf"
mgmt_folder=$1
if [ "$mgmt_folder" ]; then 

SNAP_DISTR="SNAPS/$mgmt_folder"
rdpPort=3490

vagrant destroy -f
cat $my_dir/Vagrantfile | grep v.name | awk -F"=" '{print $2}' | tr -d '"' | awk  '{print "vboxmanage controlvm "$1" poweroff && vboxmanage unregistervm "$1" --delete "}' | bash > /dev/null 2>&1
cat $my_dir/Vagrantfile | grep v.name | awk -F"=" '{print $2}' | tr -d '"' | awk  '{print "vboxmanage unregistervm "$1" --delete "}' | bash > /dev/null 2>&1
vagrant up
vagrant ssh-config > ssh.config



#count=0
#my_dir="$(dirname "$0")"

function send_files(){
line=$1
my_dir=$2
SNAP_DISTR=$3
if [[ "$(echo $line | grep MGMT)" ]]; then
VM="$line"
scp -F ssh.config -r $my_dir/distr  ubuntu@$VM:~/
scp -C -F ssh.config -r $SNAP_DISTR/subutai-*  ubuntu@$VM:~/distr &
elif  [[ "$(echo $line | grep RH)" ]]; then
VM="$line"
scp -F ssh.config -r $my_dir/distr  ubuntu@$VM:~/
scp -C  -F ssh.config -r $SNAP_DISTR/subutai_*  ubuntu@$VM:~/distr &
fi
}

function install_files(){
line=$1
if [[ "$(echo $line | grep MGMT)" ]]; then
VM="$line"
vagrant ssh $VM -c "sudo ~/distr/setup.sh mgmt" &
elif  [[ "$(echo $line | grep RH)" ]]; then
VM="$line"
vagrant ssh $VM -c "sudo ~/distr/setup.sh rh" &
fi
}

function conf_run(){
line=$1
rdpPort=$2
if [[ "$(echo $line | grep MGMT)" ]]; then
VM="$line"
VBoxManage modifyvm $VM --nic2 intnet --intnet2 intnetSnappy${line: -1}  --nicpromisc2 allow-all --macaddress2 auto
VBoxManage modifyvm $VM --vrde on --vrdeport $rdpPort
VBoxManage modifyvm $VM --nic1 bridged --bridgeadapter1 $BRIDGE_INT --nicpromisc1 allow-all  --macaddress1 auto
VBoxManage startvm $VM  --type headless

elif  [[ "$(echo $line | grep RH)" ]]; then
VM="$line"
VBoxManage modifyvm $VM --nic1 intnet --intnet1 intnetSnappy${line: -1}  --nicpromisc1 allow-all --macaddress1 auto
VBoxManage modifyvm $VM --vrde on --vrdeport $rdpPort
VBoxManage startvm $VM --type  headless
fi
}

while IFS= read -r line; do
echo "send to $line"
send_files $line $my_dir $SNAP_DISTR
done < <(cat $my_dir/Vagrantfile | grep v.name | awk -F"=" '{print $2}'  | tr -d '"')
wait

while IFS= read -r line; do
echo "installing $line"
install_files $line
done < <(cat $my_dir/Vagrantfile | grep v.name | awk -F"=" '{print $2}'  | tr -d '"')
wait

vagrant halt


while IFS= read -r line; do
echo "Configure and Run $line  rdp:$rdpPort"
conf_run $line $rdpPort
rdpPort=$((rdpPort+1))
done < <(cat $my_dir/Vagrantfile | grep v.name | awk -F"=" '{print $2}'  | tr -d '"')

else 
echo "Provide mgmt_folder option"
fi


