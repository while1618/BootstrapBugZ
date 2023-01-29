<script lang="ts">
  import { enhance } from '$app/forms';
  import { Button, Card, FloatingLabelInput } from 'flowbite-svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
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
      <div>
        <FloatingLabelInput
          class="space-y-2"
          style="standard"
          id="firstName"
          name="firstName"
          type="text"
          label="First name"
        />
        {#if form?.errors?.firstName}
          <p class="mt-2 text-sm text-red-600">{form.errors.firstName}</p>
        {/if}
      </div>
      <div>
        <FloatingLabelInput
          class="space-y-2"
          style="standard"
          id="lastName"
          name="lastName"
          type="text"
          label="Last name"
        />
        {#if form?.errors?.lastName}
          <p class="mt-2 text-sm text-red-600">{form.errors.lastName}</p>
        {/if}
      </div>
      <div>
        <FloatingLabelInput
          class="space-y-2"
          style="standard"
          id="username"
          name="username"
          type="text"
          label="Username"
        />
        {#if form?.errors?.username}
          <p class="mt-2 text-sm text-red-600">{form.errors.username}</p>
        {/if}
      </div>
      <div>
        <FloatingLabelInput
          class="space-y-2"
          style="standard"
          id="email"
          name="email"
          type="email"
          label="Email"
        />
        {#if form?.errors?.email}
          <p class="mt-2 text-sm text-red-600">{form.errors.email}</p>
        {/if}
      </div>
      <div>
        <FloatingLabelInput
          class="space-y-2"
          style="standard"
          id="password"
          name="password"
          type="password"
          label="Password"
        />
        {#if form?.errors?.password}
          <p class="mt-2 text-sm text-red-600">{form.errors.password}</p>
        {/if}
      </div>
      <div>
        <FloatingLabelInput
          class="space-y-2"
          style="standard"
          id="confirmPassword"
          name="confirmPassword"
          type="password"
          label="Confirm password"
        />
        {#if form?.errors?.confirmPassword}
          <p class="mt-2 text-sm text-red-600">{form.errors.confirmPassword}</p>
        {/if}
      </div>
      {#if form?.errorMessage}
        <p class="mt-2 text-sm text-red-600">Unexpected error occurred.</p>
      {/if}
      <Button type="submit">Sign up</Button>
    </form>
  </Card>
</div>

<style>
</style>
