<script lang="ts">
  import logo from '$lib/images/BootstrapBugZ.png';
  import {
    Button,
    CloseButton,
    DarkMode,
    Drawer,
    Navbar,
    NavBrand,
    NavHamburger,
  } from 'flowbite-svelte';
  import { sineIn } from 'svelte/easing';

  const transitionParams = {
    x: 320,
    duration: 200,
    easing: sineIn,
  };
  let hidden = true;
</script>

<Navbar color="none">
  <NavBrand href="/">
    <img src={logo} class="h-16" alt="BootstrapBugZ Logo" />
    <span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
      BootstrapBugZ
    </span>
  </NavBrand>
  <div class="flex md:gap-2">
    <div class="hidden md:flex md:gap-2">
      <Button outline size="sm" href="/auth/sign-in">Sign in</Button>
      <Button size="sm" href="/auth/sign-up">Sign up</Button>
    </div>
    <DarkMode />
    <div class="flex md:hidden">
      <NavHamburger on:click={() => (hidden = false)} />
      <Drawer placement="right" transitionType="fly" {transitionParams} bind:hidden id="sidebar">
        <div class="flex items-center">
          <h5
            class="mb-4 inline-flex items-center text-base font-semibold text-gray-500 dark:text-gray-400"
          >
            Menu
          </h5>
          <CloseButton on:click={() => (hidden = true)} class="mb-4 dark:text-white" />
        </div>
        <div class="flex flex-col gap-2">
          <Button on:click={() => (hidden = true)} outline size="sm" href="/auth/sign-in"
            >Sign in</Button
          >
          <Button on:click={() => (hidden = true)} size="sm" href="/auth/sign-up">Sign up</Button>
        </div>
      </Drawer>
    </div>
  </div>
</Navbar>
