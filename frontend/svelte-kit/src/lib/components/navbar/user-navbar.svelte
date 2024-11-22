<script lang="ts">
  import { Button } from '$lib/components/ui/button';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import { Separator } from '$lib/components/ui/separator';
  import * as Sheet from '$lib/components/ui/sheet';
  import * as m from '$lib/paraglide/messages.js';
  import {
    ChartBarIncreasingIcon,
    LogOutIcon,
    MenuIcon,
    SettingsIcon,
    UserIcon,
  } from 'lucide-svelte';
  import LanguageSwitcher from './language-switcher.svelte';
  import ThemeSelector from './theme-selector.svelte';

  interface Props {
    isAdmin: boolean;
  }

  const { isAdmin }: Props = $props();
</script>

<header class="sticky top-0 z-50 py-2">
  <div class="container">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-3 lg:hidden">
        <Sheet.Root>
          <Sheet.Trigger>
            <MenuIcon />
            <span class="sr-only">Open Menu</span>
          </Sheet.Trigger>
          <Sheet.Content side="left">
            <Sheet.Header>
              <Sheet.Title class="self-center">BootstrapBugZ</Sheet.Title>
              <Separator />
              <Sheet.Description>
                <div class="flex w-full flex-col gap-2">
                  <Button href="/profile" variant="ghost">
                    <UserIcon />
                    {m.navbar_profile()}
                  </Button>
                  <Button href="/profile/settings" variant="ghost">
                    <SettingsIcon />
                    {m.navbar_settings()}
                  </Button>
                  {#if isAdmin}
                    <Button href="/admin" variant="ghost">
                      <ChartBarIncreasingIcon />
                      {m.navbar_admin()}
                    </Button>
                  {/if}
                  <Separator />
                  <Button href="/auth/sign-out" variant="ghost">
                    <LogOutIcon />
                    {m.navbar_signOut()}
                  </Button>
                </div>
              </Sheet.Description>
              <Separator />
              <div class="self-center"><LanguageSwitcher /></div>
            </Sheet.Header>
          </Sheet.Content>
        </Sheet.Root>
      </div>

      <Button href="/" class="text-2xl" variant="ghost">BootstrapBugZ</Button>

      <div class="ml-auto flex items-center gap-3">
        <div class="flex gap-3 lg:hidden">
          <ThemeSelector />
        </div>

        <div class="hidden gap-3 lg:flex">
          <ThemeSelector />
          <LanguageSwitcher />
          <DropdownMenu.Root>
            <DropdownMenu.Trigger>
              {#snippet child({ props })}
                <Button {...props} variant="ghost" size="icon"><UserIcon /></Button>
              {/snippet}
            </DropdownMenu.Trigger>
            <DropdownMenu.Content class="w-56">
              <DropdownMenu.Group>
                <DropdownMenu.GroupHeading>My Account</DropdownMenu.GroupHeading>
                <DropdownMenu.Separator />
                <DropdownMenu.Group>
                  <DropdownMenu.Item class="cursor-pointer">
                    {#snippet child({ props })}
                      <a {...props} href="/profile"><UserIcon />{m.navbar_profile()}</a>
                    {/snippet}
                  </DropdownMenu.Item>
                  <DropdownMenu.Item class="cursor-pointer">
                    {#snippet child({ props })}
                      <a {...props} href="/profile/settings">
                        <SettingsIcon />{m.navbar_settings()}
                      </a>
                    {/snippet}
                  </DropdownMenu.Item>
                  {#if isAdmin}
                    <DropdownMenu.Item class="cursor-pointer">
                      {#snippet child({ props })}
                        <a {...props} href="/admin"><ChartBarIncreasingIcon />{m.navbar_admin()}</a>
                      {/snippet}
                    </DropdownMenu.Item>
                  {/if}
                </DropdownMenu.Group>
                <DropdownMenu.Separator />
                <DropdownMenu.Group>
                  <DropdownMenu.Item class="cursor-pointer">
                    {#snippet child({ props })}
                      <a {...props} href="/auth/sign-out"><LogOutIcon />{m.navbar_signOut()}</a>
                    {/snippet}
                  </DropdownMenu.Item>
                </DropdownMenu.Group>
              </DropdownMenu.Group>
            </DropdownMenu.Content>
          </DropdownMenu.Root>
        </div>
      </div>
    </div>
  </div>
</header>
