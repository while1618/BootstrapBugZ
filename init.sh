#!/bin/sh

echo "Pick your frontend: "
FRONEND=$(gum choose "svelte-kit")
echo "Pick your backend: "
BACKED=$(gum choose "spring-boot")

echo "You picked $FRONEND for frontend, and $BACKED for backend."