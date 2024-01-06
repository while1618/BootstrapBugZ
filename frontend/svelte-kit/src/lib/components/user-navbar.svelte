<script lang="ts">
  import ChartIcon from '$lib/icons/chart.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
  import SettingsIcon from '$lib/icons/settings.svelte';
  import SignOutIcon from '$lib/icons/sign-out.svelte';
  import avatar from '$lib/images/avatar.jpg';
  import logo from '$lib/images/logo.png';
  import ThemeSelector from './theme-selector.svelte';

  export let isAdmin: boolean;
</script>

<nav class="navbar bg-base-100 px-5">
  <div class="navbar-start">
    <a href="/" class="btn btn-ghost flex items-center">
      <div class="w-10 rounded-full">
        <img src={logo} alt="logo" />
      </div>
      <span class="text-xl normal-case">BootstrapBugZ</span>
    </a>
  </div>
  <div class="navbar-end gap-3">
    <ThemeSelector />
    <div class="dropdown dropdown-end">
      <div tabindex="-1" class="avatar btn btn-circle btn-ghost">
        <div class="w-10 rounded-full">
          <img src={avatar} alt="Avatar" />
        </div>
      </div>
      <ul class="menu-compact menu dropdown-content mt-3 w-52 rounded-box bg-base-100 p-2 shadow">
        <li><a href="/profile"><ProfileIcon /> Profile</a></li>
        {#if isAdmin}
          <li><a href="/admin"><ChartIcon /> Admin</a></li>
        {/if}
        <li><a href="/profile/settings/personal"><SettingsIcon /> Settings</a></li>
        <li><a href="/auth/sign-out"><SignOutIcon /> Sign out</a></li>
      </ul>
    </div>
  </div>
</nav>
