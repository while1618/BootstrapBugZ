<script lang="ts">
  import logo from '$lib/images/BootstrapBugZ.png';
  import { Button, DarkMode, Navbar, NavBrand } from 'flowbite-svelte';
</script>

<Navbar color="none">
  <NavBrand href="/">
    <img src={logo} class="h-16" alt="BootstrapBugZ Logo" />
    <span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
      BootstrapBugZ
    </span>
  </NavBrand>
  <div class="flex gap-2">
    <Button outline size="sm" href="/auth/sign-in">Sign in</Button>
    <Button size="sm" href="/auth/sign-up">Sign up</Button>
    <DarkMode />
  </div>
</Navbar>
