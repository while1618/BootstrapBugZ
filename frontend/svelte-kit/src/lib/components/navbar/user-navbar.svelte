<script lang="ts">
  import ChartIcon from '$lib/icons/chart.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
  import SettingsIcon from '$lib/icons/settings.svelte';
  import SignOutIcon from '$lib/icons/sign-out.svelte';
  import avatar from '$lib/images/avatar.jpg';
  import * as m from '$lib/paraglide/messages.js';
  import LanguageSwitcher from './language-switcher.svelte';
  import ThemeSelector from './theme-selector.svelte';

  interface Props {
    isAdmin: boolean;
  }

  let { isAdmin }: Props = $props();
</script>

<header class="sticky top-0 z-50 bg-base-100 py-2">
  <div class="container">
    <div class="navbar px-0">
      <div class="navbar-start">
        <a href="/" class="btn btn-ghost text-2xl normal-case">BootstrapBugZ</a>
      </div>
      <div class="navbar-end gap-3">
        <ThemeSelector />
        <LanguageSwitcher />
        <div class="dropdown dropdown-end">
          <div tabindex="-1" class="avatar btn btn-circle btn-ghost">
            <div class="w-10 rounded-full">
              <img src={avatar} alt="Avatar" />
            </div>
          </div>
          <ul
            class="menu-compact menu dropdown-content mt-3 w-52 rounded-box bg-base-200 p-2 shadow"
          >
            <li><a href="/profile"><ProfileIcon /> {m.navbar_profile()}</a></li>
            {#if isAdmin}
              <li><a href="/admin"><ChartIcon /> {m.navbar_admin()}</a></li>
            {/if}
            <li><a href="/profile/settings/personal"><SettingsIcon /> {m.navbar_settings()}</a></li>
            <li><a href="/auth/sign-out"><SignOutIcon /> {m.navbar_signOut()}</a></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</header>
