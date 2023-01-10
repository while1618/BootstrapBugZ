<script lang="ts">
  import LockIcon from '$lib/icons/lock.svelte';
  import TrashIcon from '$lib/icons/trash.svelte';
  import XCircleIcon from '$lib/icons/x-circle.svelte';
  import {
    Checkbox,
    TableBody,
    TableBodyCell,
    TableBodyRow,
    TableHead,
    TableHeadCell,
    TableSearch,
  } from 'flowbite-svelte';
  let searchTerm = '';
  let users = [
    {
      id: 1,
      firstName: 'Admin',
      lastName: 'Admin',
      username: 'admin',
      email: 'admin@localhost.com',
      activated: true,
      nonLocker: true,
      roles: ['USER', 'ADMIN'],
    },
    {
      id: 2,
      firstName: 'John',
      lastName: 'Doe',
      username: 'john.doe',
      email: 'john.doe@localhost.com',
      activated: true,
      nonLocker: true,
      roles: ['USER'],
    },
    {
      id: 3,
      firstName: 'Jane',
      lastName: 'Doe',
      username: 'jane.doe',
      email: 'jane.doe@localhost.com',
      activated: true,
      nonLocker: true,
      roles: ['USER'],
    },
  ];
  $: filteredItems = users.filter(
    (item) => item.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1
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
      <TableHeadCell>Non locked</TableHeadCell>
      <TableHeadCell>Roles</TableHeadCell>
      <TableHeadCell>Actions</TableHeadCell>
    </TableHead>
    <TableBody>
      {#each filteredItems as item}
        <TableBodyRow>
          <TableBodyCell class="w-10"><Checkbox /></TableBodyCell>
          <TableBodyCell>{item.id}</TableBodyCell>
          <TableBodyCell>{item.firstName}</TableBodyCell>
          <TableBodyCell>{item.lastName}</TableBodyCell>
          <TableBodyCell>{item.username}</TableBodyCell>
          <TableBodyCell>{item.email}</TableBodyCell>
          <TableBodyCell>{item.activated}</TableBodyCell>
          <TableBodyCell>{item.nonLocker}</TableBodyCell>
          <TableBodyCell>{item.roles}</TableBodyCell>
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
