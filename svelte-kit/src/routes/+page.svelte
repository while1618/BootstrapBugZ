<script lang="ts">
  import NoAuthNavbar from '$lib/navbar/NoAuthNavbar.svelte';
  import { Card } from 'flowbite-svelte';
</script>

<NoAuthNavbar />
<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <div class="flex justify-center">
      <h1 class="text-lg dark:text-white">BootstrapBugZ</h1>
    </div>
  </Card>
</div>

<style>
</style>
