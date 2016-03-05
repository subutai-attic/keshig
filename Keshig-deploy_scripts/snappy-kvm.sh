#!/bin/bash

set -e
#SNAPPY IMAGE
SNAPPY_IMG="ubuntu-15.04-snappy-amd64-generic.img"
#COMPRESSED SNAPPY IMG
SNAPPY_IMG_XZ="ubuntu-15.04-snappy-amd64-generic.img.xz"
#SNAPPY DISTRIBUTION URL
SNAPPY_URL="http://releases.ubuntu.com/15.04/$SNAPPY_IMG_XZ"
#SNAPPY IMG DIR
SNAPPY_DIR=/home/ubuntu/kvm/snappy/
#Subutai KVM dir
SUBUTAI_KVM_DIR=/home/ubuntu/kvm
#Snapps required
LIST="btrfs collectd curl lxc ovs rh rngd subutai cgmanager p2p dnsmasq"
#EPOCH date that will be appeneded to VM names
DATE="`date +%s`"
#Export dirs for snapps
EXPORT_DIR=$SNAPPY_DIR/snapps

function download_deps {

apt-get install qemu-kvm libvirt-bin virtinst -y

}

#download snappy image
function wget_snappy_kvm {
	printf "Downloading Ubuntu Snappy Image from: ${SNAPPY_URL}\n"
	create_dir_dne $SNAPPY_DIR
	if [ $? -eq 0 ]; then
		if [ ! -f "$SNAPPY_DIR/$SNAPPY_IMG_XZ" ] && [ ! -f "$SNAPPYDIR/$SNAPPY_IMG" ]; then
			printf "Does not exist: $SNAPPY_DIR$SNAPPY_IMG_XZ\n"
			wget $SNAPPY_URL -P $SNAPPY_DIR
		fi
		#if uncompressed exists do nothing here
		if [ ! "$SNAPP_DIR/$SNAPPY_IMG" ]; then
			if [ -f $SNAPPY_DIR/$SNAPPY_IMG_XZ ]; then
				printf "Uncomperssing $SNAPPY_DIR$SNAPPY_IMG_XZ\n"
				pushd $SNAPPY_DIR > /dev/null
				unxz ./$SNAPPY_IMG_XZ
				popd /dev/null
			else
				printf "Did not find $SNAPPY_DIR/$SNAPPY_IMG_XZ\n"
			fi
		fi
	else
		printf "mkdir -p ${SNAPPY_DIR} failed with exit code: $?"
	fi
}

function git_clone_subutai_snappy {

	local branch="$1"
	pushd $SNAPPY_DIR > /dev/null
	if [ ! -d "./Subutai-snappy" ]; then
		git clone https://github.com/subutai-io/Subutai-snappy.git
	fi
	pushd "./Subutai-snappy" > /dev/null
	local target_branch=`git branch | grep -oh $branch`	
	if [ ! "$target_branch" = "$branch" ]; then
		git branch $branch
		git checkout $branch
		git pull origin "$branch"
	fi
	popd > /dev/null

	popd > /dev/null
}
#import guest domain to libvirt
function import_clean_snappy {
	local snappy_clean=`virsh list --all | grep "snappy-clean " | awk '{print $2'}`
	printf "Found VM running with name:${snappy_clean}\n"
	if [ ! "$snappy_clean" = "snappy-clean" ]; then
		printf "Installing Ubuntu Snappy clean image from: $SNAPPY_DIR$SNAPPY_IMG\n"
		sudo virt-install -n snappy-clean -r 2048 --disk=$SNAPPY_DIR$SNAPPY_IMG,bus=virtio,size=1 --network=default,model=virtio  --import --noautoconsole -v
	fi
	printf "Seems Snappy Image is already installed\n"
	virsh list --all | grep snappy-clean

}
#build snapps
function snap_build {

	pushd "$SNAPPY_DIR/Subutai-snappy" > /dev/null
	rm -rf /tmp/tmpdir_subutai
	mkdir -p /tmp/tmpdir_subutai
	for i in $LIST; do
		cp -r $i/* /tmp/tmpdir_subutai
	done
	create_dir_dne $EXPORT_DIR
	snappy build /tmp/tmpdir_subutai --output=$EXPORT_DIR
	popd > /dev/null
}
#clone vm
function clone_vm {
	
	create_dir_dne "$SNAPPY_DIR/storage/"
	local state=`virsh list | grep snappy-clean | grep -oh "running"`

	if [ "$state" = "running" ]; then
		virsh shutdown snappy-clean
		sleep 10
	fi

	virt-clone -o "snappy-clean" -n "subutai-${DATE}" --file "$SNAPPY_DIR/storage/subutai-${DATE}"
}

function virt_addr {

	local vm="$1"
	local ipaddr=$(arp -an | grep "`virsh dumpxml $vm | grep "mac address" | sed "s/.*'\(.*\)'.*/\1/g"`" | awk '{ gsub(/[\(\)]/,"",$2); print $2 }')
	echo $ipaddr
}

