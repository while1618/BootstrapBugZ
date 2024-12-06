<script lang="ts">
  import { navigating } from '$app/stores';
  import GuestNavbar from '$lib/components/navbar/guest-navbar.svelte';
  import UserNavbar from '$lib/components/navbar/user-navbar.svelte';
  import Loading from '$lib/components/shared/loading.svelte';
  import { Toaster } from '$lib/components/ui/sonner';
  import { i18n } from '$lib/i18n';
  import { ParaglideJS } from '@inlang/paraglide-sveltekit';
  import '../app.css';
  import type { LayoutData } from './$types';

  interface Props {
    data: LayoutData;
    children?: import('svelte').Snippet;
  }

  const { data, children }: Props = $props();
</script>

<ParaglideJS {i18n}>
  <Toaster />
  {#if $navigating}
    <Loading />
  {:else if data.profile}
    <UserNavbar isAdmin={data.isAdmin} />
    {@render children?.()}
  {:else}
    <GuestNavbar />
    {@render children?.()}
  {/if}
</ParaglideJS>
