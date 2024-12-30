# SvelteKit

## Running locally (dev)

### Requirements

Before running, you'll need to have the following software installed on your machine:
- node
- npm
- pnpm
- docker

### Running

The API needs to be up and running in order for the UI to function properly, and the easiest way to do it is just to run `docker-compose-api-dev.yml`. Check README.md file in `backend/spring-boot` for more info.

Once API is running, create `.env` file in root of svelte-kit directory with the following data:

```bash
# Public
PUBLIC_APP_NAME=BootstrapBugZ
PUBLIC_API_URL=http://localhost:8080/v1
# Private
JWT_SECRET=secret
```

To start the app, first install dependencies with:

```bash
pnpm install
```

And then run the app with:

```bash
pnpm run dev
```
