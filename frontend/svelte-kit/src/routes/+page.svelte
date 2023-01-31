<script lang="ts">
  import AuthNavbar from '$lib/components/navbar/AuthNavbar.svelte';
  import NoAuthNavbar from '$lib/components/navbar/NoAuthNavbar.svelte';
  import { Card } from 'flowbite-svelte';
  import type { PageServerData } from './$types';

  export let data: PageServerData;
</script>

{#if data.user}
  <AuthNavbar />
{:else}
  <NoAuthNavbar />
{/if}

<div class="flex justify-center pt-10">
  <Card
    size="sm"
    color="none"
    class="w-full gap-5 border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <div class="flex justify-center">
      <h1 class="text-lg dark:text-white">BootstrapBugZ</h1>
    </div>
  </Card>
</div>

<style>
</style>
