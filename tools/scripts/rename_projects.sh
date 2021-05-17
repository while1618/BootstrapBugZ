#!/bin/bash

echo "Enter name for Angular project: "
read ANGULAR_UI
echo "Enter name for Spring Boot project: "
read SPRING_BOOT_API

nx g @nrwl/workspace:mv --project angular-ui --destination $ANGULAR_UI
nx g @nrwl/workspace:mv --project angular-ui-e2e --destination "$ANGULAR_UI-e2e"
nx g @nrwl/workspace:mv --project spring-boot-api --destination $SPRING_BOOT_API
