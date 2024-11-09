<script lang="ts">
  import logo from '$lib/images/logo.png';
  import * as m from '$lib/paraglide/messages.js';
  import LanguageSwitcher from './language-switcher.svelte';
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
            <li><a href="/auth/sign-in">{m.navbar_singIn()}</a></li>
            <li><a href="/auth/sign-up">{m.navbar_signUp()}</a></li>
          </ul>
        </div>
        <a href="/" class="btn btn-ghost text-2xl normal-case">BootstrapBugZ</a>
      </div>
      <div class="navbar-center hidden lg:flex">
        <ul class="menu menu-horizontal p-0 font-medium">
          <li><a href="/auth/sign-in">{m.navbar_singIn()}</a></li>
          <li><a href="/auth/sign-up">{m.navbar_signUp()}</a></li>
        </ul>
      </div>
      <div class="navbar-end gap-3">
        <ThemeSelector />
        <LanguageSwitcher />
      </div>
    </div>
  </div>
</header>
