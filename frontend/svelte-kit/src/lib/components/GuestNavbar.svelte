<script lang="ts">
  import logo from '$lib/images/BootstrapBugZ.png';
</script>

<nav class="navbar bg-base-100 px-5">
  <div class="navbar-start">
    <a href="/" class="flex items-center">
      <div class="w-10 rounded-full">
        <img src={logo} alt="BootstrapBugZLogo" />
      </div>
      <span class="text-xl normal-case">BootstrapBugZ</span>
    </a>
  </div>
  <div class="navbar-end gap-3">
    <a href="/auth/sign-in" class="btn">Sign in</a>
    <a href="/auth/sign-up" class="btn">Sign up</a>
  </div>
</nav>
