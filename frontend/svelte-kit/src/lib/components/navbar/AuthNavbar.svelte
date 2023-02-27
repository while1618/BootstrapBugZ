<script lang="ts">
  import DashboardIcon from '$lib/icons/dashboard.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
  import SettingsIcon from '$lib/icons/settings.svelte';
  import SignOutIcon from '$lib/icons/sign-out.svelte';
  import avatar from '$lib/images/avatar.jpg';
  import logo from '$lib/images/BootstrapBugZ.png';
  import {
    Avatar,
    CloseButton,
    DarkMode,
    Drawer,
    Dropdown,
    DropdownDivider,
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
  let isAdmin = true;
</script>

<Navbar color="none">
  <NavBrand href="/">
    <img src={logo} class="h-16" alt="BootstrapBugZ Logo" />
    <span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
      BootstrapBugZ
    </span>
  </NavBrand>
  <div class="flex md:gap-2">
    <Avatar border class="hidden md:flex" id="avatar-menu" src={avatar} />
    <Dropdown placement="bottom" triggeredBy="#avatar-menu">
      <DropdownItem href="/user/john.doe" class="flex items-center gap-2">
        <ProfileIcon />
        john.doe
      </DropdownItem>
      <DropdownDivider />
      <DropdownItem href="/" class="flex items-center gap-2">
        <DashboardIcon />
        Dashboard
      </DropdownItem>
      {#if isAdmin}
        <DropdownItem href="/admin/dashboard" class="flex items-center gap-2">
          <DashboardIcon />
          Admin dashboard
        </DropdownItem>
      {/if}
      <DropdownItem href="/user/settings" class="flex items-center gap-2">
        <SettingsIcon />
        Settings
      </DropdownItem>
      <DropdownDivider />
      <DropdownItem href="/auth/sign-out" class="flex items-center gap-2">
        <SignOutIcon />
        Sign out
      </DropdownItem>
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
            <SidebarItem label="john.doe" href="/user/john.doe">
              <svelte:fragment slot="icon">
                <ProfileIcon />
              </svelte:fragment>
            </SidebarItem>
          </SidebarGroup>
          <SidebarGroup border>
            <SidebarItem label="Dashboard" href="/">
              <svelte:fragment slot="icon">
                <DashboardIcon />
              </svelte:fragment>
            </SidebarItem>
            {#if isAdmin}
              <SidebarItem label="Admin dashboard" href="/admin/dashboard">
                <svelte:fragment slot="icon">
                  <DashboardIcon />
                </svelte:fragment>
              </SidebarItem>
            {/if}
            <SidebarItem label="Settings" href="/user/settings">
              <svelte:fragment slot="icon">
                <SettingsIcon />
              </svelte:fragment>
            </SidebarItem>
          </SidebarGroup>
          <SidebarGroup border>
            <SidebarItem label="Sign out" href="/">
              <svelte:fragment slot="icon">
                <SignOutIcon />
              </svelte:fragment>
            </SidebarItem>
          </SidebarGroup>
        </SidebarWrapper>
      </Sidebar>
    </Drawer>
  </div>
</Navbar>
