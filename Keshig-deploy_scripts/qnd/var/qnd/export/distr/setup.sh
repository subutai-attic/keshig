#!/bin/bash

type=$1
distr="/tmp/distr"

if [[ $type = "mgmt"  ]]
then 
    SUB_TYPE=subutai-mng
    bash -c "echo 180 > /sys/block/sda/device/timeout"
    bash -c "echo 180 > /sys/block/sdb/device/timeout"
    sudo snappy install --allow-unauthenticated $distr/*.snap
#    sleep 10
    systemctl stop subutai*
    #systemctl | grep subutai-mng_subutai-mng | awk -F" " '{print $1}' | awk -F"." '{print " sudo systemctl stop "$1}'  | bash
    #sudo find /var/lib/apps/subutai-mng  -name db -type d | awk '{print "sudo rm -rf "$1}' | bash
        sudo bash -c "echo 'Defaults secure_path="/apps/bin/:/apps/subutai/current/bin:/apps/subutai-mng/current/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"' > /etc/sudoers.d/subutai"
    sudo rm -rf /tmp/distr

    #    snappy install --allow-unauthenticated /home/ubuntu/tmpfs/subutai-mng_4.0.0_amd64.snap
    #    sleep 10
    echo management > /etc/hostname
    sed '/Delete me after export/d' -i /apps/subutai-mng/current/bin/start
    rm -rf /var/lib/apps/subutai-mng/current/*

elif [[ $type = "rh" ]]
then
    SUB_TYPE=subutai
    bash -c "echo 180 > /sys/block/sda/device/timeout"
    bash -c "echo 180 > /sys/block/sdb/device/timeout"

   sudo snappy install --allow-unauthenticated $distr/*.snap
   sudo bash -c "echo 'Defaults secure_path="/apps/subutai/current/bin:/apps/subutai-mng/current/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"' > /etc/sudoers.d/subutai"
   sudo btrfsinit /dev/sdc
   sudo rm -rf /tmp/distr
#  systemctl stop subutai*
   rm -rf /root/.gnupg
#    echo localhost > /etc/hostname
    systemctl stop subutai*
   sudo -H -u root bash -c 'echo localhost > /etc/hostname'
fi

systemctl stop snappy-autopilot.service
systemctl disable snappy-autopilot.service
systemctl stop snappy-autopilot.timer
systemctl disable snappy-autopilot.timer
mount -o remount,rw /
rm -rf /lib/systemd/system/snappy-autopilot.timer

#echo 'Defaults secure_path="/apps/subutai/current/bin:/apps/subutai-mng/current/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"' > /etc/sudoers.d/subutai

#> /apps/$SUB_TYPE/current/etc/subutai-agent/uuid.txt
#> /var/lib/dhcp/dhclient.eth0.leases
#> /var/lib/dhcp/dhclient.leases


#rm -f /apps/$SUB_TYPE/current/etc/subutai-agent/ca.crt /apps/$SUB_TYPE/current/etc/subutai-agent/client.key /apps/$SUB_TYPE/current/etc/subutai-agent/client.crt

sync
sync
