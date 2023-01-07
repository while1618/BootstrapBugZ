<script lang="ts">
  import { Button, DarkMode, Navbar, NavBrand, NavHamburger, NavLi, NavUl } from 'flowbite-svelte';
</script>

<Navbar let:hidden let:toggle>
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
  <div class="flex gap-2 md:order-2">
    <Button size="sm">Get started</Button>
    <DarkMode />
    <NavHamburger on:click={toggle} />
  </div>
  <NavUl {hidden} class="order-1">
    <NavLi href="/" active={true}>Home</NavLi>
    <NavLi href="/auth/sign-in">Sign In</NavLi>
    <NavLi href="/auth/sign-up">Sign Up</NavLi>
    <NavLi href="/auth/forgot-password">Forgot Password</NavLi>
    <NavLi href="/auth/reset-password">Reset Password</NavLi>
    <NavLi href="/user/john.doe">Profile</NavLi>
    <NavLi href="/user/settings">Settings</NavLi>
  </NavUl>
</Navbar>
