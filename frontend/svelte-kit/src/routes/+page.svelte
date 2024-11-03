<script lang="ts">
  import GuestHome from '$lib/components/home/guest-home.svelte';
  import UserHome from '$lib/components/home/user-home.svelte';
  import type { LayoutData } from './$types';

  interface Props {
    data: LayoutData;
  }

  let { data }: Props = $props();
</script>

{#if data.user}
  <UserHome />
{:else}
  <GuestHome />
{/if}
