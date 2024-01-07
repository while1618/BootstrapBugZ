<script lang="ts">
  import { userStore } from '$lib/stores/user';
  import '../app.css';

  import GuestNavbar from '$lib/components/guest-navbar.svelte';
  import Loading from '$lib/components/loading.svelte';
  import UserNavbar from '$lib/components/user-navbar.svelte';
  import { beforeUpdate } from 'svelte';
  import type { LayoutData } from './$types';

  export let data: LayoutData;
  beforeUpdate(() => {
    userStore.set(data.user);
  });
</script>

<div class="bg-base-200 flex h-screen flex-col overflow-hidden">
  {#if !data.user}
    <GuestNavbar />
    <slot />
  {:else if !$userStore}
    <Loading />
  {:else}
    <UserNavbar isAdmin={data.isAdmin} />
    <slot />
  {/if}
</div>
