<script lang="ts">
  import github from '$lib/images/github-mark.svg';
  import {
    Avatar,
    DarkMode,
    Dropdown,
    DropdownDivider,
    DropdownHeader,
    DropdownItem,
    Navbar,
    NavBrand,
  } from 'flowbite-svelte';
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
    <Avatar id="avatar-menu" src={github} />
    <DarkMode />
  </div>
  <Dropdown placement="bottom" triggeredBy="#avatar-menu">
    <DropdownHeader>
      <span class="block text-sm"> Bonnie Green </span>
      <span class="block truncate text-sm font-medium"> name@flowbite.com </span>
    </DropdownHeader>
    <DropdownItem href="/user/john.doe">Profile</DropdownItem>
    <DropdownItem href="/">Dashboard</DropdownItem>
    <DropdownItem href="/user/settings">Settings</DropdownItem>
    <DropdownDivider />
    <DropdownItem>Sign out</DropdownItem>
  </Dropdown>
</Navbar>
