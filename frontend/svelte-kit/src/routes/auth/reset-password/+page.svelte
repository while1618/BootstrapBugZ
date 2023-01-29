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
    <form class="flex flex-col space-y-6" method="POST" action="?/resetPassword" use:enhance>
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Reset password
      </h3>
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
      {#if form?.errors?.accessToken}
        <p class="mt-2 text-sm text-red-600">{form.errors.accessToken}</p>
      {/if}
      {#if form?.errorMessage}
        {#each form.errorMessage.details as error}
          <p class="mt-2 text-sm text-red-600">{error.message}</p>
        {/each}
      {/if}
      <Button type="submit">Reset password</Button>
    </form>
  </Card>
</div>

<style>
</style>
