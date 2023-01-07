<script lang="ts">
  import type { PageData } from './$types';

  export let data: PageData;
</script>

<main class="bg-white dark:bg-gray-800">
  <div class="flex h-screen items-center justify-center">
    <h1 class="text-3xl font-bold dark:text-white">{data.name}</h1>
  </div>
</main>

<style>
</style>
