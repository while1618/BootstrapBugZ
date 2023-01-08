<script lang="ts">
  import { Button, DarkMode, Navbar, NavBrand } from 'flowbite-svelte';
</script>

<Navbar color="none">
  <NavBrand href="/">
    <img
      src="https://flowbite.com/docs/images/logo.svg"
      class="mr-3 h-6 sm:h-9"
      alt="Flowbite Logo"
    />
    <span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
      BootstrapBugZ
    </span>
  </NavBrand>
  <div class="flex items-center gap-2">
    <Button outline size="sm" href="/auth/sign-in">Sign in</Button>
    <Button size="sm" href="/auth/sign-up">Sign up</Button>
    <DarkMode />
  </div>
</Navbar>
