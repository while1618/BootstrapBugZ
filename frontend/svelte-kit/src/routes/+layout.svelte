<script lang="ts">
  import GuestNavbar from '$lib/components/guest-navbar.svelte';
  import Loading from '$lib/components/loading.svelte';
  import UserNavbar from '$lib/components/user-navbar.svelte';
  import { userStore } from '$lib/stores/user';
  import { beforeUpdate } from 'svelte';
  import '../app.css';
  import type { LayoutData } from './$types';

  export let data: LayoutData;

  beforeUpdate(() => {
    userStore.set(data.user);
  });
</script>

{#if !data.user}
  <GuestNavbar />
  <slot />
{:else if !$userStore}
  <Loading />
{:else}
  <UserNavbar isAdmin={data.isAdmin} />
  <slot />
{/if}
