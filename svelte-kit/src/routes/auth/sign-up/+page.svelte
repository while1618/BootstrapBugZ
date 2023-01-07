<script lang="ts">
  import { Button, Card, Input, Label } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card size="sm" class="w-full">
    <form class="flex flex-col space-y-6" action="/">
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Sign up to BootstrapBugZ
      </h3>
      <Label class="space-y-2">
        <span>First name</span>
        <Input name="firstName" required />
      </Label>
      <Label class="space-y-2">
        <span>Last name</span>
        <Input name="lastName" required />
      </Label>
      <Label class="space-y-2">
        <span>Username</span>
        <Input name="username" required />
      </Label>
      <Label class="space-y-2">
        <span>Email</span>
        <Input name="email" required />
      </Label>
      <Label class="space-y-2">
        <div class="flex justify-between">
          <span>Password</span>
        </div>
        <Input type="password" name="password" required />
      </Label>
      <Label class="space-y-2">
        <div class="flex justify-between">
          <span>Confirm Password</span>
        </div>
        <Input type="password" name="confirmPassword" required />
      </Label>
      <Button type="submit" class="w-full">Sign up</Button>
    </form>
  </Card>
</div>

<style>
</style>
