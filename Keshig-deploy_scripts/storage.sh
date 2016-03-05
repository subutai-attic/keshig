#!/bin/bash

set -e
# Defaults that are used and can be reset with opts

# size of the preallocated file in megabytes
DEFAULT_SIZE=4096
#default buffer size
DEFAULT_BUFFER_SIZE=1
#default dir
DEFAULT_XML_STORAGE_DIR=/home/ubuntu/kvm/storage
#default file-based storage dir
DEFAULT_STORAGE_DIR=/var/lib/libvirt/images
#libvirt domain
DOMAIN="NONE"
#[flags]

#sparse file
SPARSE=false
#live mount
LIVE=false
#persist mount
PERSISTENT=false
#qcow2
COW=false

TIME=$(date +%s)
FILENAME=subutai_media_${TIME}


#print usage of the script
function storage_usage {
	printf "\tAllocate file-based storage\n" 
	printf "\tUsage:\n"
	printf "\tstorage.sh -s SIZE -b BUFFER SIZE -m DOMAIN -t[optional]\n"
	printf "\t\t -s SIZE		-size of the file-based disk in megabytes\n"
	printf "\t\t -b BUFFER SIZE  	-buffer size\n"
	printf "\t\t -m DOMAIN		-libvirt domain to mount the newly created disk\n"
	printf "\t\t -n NAME		-device name [starts with \"hd\" or \"sd\"] \n"
	printf "\t[flags]\n"
	printf "\t\t -t			-sparse file\n"
	printf "\t\t -c			-qcow2 file\n"
	printf "\t\t -l			-live mount\n"
	printf "\t\t -p			-persistent\n"
	printf "\n"
	printf "\tBye\n"
	exit 0
}

#check if dirs exists, if not create dirs
function check_dir {

if [ ! -d "$DEFAULT_XML_STORAGE_DIR" ]; then
	printf "${DEFAULT_XML_STORAGE_DIR} does not exist. Creating dir...\n"
	mkdir -p $DEFAULT_XML_STORAGE_DIR		
fi

if [ ! -d "$DEFAULT_STORAGE_DIR" ]; then
	printf "${DEFAULT_STORAGE_DIR} does not exist. Creating dir...\n"
	mkdir -p $DEFAULT_STORAGE_DIR
fi

}

#allocate file-based storage
function alloc {

printf "Allocating file-based disk storage with size:${DEFAULT_SIZE} \n"
#create dir if dne
check_dir

dd if=/dev/zero of=${DEFAULT_STORAGE_DIR}/${FILENAME}.img bs=${DEFAULT_BUFFER_SIZE}G count=${DEFAULT_SIZE}
printf "\n%s\n" "IMG FILE: ${DEFAULT_STORAGE_DIR}/${FILENAME}.img"
write_xml "raw" 

}

function alloc_cow {

qemu-img create -f qcow2 $DEFAULT_STORAGE_DIR/${FILENAME} ${DEFAULT_SIZE}M
printf "\n%s\n" "IMG FILE: ${DEFAULT_STORAGE_DIR}/${FILENAME}.img"
write_xml "raw"
}

function write_xml {
local in_type="$1"

touch ${DEFAULT_XML_STORAGE_DIR}/${FILENAME}.xml

printf "%s\n" "<disk type='file' device='disk'>" >> ${DEFAULT_XML_STORAGE_DIR}/${FILENAME}.xml
printf "%s\n" "<driver name='qemu' type='${in_type}' cache='none'/>" >> ${DEFAULT_XML_STORAGE_DIR}/${FILENAME}.xml
printf "%s\n" "<source file='${DEFAULT_STORAGE_DIR}/${FILENAME}.img'/>" >> ${DEFAULT_XML_STORAGE_DIR}/${FILENAME}.xml
printf "%s\n" "<target dev='${NAME}'/>" >> ${DEFAULT_XML_STORAGE_DIR}/${FILENAME}.xml
printf "%s\n" "</disk>" >> ${DEFAULT_XML_STORAGE_DIR}/${FILENAME}.xml

printf "\n%s\n" "XML FILE: ${DEFAULT_XML_STORAGE_DIR}/${FILENAME}.xml"
} 

#create sparse file
#NOTE: for TEST usage only
function sparse_file {

check_dir

printf "\n"
printf "Allocating SPARSE file-based disk with size:${DEFAULT_SIZE}. NOTE: NOT PRODUCTION USE\n"

dd if=/dev/zero of=/var/lib/libvirt/images/${FILENAME}.img bs=${DEFAULT_BUFFER_SIZE}M seek=${DEFAULT_SIZE} count=0 
write_xml "raw"
}

#print defaults
function print_vars {
printf "\n"
printf "Folling variables and flags are set:\n"
printf "\tALLOCATING DISK SIZE:	${DEFAULT_SIZE}\n"
printf "\tBUFFER SIZE:		${DEFAULT_BUFFER_SIZE}\n"
printf "\tXML STORAGE DIR:	${DEFAULT_XML_STORAGE_DIR}\n"
printf "\tFILE STORAGE DIR:	${DEFAULT_STORAGE_DIR}\n"
printf "\tSPARSE:			${SPARSE}\n"
printf "\tLIVE MOUT:		${LIVE}\n"
printf "\tPERSISTENT:		${PERSISTENT}\n"
printf "\tDOMAIN TO MOUNT:	${DOMAIN}\n"
printf "\tDISK NAME:		${NAME}\n"
}

 while getopts "n:h:s:b:tm:lpc" OPTION
 do
	 case $OPTION in
		 h)
			 storage_usage
			 exit 0
			 ;;
		 s)
			 DEFAULT_SIZE=$OPTARG
			 ;;
		 b)
			 DEFAULT_BUFFER_SIZE=$OPTARG
			 ;;
		 t)
			SPARSE=true
			COW=false
			;;
		c)
			COW=true
			SPARSE=false
			;;
		 m)
			DOMAIN=$OPTARG
			;;
		 p)
			PERSISTENT=true
			;;
		 l)
			LIVE=true
			 ;;
		 n)
			 NAME=$OPTARG
			 ;;
		 ?)
			storage_usage
			exit
			;;
	esac
done

print_vars
check_dir
if [ "$SPARSE" = true ] ; then
	printf 'Creating a sparse file. NOTE: NOT RECOMMENDED FOR PRODUCTION USE'
	printf "\n"
	sparse_file
elif [ "$COW" = true ]; then
	printf "Creating qcow2 storage"
	printf "\n"
	alloc_cow
else
	printf 'Creating a file-based storage'
	printf "\n" 
	alloc
fi

printf "Domain name passed: $DOMAIN\n"

if [ ! "$DOMAIN" = "NONE" ] ; then
	printf "Mounting disk to $DOMAIN\n"
	virsh attach-device $DOMAIN $DEFAULT_XML_STORAGE_DIR/${FILENAME}.xml --live --persistent

fi

		
