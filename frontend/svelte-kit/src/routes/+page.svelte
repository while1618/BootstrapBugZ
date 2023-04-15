<script lang="ts">
  import GuestHome from '$lib/components/guest-home.svelte';
  import UserHome from '$lib/components/user-home.svelte';
  import type { LayoutData } from './$types';

  export let data: LayoutData;
</script>

{#if data.user}
  <UserHome />
{:else}
  <GuestHome />
{/if}
