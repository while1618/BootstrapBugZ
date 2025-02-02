# bugzkit

[![Spring Boot CI](https://github.com/while1618/bugzkit/actions/workflows/spring-boot.yml/badge.svg?branch=master)](https://github.com/while1618/bugzkit/actions/workflows/spring-boot.yml)
[![Svelte Kit CI](https://github.com/while1618/bugzkit/actions/workflows/svelte-kit.yml/badge.svg)](https://github.com/while1618/bugzkit/actions/workflows/svelte-kit.yml)
[![Docs](https://github.com/while1618/bugzkit/actions/workflows/docs.yml/badge.svg)](https://github.com/while1618/bugzkit/actions/workflows/docs.yml)

A production-ready web application template designed to handle essential web app functionalities.
Developers can focus on business logic while using this template as a robust starting point.

[Docs](https://docs.bugzkit.com/)

## Overview

This project is not a no-code solution, nor is it just another SaaS boilerplate stuffed with third-party integrations.
It's designed specifically for experienced developers who want complete control over their applications.
You'll need to understand the written code, which is entirely yours to adapt, extend, and own.

The goal is to provide a reliable and customizable foundation that eliminates repetitive tasks, letting you focus entirely on your business logic.

### Core features

| Feature                  | Description                                                                          |
| ------------------------ | ------------------------------------------------------------------------------------ |
| **Authentication**       | JWT auth with role-based access control                                              |
| **Admin Panel**          | Complete user management interface                                                   |
| **Error Handling**       | Standardized error codes and handling mechanisms                                     |
| **Internationalization** | Built-in i18n support                                                                |
| **Email Support**        | Sending responsive emails ([MJML](https://github.com/mjmlio/mjml)) from your backend |
| **Logging**              | Comprehensive logging and monitoring system                                          |

### Tech Stack

<details>
  <summary>Backend</summary>

- [Java 21](https://openjdk.org/projects/jdk/21/)
- [SpringBoot](https://spring.io/projects/spring-boot)
- [Postgres](https://www.postgresql.org/)
- [Redis](https://redis.io/)

</details>

<details>
  <summary>Frontend</summary>

- [SvelteKit](https://kit.svelte.dev/)
- [TypeScript](https://www.typescriptlang.org/)
- [TailwindCSS](https://tailwindcss.com/)
- [shadcn-svelte](https://github.com/huntabyte/shadcn-svelte)
- [paraglidejs](https://inlang.com/m/gerre34r/library-inlang-paraglideJs)
- [Zod](https://zod.dev/)
- [Superforms](https://superforms.rocks/)
- [Formsnap](https://formsnap.dev/docs)

</details>

<details>
  <summary>Devops</summary>

- [GitHub Actions](https://github.com/features/actions)
- [Docker](https://www.docker.com/)

</details>
