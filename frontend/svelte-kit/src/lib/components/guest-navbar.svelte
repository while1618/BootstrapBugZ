<script lang="ts">
  import MoonIcon from '$lib/icons/moon.svelte';
  import SunIcon from '$lib/icons/sun.svelte';
  import logo from '$lib/images/logo.png';
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
    <a href="/auth/sign-in" class="btn">Sign in</a>
    <a href="/auth/sign-up" class="btn">Sign up</a>
  </div>
</nav>
