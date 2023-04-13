<script>
  import { page } from '$app/stores';
  import { Card } from 'flowbite-svelte';
</script>

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full gap-5 border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <div class="flex flex-col text-center">
      <h1 class="text-lg dark:text-white">{$page?.error?.status}</h1>
      <h1 class="text-lg dark:text-white">{$page?.error?.message}</h1>
    </div>
  </Card>
</div>
