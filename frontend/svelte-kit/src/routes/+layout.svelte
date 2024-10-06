<script lang="ts">
  import { navigating } from '$app/stores';
  import GuestNavbar from '$lib/components/navbar/guest-navbar.svelte';
  import UserNavbar from '$lib/components/navbar/user-navbar.svelte';
  import Loading from '$lib/components/shared/loading.svelte';
  import { i18n } from '$lib/i18n';
  import { userStore } from '$lib/stores/user';
  import { ParaglideJS } from '@inlang/paraglide-sveltekit';
  import { beforeUpdate } from 'svelte';
  import '../app.css';
  import type { LayoutData } from './$types';

  export let data: LayoutData;

  beforeUpdate(() => {
    userStore.set(data.user);
  });
</script>

{#if $navigating}
  <Loading />
{:else}
  <ParaglideJS {i18n}>
    {#if !data.user}
      <GuestNavbar />
      <slot />
    {:else if !$userStore}
      <Loading />
    {:else}
      <UserNavbar isAdmin={data.isAdmin} />
      <slot />
    {/if}
  </ParaglideJS>
{/if}
