<script lang="ts">
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
    <div class="navbar px-0">
      <div class="navbar-start">
        <label for="my-drawer" class="btn btn-square btn-ghost lg:hidden">
          <MenuIcon />
        </label>
        <a href="/" class="btn btn-ghost text-2xl">BootstrapBugZ</a>
      </div>
      <div class="navbar-center hidden lg:flex">
        <ul class="menu menu-horizontal p-0">
          <li><a href="/profile"><UserIcon /> {m.navbar_profile()}</a></li>
          {#if isAdmin}
            <li><a href="/admin"><ChartBarIncreasingIcon /> {m.navbar_admin()}</a></li>
          {/if}
          <li><a href="/profile/settings/personal"><SettingsIcon /> {m.navbar_settings()}</a></li>
          <li><a href="/auth/sign-out"><LogOutIcon /> {m.navbar_signOut()}</a></li>
        </ul>
      </div>
      <div class="navbar-end hidden gap-3 lg:flex">
        <ThemeSelector />
        <LanguageSwitcher />
      </div>
    </div>

    <div class="drawer">
      <input id="my-drawer" type="checkbox" class="drawer-toggle" />
      <div class="drawer-side">
        <label for="my-drawer" aria-label="close sidebar" class="drawer-overlay"></label>
        <ul class="menu min-h-full w-80 gap-2 bg-base-200 p-4 text-base-content">
          <li><a href="/profile"><UserIcon /> {m.navbar_profile()}</a></li>
          {#if isAdmin}
            <li><a href="/admin"><ChartBarIncreasingIcon /> {m.navbar_admin()}</a></li>
          {/if}
          <li><a href="/profile/settings/personal"><SettingsIcon /> {m.navbar_settings()}</a></li>
          <li><a href="/auth/sign-out"><LogOutIcon /> {m.navbar_signOut()}</a></li>
          <li><LanguageSwitcher /></li>
          <li class="w-8"><ThemeSelector /></li>
        </ul>
      </div>
    </div>
  </div>
</header>
