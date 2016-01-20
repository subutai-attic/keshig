#!/bin/bash
scp `pwd`/keshig-cli/target/keshig-cli-4.0.0-RC6.jar ubuntu@172.16.131.81:~
scp `pwd`/keshig-impl/target/keshig-impl-4.0.0-RC6.jar ubuntu@172.16.131.81:~
scp `pwd`/keshig-rest/target/keshig-rest-4.0.0-RC6.jar ubuntu@172.16.131.81:~
scp `pwd`/keshig-api/target/keshig-api-4.0.0-RC6.jar ubuntu@172.16.131.81:~
scp `pwd`/keshig-webui/target/keshig-webui-4.0.0-RC6-classes.jar ubuntu@172.16.131.81:~
scp `pwd`/keshig-webui/target/keshig-webui-4.0.0-RC6.war ubuntu@172.16.131.81:~

