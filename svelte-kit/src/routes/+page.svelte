<script lang="ts">
  import NoAuthNavbar from '$lib/navbar/NoAuthNavbar.svelte';
  import UserNavbar from '$lib/navbar/UserNavbar.svelte';
  import { Button, Card } from 'flowbite-svelte';

  let signedIn = false;
</script>

{#if signedIn}
  <UserNavbar />
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
    <Button on:click={() => (signedIn = !signedIn)}>Switch Navbar</Button>
  </Card>
</div>

<style>
</style>
