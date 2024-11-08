<script lang="ts">
  import { Separator } from '$lib/components/ui/separator';
  import SidebarNav from './(components)/sidebar-nav.svelte';

  interface Props {
    children?: import('svelte').Snippet;
  }

  let { children }: Props = $props();

  const sidebarNavItems = [
    {
      title: 'Personal',
      href: '/profile/settings/personal',
    },
    {
      title: 'Security',
      href: '/profile/settings/security',
    },
  ];
</script>

<div class="md:hidden">
  <p>mobile version to be implemented</p>
</div>
<div class="hidden space-y-6 p-10 pb-16 md:block">
  <div class="space-y-0.5">
    <h2 class="text-2xl font-bold tracking-tight">Settings</h2>
    <p class="text-muted-foreground">Manage your account settings.</p>
  </div>
  <Separator class="my-6" />
  <div class="flex flex-col space-y-8 lg:flex-row lg:space-x-12 lg:space-y-0">
    <aside class="-mx-4 lg:w-1/5">
      <SidebarNav items={sidebarNavItems} />
    </aside>
    <div class="flex-1 lg:max-w-2xl">
      {@render children?.()}
    </div>
  </div>
</div>
