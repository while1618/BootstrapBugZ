<script lang="ts">
  import { Card } from 'flowbite-svelte';
  import type { PageData } from './$types';

  export let data: PageData;
</script>

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <div class="flex justify-center">
      <h1 class="text-lg dark:text-white">{data.name}</h1>
    </div>
  </Card>
</div>

<style>
</style>
