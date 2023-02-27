<script lang="ts">
  import { enhance } from '$app/forms';
  import en from '$lib/i18n/en.json';
  import { Button, Card, FloatingLabelInput } from 'flowbite-svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let usernameOrEmail = '';
</script>

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <form class="flex flex-col space-y-6" method="POST" action="?/signIn" use:enhance>
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Sign in to BootstrapBugZ
      </h3>
      <div>
        <FloatingLabelInput
          class="space-y-2"
          style="standard"
          id="usernameOrEmail"
          name="usernameOrEmail"
          type="text"
          label="Username or email"
          bind:value={usernameOrEmail}
        />
        {#if form?.errors?.usernameOrEmail}
          <p class="mt-2 text-sm text-red-600">{form.errors.usernameOrEmail}</p>
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
        {#if form?.errorMessage}
          {#each form.errorMessage.details as error}
            <div class="mt-2 flex justify-center text-sm">
              <p class="text-red-600">{error.message}</p>
              {#if error.message === en['user.notActivated']}
                <a
                  href="/auth/resend-confirmation-email?usernameOrEmail={usernameOrEmail}"
                  class="ml-auto text-sm text-blue-700 hover:underline dark:text-blue-500"
                  >Resend confirmation email</a
                >
              {/if}
            </div>
          {/each}
        {/if}
      </div>
      <Button type="submit">Sign in</Button>
      <div class="flex justify-center text-sm font-medium text-gray-500 dark:text-gray-300">
        <div>
          Not registered? <a
            href="/auth/sign-up"
            class="text-blue-700 hover:underline dark:text-blue-500">Sign up</a
          >
        </div>
        <a
          href="/auth/forgot-password"
          class="ml-auto text-sm text-blue-700 hover:underline dark:text-blue-500"
          >Forgot password?</a
        >
      </div>
    </form>
  </Card>
</div>

<style>
</style>
