<script lang="ts">
  import { Button, Card, FloatingLabelInput } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card size="sm" class="w-full">
    <form class="flex flex-col space-y-6" action="/">
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
      <Button type="submit" class="w-full">Reset password</Button>
    </form>
  </Card>
</div>

<style>
</style>
