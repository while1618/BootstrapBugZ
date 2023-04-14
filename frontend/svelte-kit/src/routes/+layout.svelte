<script lang="ts">
  import { userStore } from '$lib/stores/user';
  import '../app.css';

  import AuthNavbar from '$lib/components/navbar/AuthNavbar.svelte';
  import NoAuthNavbar from '$lib/components/navbar/NoAuthNavbar.svelte';
  import { beforeUpdate } from 'svelte';
  import type { LayoutData } from './$types';

  export let data: LayoutData;
  beforeUpdate(() => {
    userStore.set(data.user);
  });
</script>

<div class="flex min-h-screen flex-col bg-gray-50 dark:bg-gray-800">
  {#if data.user}
    <AuthNavbar />
  {:else}
    <NoAuthNavbar />
  {/if}
  <slot />
</div>
