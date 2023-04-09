<script lang="ts">
  import { Card } from 'flowbite-svelte';
  import type { PageServerData } from './$types';

  export let data: PageServerData;
</script>

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <div class="flex justify-center">
      <h1 class="text-lg dark:text-white">{data?.user?.username}</h1>
    </div>
  </Card>
</div>

<style>
</style>
