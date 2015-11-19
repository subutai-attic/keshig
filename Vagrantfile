# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  config.ssh.username="ubuntu"
  config.ssh.password="ubuntu"
  config.vm.synced_folder ".", "/vagrant", id: "vagrant-root", disabled: true

  config.vm.define :SubutaiMGMT1 do |conf|
    conf.vm.box="sn"
    conf.vm.provider "virtualbox" do |v|
      v.name="SubutaiMGMT1"
    end
  end

  config.vm.define :SubutaiMGMT2 do |conf|
    conf.vm.box="sn"
    conf.vm.provider "virtualbox" do |v|
      v.name="SubutaiMGMT2"
    end
  end

  config.vm.define :SubutaiMGMT3 do |conf|
    conf.vm.box="sn"
    conf.vm.provider "virtualbox" do |v|
      v.name="SubutaiMGMT3"
    end
  end

  config.vm.define :SubutaiRH11 do |conf|
    conf.vm.box="sn"
    conf.vm.provider "virtualbox" do |v|
      v.name="SubutaiRH11"
    end
  end

  config.vm.define :SubutaiRH12 do |conf|
    conf.vm.box="sn"
    conf.vm.provider "virtualbox" do |v|
      v.name="SubutaiRH12"
    end
  end

  config.vm.define :SubutaiRH13 do |conf|
    conf.vm.box="sn"
    conf.vm.provider "virtualbox" do |v|
      v.name="SubutaiRH13"
    end
  end

  config.vm.define :SubutaiRH23 do |conf|
    conf.vm.box="sn"
    conf.vm.provider "virtualbox" do |v|
      v.name="SubutaiRH23"
    end
  end


end
