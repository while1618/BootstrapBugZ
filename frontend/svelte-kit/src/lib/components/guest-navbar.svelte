<script lang="ts">
  import logo from '$lib/images/logo.png';
  import ThemeSelector from './theme-selector.svelte';
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
    <a href="/auth/sign-in" class="btn">Sign in</a>
    <a href="/auth/sign-up" class="btn">Sign up</a>
  </div>
</nav>
