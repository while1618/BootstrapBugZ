<script lang="ts">
  import logo from '$lib/images/logo.png';
  import ThemeSelector from './theme-selector.svelte';
</script>

<header class="sticky top-0 z-50 bg-base-100 py-2">
  <div class="container">
    <div class="navbar px-0">
      <div class="navbar-start">
        <div class="dropdown">
          <div tabIndex={0} class="btn btn-circle mr-1 w-12 rounded-full lg:hidden">
            <img alt="logo" src={logo} />
          </div>
          <ul
            tabIndex={0}
            class="menu-compact menu dropdown-content mt-1 w-52 rounded-box bg-base-200 p-2 shadow"
          >
            <li><a href="/auth/sign-in">Sign in</a></li>
            <li><a href="/auth/sign-up">Sign up</a></li>
          </ul>
        </div>
        <a href="/" class="btn btn-ghost text-2xl normal-case">BootstrapBugZ</a>
      </div>
      <div class="navbar-center hidden lg:flex">
        <ul class="menu menu-horizontal p-0 font-medium">
          <li><a href="/auth/sign-in">Sign in</a></li>
          <li><a href="/auth/sign-up">Sign up</a></li>
        </ul>
      </div>
      <div class="navbar-end">
        <ThemeSelector />
      </div>
    </div>
  </div>
</header>
