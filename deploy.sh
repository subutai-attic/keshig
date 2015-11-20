#!/bin/bash
scp `pwd`/keshigqd-cli/target/keshigqd-cli-4.0.0-RC3.jar ubuntu@172.16.131.81:~
scp `pwd`/keshigqd-impl/target/keshigqd-impl-4.0.0-RC3.jar ubuntu@172.16.131.81:~
scp `pwd`/keshigqd-rest/target/keshigqd-rest-4.0.0-RC3.jar ubuntu@172.16.131.81:~
scp `pwd`/keshigqd-api/target/keshigqd-api-4.0.0-RC3.jar ubuntu@172.16.131.81:~

