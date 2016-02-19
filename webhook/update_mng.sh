#!/bin/bash
#####################################
service management stop
​
cd /root
mkdir temp
tar xf subutai-4.0.0-RC6.tar.gz -C /root/temp/
​
#####################################
rm -rf /var/lib/apps/subutai-mng/current/db
rm -rf /var/lib/apps/subutai-mng/current/data
rm -rf /var/lib/apps/subutai-mng/current/keystores
​
echo Removed Subutai DB/Repository ...
#####################################
​
rm -rf /apps/subutai-mng/current/system
rm -rf /apps/subutai-mng/current/lib
rm -rf /apps/subutai-mng/current/deploy
​
echo Removed old files of karaf ...
​
cp -a /root/temp/subutai*/system /apps/subutai-mng/current/system
cp -a /root/temp/subutai*/lib /apps/subutai-mng/current/lib
cp -a /root/temp/subutai*/deploy /apps/subutai-mng/current/deploy
cp /root/temp/subutai*/etc/subutai-mng/git.properties /apps/subutai-mng/current/etc/subutai-mng/
cp /root/temp/subutai*/etc/subutai-mng/quota.cfg /apps/subutai-mng/current/etc/subutai-mng/
​
rm -r /root/temp/
​
######################################
# Set DEBUG MODE #####################
sed 's/# export KARAF_DEBUG/export KARAF_DEBUG=true/g' /apps/subutai-mng/current/bin/setenv > setenv && mv setenv /apps/subutai-mng/current/bin
######################################
service management start
​
echo Management Started successfully !!!
​
#######################################   
