<script lang="ts">
  import ChartIcon from '$lib/icons/chart.svelte';
  import MoonIcon from '$lib/icons/moon.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
  import SettingsIcon from '$lib/icons/settings.svelte';
  import SignOutIcon from '$lib/icons/sign-out.svelte';
  import SunIcon from '$lib/icons/sun.svelte';
  import avatar from '$lib/images/avatar.jpg';
  import logo from '$lib/images/logo.png';

  export let isAdmin: boolean;
</script>

<nav class="navbar bg-base-100 px-5">
  <div class="navbar-start">
    <a href="/" class="flex items-center">
      <div class="w-10 rounded-full">
        <img src={logo} alt="logo" />
      </div>
      <span class="text-xl normal-case">BootstrapBugZ</span>
    </a>
  </div>
  <div class="navbar-end gap-3">
    <label class="swap swap-rotate">
      <input type="checkbox" class="theme-controller" value="light" />
      <SunIcon />
      <MoonIcon />
    </label>
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
