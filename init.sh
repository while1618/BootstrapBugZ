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

find ./backend -mindepth 1 ! -regex "^./backend/$BACKEND\(/.*\)?" -delete
find ./frontend -mindepth 1 ! -regex "^./frontend/$FRONEND\(/.*\)?" -delete

cp -r ./backend/$BACKEND/. ./backend
cp -r ./frontend/$FRONEND/. ./frontend

rm -rf ./backend/$BACKEND
rm -rf ./frontend/$FRONEND

echo "Name of your app is: $APP_NAME"
echo "You picked $BACKEND for backend, and $FRONEND for frontend."