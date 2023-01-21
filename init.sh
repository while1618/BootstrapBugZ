#!/bin/sh

clear
echo "Name of your app: "
APP_NAME=$(gum input --placeholder "")
clear
echo "Pick your backend: "
BACKEND=$(gum choose "spring-boot")
clear
echo "Pick your frontend: "
FRONEND=$(gum choose "svelte-kit")
clear

echo "Name of your app is: $APP_NAME"
echo "You picked $BACKEND for backend, and $FRONEND for frontend."