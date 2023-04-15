<script lang="ts">
  import avatar from '$lib/images/avatar.jpg';
  import logo from '$lib/images/logo.png';
  import { RoleName } from '$lib/models/user/role';
  import { userStore } from '$lib/stores/user';

  $: username = $userStore?.username;
  $: isAdmin = $userStore?.roles.some((role) => role.name === RoleName.ADMIN);
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
        <li><a href="/user/{username}">Profile</a></li>
        {#if isAdmin}
          <li><a href="/admin/dashboard">Admin dashboard</a></li>
        {/if}
        <li><a href="/user/settings/profile">Settings</a></li>
        <li><a href="/auth/sign-out">Sign out</a></li>
      </ul>
    </div>
  </div>
</nav>
