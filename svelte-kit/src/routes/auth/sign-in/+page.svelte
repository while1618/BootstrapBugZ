<script lang="ts">
  import { Button, Card, FloatingLabelInput } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card size="sm" class="w-full">
    <form class="flex flex-col space-y-6" action="/">
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Sign in to BootstrapBugZ
      </h3>
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="usernameOrEmail"
        name="usernameOrEmail"
        type="text"
        label="Username or email"
      />
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="password"
        name="password"
        type="password"
        label="Password"
      />
      <Button type="submit" class="w-full">Sign in</Button>
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
