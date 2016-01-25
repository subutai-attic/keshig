#!/bin/bash
mvn clean install
scp `pwd`/keshig-cli/target/keshig-cli-4.0.0-RC6.jar ubuntu@keshig-qa:~
scp `pwd`/keshig-impl/target/keshig-impl-4.0.0-RC6.jar ubuntu@keshig-qa:~
scp `pwd`/keshig-rest/target/keshig-rest-4.0.0-RC6.jar ubuntu@keshig-qa:~
scp `pwd`/keshig-api/target/keshig-api-4.0.0-RC6.jar ubuntu@keshig-qa:~
scp `pwd`/keshig-webui/target/keshig-webui-4.0.0-RC6-classes.jar ubuntu@keshig-qa:~
scp `pwd`/keshig-webui/target/keshig-webui-4.0.0-RC6.war ubuntu@keshig-qa:~

