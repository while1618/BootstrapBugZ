<script lang="ts">
  import { enhance } from '$app/forms';
  import { Button, Card, FloatingLabelInput } from 'flowbite-svelte';
  import { beforeUpdate } from 'svelte';
  import type { ActionData } from './$types';

  interface SignUpErrors {
    firstName: string | null;
    lastName: string | null;
    username: string | null;
    email: string | null;
    password: string | null;
    confirmPassword: string | null;
  }

  export let form: ActionData;
  export let errors: SignUpErrors;

  errors = {
    firstName: null,
    lastName: null,
    username: null,
    email: null,
    password: null,
    confirmPassword: null,
  };

  beforeUpdate(() => {
    Object.keys(errors).forEach((key) => (errors[key as keyof SignUpErrors] = null));
    form?.errorMessage.details.forEach((detail) => {
      const key = detail?.field || null;
      if (key) errors[key as keyof SignUpErrors] = detail.message;
    });
  });
</script>

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <form class="flex flex-col space-y-6" method="POST" action="?/signUp" use:enhance>
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Sign up to BootstrapBugZ
      </h3>
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="firstName"
        name="firstName"
        type="text"
        label="First name"
      />
      {#if errors.firstName}
        <p class="text-red-600">{errors.firstName}</p>
      {/if}
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="lastName"
        name="lastName"
        type="text"
        label="Last name"
      />
      {#if errors.lastName}
        <p class="text-red-600">{errors.lastName}</p>
      {/if}
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="username"
        name="username"
        type="text"
        label="Username"
      />
      {#if errors.username}
        <p class="text-red-600">{errors.username}</p>
      {/if}
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="email"
        name="email"
        type="email"
        label="Email"
      />
      {#if errors.email}
        <p class="text-red-600">{errors.email}</p>
      {/if}
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="password"
        name="password"
        type="password"
        label="Password"
      />
      {#if errors.password}
        <p class="text-red-600">{errors.password}</p>
      {/if}
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="confirmPassword"
        name="confirmPassword"
        type="password"
        label="Confirm password"
      />
      {#if errors.confirmPassword}
        <p class="text-red-600">{errors.confirmPassword}</p>
      {/if}
      <Button type="submit">Sign up</Button>
    </form>
  </Card>
</div>

<style>
</style>
