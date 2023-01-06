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
  <div class="flex md:order-2">
    <Button size="sm">Get started</Button>
    <DarkMode />
    <NavHamburger on:click={toggle} />
  </div>
  <NavUl {hidden} class="order-1">
    <NavLi href="/" active={true}>Home</NavLi>
    <NavLi href="/about">About</NavLi>
    <NavLi href="/services">Services</NavLi>
    <NavLi href="/pricing">Pricing</NavLi>
    <NavLi href="/contact">Contact</NavLi>
  </NavUl>
</Navbar>
