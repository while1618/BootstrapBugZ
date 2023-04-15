<script lang="ts">
  import GuestHome from '$lib/components/GuestHome.svelte';
  import UserHome from '$lib/components/UserHome.svelte';
  import type { LayoutData } from './$types';

  export let data: LayoutData;
</script>

{#if data.user}
  <UserHome />
{:else}
  <GuestHome />
{/if}
