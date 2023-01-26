<script lang="ts">
  import { enhance } from '$app/forms';
  import { Button, Card, FloatingLabelInput } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <form class="flex flex-col space-y-6" method="PUT" action="?/resetPassword" use:enhance>
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Reset password
      </h3>
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="password"
        name="password"
        type="password"
        label="Password"
      />
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="confirmPassword"
        name="confirmPassword"
        type="password"
        label="Confirm password"
      />
      <Button type="submit">Reset password</Button>
    </form>
  </Card>
</div>

<style>
</style>
