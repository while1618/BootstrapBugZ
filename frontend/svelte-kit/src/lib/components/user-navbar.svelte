<script lang="ts">
  import ChartIcon from '$lib/icons/chart.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
  import SettingsIcon from '$lib/icons/settings.svelte';
  import SignOutIcon from '$lib/icons/sign-out.svelte';
  import avatar from '$lib/images/avatar.jpg';
  import logo from '$lib/images/logo.png';
  // import { RoleName } from '$lib/models/user/role';
  // import { userStore } from '$lib/stores/user';

  // $: isAdmin = $userStore?.roles.some((role) => role.name === RoleName.ADMIN);
  $: isAdmin = true;
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
  <div class="navbar-end">
    <div class="dropdown-end dropdown">
      <div tabindex="-1" class="btn-ghost btn-circle avatar btn">
        <div class="w-10 rounded-full">
          <img src={avatar} alt="Avatar" />
        </div>
      </div>
      <ul class="dropdown-content menu rounded-box menu-compact mt-3 w-52 bg-base-100 p-2 shadow">
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
