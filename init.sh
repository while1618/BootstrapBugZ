#!/bin/sh

# self delete this script after execution
rm -- "$0"

clear
echo "What is the name of your app: "
APP_NAME=$(gum input --placeholder "" | tr '[:upper:]' '[:lower:]')

clear
echo "Choose your backend: "
BACKEND=$(gum choose "spring-boot")

clear
echo "Choose your frontend: "
FRONTEND=$(gum choose "svelte-kit")

# delete all other frameworks except the chosen ones
find ./backend -mindepth 1 ! -regex "^./backend/$BACKEND\(/.*\)?" -delete
find ./frontend -mindepth 1 ! -regex "^./frontend/$FRONTEND\(/.*\)?" -delete

# delete all workflows except the chosen frameworks
find .github/workflows -type f ! -name "${BACKEND}.yml" ! -name "${FRONTEND}.yml" -exec rm -f {} +

sed -i "s/\/${BACKEND}//g" .github/workflows/${BACKEND}.yml
sed -i "s/\/${FRONTEND}//g" .github/workflows/${FRONTEND}.yml

sed -i "s/\/${BACKEND}//g" docker-compose.yml
sed -i "s/\/${FRONTEND}//g" docker-compose.yml

cp -r ./backend/$BACKEND/. ./backend
cp -r ./frontend/$FRONTEND/. ./frontend

rm -rf ./backend/$BACKEND
rm -rf ./frontend/$FRONTEND

if [ "$BACKEND" == "spring-boot" ]; then
  mv ./backend/src/main/java/org/bugzkit ./backend/src/main/java/org/$APP_NAME
  mv ./backend/src/test/java/org/bugzkit ./backend/src/test/java/org/$APP_NAME
fi

# replace all occurrences of the bugzkit in all files
find . \
  -type f \
  -not -path "./node_modules/*" \
  -not -path "./target/*" \
  -not -path "./.git/*" \
  -not -path "./build/*" \
  -not -path "./init.sh" \
  -not -path "./.svelte-kit/*" \
  -exec sed -i "s/bugzkit/${APP_NAME}/Ig" {} + > /dev/null 2>&1

clear
echo "Name of your app is: $APP_NAME"
echo "You picked $BACKEND for the backend, and $FRONTEND for the frontend."
