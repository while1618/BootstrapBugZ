<script lang="ts">
  import { Button, Card, Input, Label } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card size="sm" class="w-full">
    <form class="flex flex-col space-y-6" action="/">
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Sign in to BootstrapBugZ
      </h3>
      <Label class="space-y-2">
        <span>Username or Email</span>
        <Input name="usernameOrEmail" required />
      </Label>
      <Label class="space-y-2">
        <div class="flex justify-between">
          <span>Password</span>
          <a
            href="/auth/forgot-password"
            class="ml-auto text-sm text-blue-700 hover:underline dark:text-blue-500"
            >Forgot password?</a
          >
        </div>
        <Input type="password" name="password" required />
      </Label>
      <Button type="submit" class="w-full">Sign in</Button>
      <div class="text-sm font-medium text-gray-500 dark:text-gray-300">
        Not registered? <a
          href="/auth/sign-up"
          class="text-blue-700 hover:underline dark:text-blue-500">Create an account</a
        >
      </div>
    </form>
  </Card>
</div>

<style>
</style>