function mount_storage {

	pushd $SUBUTAI_KVM_DIR > /dev/null
	printf "Creating new disk storage \n" 
	./storage.sh -s 2 -m subutai-${DATE} -n sda
	popd > /dev/null
}

#install snapps
#add port forward from host machine 2277 port to 22 on target VM
#scp files and install them
function install_snapps {
	pushd "$SNAPPY_DIR/Subutai-snappy"
	local vm="$1"
	local state=$(virsh list --all | grep $1 | awk '{print $3}')
	printf "VM: $1 \t state:$state\n"

	if [ ! $state = "running" ]; then
		printf "VM is not running. Starting VM :$vm\n"
		virsh start $vm
		sleep 10
	fi
	mount_storage

	local ip_addr=$(virt_addr $vm)
	printf "VM:$vm IP addr: $ip_addr\n"

	if [ -f "$HOME/.ssh/id_rsa.pub" ]; then
		echo "Adding user public key"
		pubkey="$(cat $HOME/.ssh/id_rsa.pub)"
		sshpass -p "ubuntu" ssh -o StrictHostKeyChecking=no ubuntu@$ip_addr "sudo bash -c 'echo $pubkey >> /root/.ssh/authorized_keys'"
	fi
	printf "Creating tmpfs\n"
	sshpass -p "ubuntu" ssh -o StrictHostKeyChecking=no ubuntu@$ip_addr "mkdir tmpfs; sudo mount -t tmpfs -o size=1G tmpfs /home/ubuntu/tmpfs"
	printf  "Copying snap\n"
	sshpass -p "ubuntu" scp prepare-server.sh $EXPORT_DIR/subutai_4.0.0_amd64.snap ubuntu@$ip_addr:/home/ubuntu/tmpfs/
	AUTOBUILD_IP=$(ifconfig `route -n | grep ^0.0.0.0 | awk '{print $8}'` | grep 'inet addr' | awk -F: '{print $2}' | awk '{print $1}') 
	sshpass -p "ubuntu" ssh -o StrictHostKeyChecking=no ubuntu@$ip_addr "sed -i \"s/IPPLACEHOLDER/$AUTOBUILD_IP/g\" /home/ubuntu/tmpfs/prepare-server.sh"
	printf "Running install script\n"

	sshpass -p "ubuntu" ssh -o StrictHostKeyChecking=no ubuntu@$ip_addr "sudo /home/ubuntu/tmpfs/prepare-server.sh"
	sshpass -p "ubuntu" ssh -o StrictHostKeyChecking=no ubuntu@$ip_addr "sudo /apps/subutai/current/bin/btrfsinit /dev/sda"
	popd
}

#create dir if does not exist
function create_dir_dne {

local dir="$1"

if [ ! -d "$dir" ]; then
        printf "$dir does not exist. Creating dir...\n"
        mkdir -p $dir
fi

}

download_deps

create_dir_dne "$SNAPPY_DIR"

git_clone_subutai_snappy "devel"

wget_snappy_kvm

import_clean_snappy

clone_vm "subutai-${DATE}"

snap_build

install_snapps "subutai-${DATE}"
