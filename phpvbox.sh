#!/bin/bash

#before install we need set BIOS to Vt-x enable to be able to run 64bit system

sudo add-apt-repository ppa:snappy-dev/tools -y
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install snappy-tools bzr -y
#--Virtual Box installation steps
sudo apt-get update && sudo apt-get upgrade -y
sudo apt-get install build-essential dkms unzip -y
echo "deb http://download.virtualbox.org/virtualbox/debian vivid contrib" | sudo tee -a  /etc/apt/sources.list
wget -q http://download.virtualbox.org/virtualbox/debian/oracle_vbox.asc -O- | sudo apt-key add -
sudo apt-get update
sudo apt-get install VirtualBox-4.3 -y
sudo usermod -aG vboxusers ubuntu
#sudo /etc/init.d/vboxdrv setup
wget http://download.virtualbox.org/virtualbox/4.3.28/Oracle_VM_VirtualBox_Extension_Pack-4.3.28-100309.vbox-extpack
sudo VBoxManage extpack install Oracle_VM_VirtualBox_Extension_Pack-4.3.28-100309.vbox-extpack
sudo apt-get install vagrant -y

#--phpvirtualbox installation steps
sudo apt-get install apache2 php5 php5-mysql libapache2-mod-php5 php-soap -y
sudo /etc/init.d/apache2 restart
wget http://sourceforge.net/projects/phpvirtualbox/files/phpvirtualbox-4.3-3.zip
unzip phpvirtualbox-4.3-3.zip
sudo mv phpvirtualbox-4.3-3 /var/www/html/phpvirtualbox
sudo cp /var/www/html/phpvirtualbox/config.php-example /var/www/html/phpvirtualbox/config.php

rm -r '/home/ubuntu/VirtualBox VMs'
ln -s /var/qnd/VB_HOME '/home/ubuntu/VirtualBox VMs'

sudo sed -i.bak 's/^\(var $username =\).*/\1 \"ubuntu\"; /' /var/www/html/phpvirtualbox/config.php
sudo sed -i.bak 's/^\(var $password =\).*/\1 \"ubuntu\"; /'  /var/www/html/phpvirtualbox/config.php
echo "VBOXWEB_USER=ubuntu" | sudo tee -a  /etc/default/virtualbox

sudo /etc/init.d/vboxweb-service start

my_dir="$(dirname "$0")"
pushd $my_dir
wget https://s3.eu-central-1.amazonaws.com/vagrant-export-playbook/snappy.box
popd
vagrant box add sn $my_dir/snappy.box
sudo dpkg -i $my_dir/qnd.deb
sudo dpkg -i $my_dir/subutaideploy-cli_2.1.1-192_all.deb
sudo apt-get -f install -y
sudo dpkg -i $my_dir/subutaideploy-cli_2.1.1-192_all.deb
