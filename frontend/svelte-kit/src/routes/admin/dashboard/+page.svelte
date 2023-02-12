<script lang="ts">
  import LockIcon from '$lib/icons/lock-icon.svelte';
  import TrashIcon from '$lib/icons/trash-icon.svelte';
  import XCircleIcon from '$lib/icons/x-circle-icon.svelte';
  import {
    Checkbox,
    TableBody,
    TableBodyCell,
    TableBodyRow,
    TableHead,
    TableHeadCell,
    TableSearch,
  } from 'flowbite-svelte';
  import type { PageServerData } from './$types';

  export let data: PageServerData;
  let searchTerm = '';
  $: filteredUsers = data.users.filter(
    (user) => user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1
  );
</script>

<div class="pl-10 pr-10">
  <TableSearch placeholder="Search by maker name" hoverable={true} bind:inputValue={searchTerm}>
    <TableHead>
      <TableHeadCell />
      <TableHeadCell>ID</TableHeadCell>
      <TableHeadCell>First name</TableHeadCell>
      <TableHeadCell>Last name</TableHeadCell>
      <TableHeadCell>username</TableHeadCell>
      <TableHeadCell>Email</TableHeadCell>
      <TableHeadCell>Activated</TableHeadCell>
      <TableHeadCell>Locked</TableHeadCell>
      <TableHeadCell>Roles</TableHeadCell>
      <TableHeadCell>Actions</TableHeadCell>
    </TableHead>
    <TableBody>
      {#each filteredUsers as user}
        <TableBodyRow>
          <TableBodyCell class="w-10"><Checkbox /></TableBodyCell>
          <TableBodyCell>{user.id}</TableBodyCell>
          <TableBodyCell>{user.firstName}</TableBodyCell>
          <TableBodyCell>{user.lastName}</TableBodyCell>
          <TableBodyCell>{user.username}</TableBodyCell>
          <TableBodyCell>{user.email}</TableBodyCell>
          <TableBodyCell>{user.activated}</TableBodyCell>
          <TableBodyCell>{!user.nonLocked}</TableBodyCell>
          <TableBodyCell
            >{#each user.roles as role}
              {role.name}
            {/each}</TableBodyCell
          >
          <TableBodyCell>
            <div class="flex gap-2">
              <a href="/" class="font-medium text-red-600 hover:underline dark:text-red-500">
                <XCircleIcon />
              </a>
              <a href="/" class="font-medium text-blue-600 hover:underline dark:text-blue-500">
                <LockIcon />
              </a>
              <a href="/" class="font-medium text-red-600 hover:underline dark:text-red-500">
                <TrashIcon />
              </a>
              <a href="/" class="font-medium text-blue-600 hover:underline dark:text-blue-500">
                Change role
              </a>
            </div>
          </TableBodyCell>
        </TableBodyRow>
      {/each}
    </TableBody>
  </TableSearch>
</div>

<style>
</style>
