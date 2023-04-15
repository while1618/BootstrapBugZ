<script lang="ts">
  import { userStore } from '$lib/stores/user';
  import '../app.css';

  import GuestNavbar from '$lib/components/guest-navbar.svelte';
  import UserNavbar from '$lib/components/user-navbar.svelte';
  import { beforeUpdate } from 'svelte';
  import type { LayoutData } from './$types';

  export let data: LayoutData;
  beforeUpdate(() => {
    userStore.set(data.user);
  });
</script>

<div class="flex h-screen flex-col overflow-hidden bg-base-200">
  {#if data.user}
    <UserNavbar />
  {:else}
    <GuestNavbar />
  {/if}
  <slot />
</div>
