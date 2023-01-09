<script lang="ts">
  import logo from '$lib/images/BootstrapBugZ.png';
  import github from '$lib/images/github-mark.svg';
  import {
    Avatar,
    CloseButton,
    DarkMode,
    Drawer,
    Dropdown,
    DropdownDivider,
    DropdownHeader,
    DropdownItem,
    Navbar,
    NavBrand,
    NavHamburger,
    Sidebar,
    SidebarGroup,
    SidebarItem,
    SidebarWrapper,
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
    <Avatar class="hidden md:flex" id="avatar-menu" src={github} />
    <Dropdown placement="bottom" triggeredBy="#avatar-menu">
      <DropdownHeader>
        <a href="/" class="block text-sm">@while1618</a>
      </DropdownHeader>
      <DropdownItem href="/">Dashboard</DropdownItem>
      <DropdownItem href="/user/settings">Settings</DropdownItem>
      <DropdownDivider />
      <DropdownItem href="/">Sign out</DropdownItem>
    </Dropdown>
    <DarkMode />
    <NavHamburger on:click={() => (hidden = false)} />
    <Drawer placement="right" transitionType="fly" {transitionParams} bind:hidden>
      <div class="flex items-center">
        <h5 class="mb-4 inline-flex font-semibold text-gray-500 dark:text-gray-400">Menu</h5>
        <CloseButton on:click={() => (hidden = true)} class="mb-4 dark:text-white" />
      </div>
      <Sidebar>
        <SidebarWrapper>
          <SidebarGroup>
            <SidebarItem label="@while1618" href="/" />
          </SidebarGroup>
          <SidebarGroup border>
            <SidebarItem label="Dashboard" href="/" />
            <SidebarItem label="Settings" href="/user/settings" />
          </SidebarGroup>
          <SidebarGroup border>
            <SidebarItem label="Sign out" href="/" />
          </SidebarGroup>
        </SidebarWrapper>
      </Sidebar>
    </Drawer>
  </div>
</Navbar>
