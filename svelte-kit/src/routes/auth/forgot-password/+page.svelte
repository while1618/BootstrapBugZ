<script lang="ts">
  import { Button, Card, FloatingLabelInput } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <form class="flex flex-col space-y-6" action="/">
      <h3 class="grid justify-items-center p-0 text-xl font-medium text-gray-900 dark:text-white">
        Forgot password
      </h3>
      <FloatingLabelInput
        class="space-y-2"
        style="standard"
        id="email"
        name="email"
        type="email"
        label="Email"
      />
      <Button type="submit">Send</Button>
    </form>
  </Card>
</div>

<style>
</style>
