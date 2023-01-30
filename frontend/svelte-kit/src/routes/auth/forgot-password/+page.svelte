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
    <form class="flex flex-col space-y-6" method="POST" action="?/forgotPassword" use:enhance>
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Forgot password
      </h3>
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
        {#if form?.errorMessage}
          {#each form.errorMessage.details as error}
            <p class="mt-2 text-sm text-red-600">{error.message}</p>
          {/each}
        {/if}
      </div>
      <Button type="submit">Send</Button>
    </form>
  </Card>
</div>

<style>
</style>
