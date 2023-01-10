<script lang="ts">
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
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  class="h-6 w-6"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M9.75 9.75l4.5 4.5m0-4.5l-4.5 4.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </a>
              <a href="/" class="font-medium text-blue-600 hover:underline dark:text-blue-500">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  class="h-6 w-6"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z"
                  />
                </svg>
              </a>
              <a href="/" class="font-medium text-red-600 hover:underline dark:text-red-500">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  class="h-6 w-6"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"
                  />
                </svg>
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
