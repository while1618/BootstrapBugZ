<script lang="ts">
  import { Button, Card, Input, Label } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card size="sm" class="w-full">
    <form class="flex flex-col space-y-6" action="/">
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Forgot password
      </h3>
      <Label class="space-y-2">
        <span>Email</span>
        <Input name="email" required />
      </Label>
      <Button type="submit" class="w-full">Send</Button>
    </form>
  </Card>
</div>

<style>
</style>
