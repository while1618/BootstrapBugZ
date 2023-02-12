<script lang="ts">
  import { enhance } from '$app/forms';
  import CheckCircleIcon from '$lib/icons/check-circle.svelte';
  import LockClosedIcon from '$lib/icons/lock-closed.svelte';
  import LockOpenIcon from '$lib/icons/lock-open.svelte';
  import PencilIcon from '$lib/icons/pencil.svelte';
  import TrashIcon from '$lib/icons/trash.svelte';
  import XCircleIcon from '$lib/icons/x-circle.svelte';
  import {
    Button,
    Checkbox,
    Modal,
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
  let changeRoleModal = false;
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
      <TableHeadCell />
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
          <TableBodyCell>
            <form method="POST" action="?/activate&state={user.activated}" use:enhance>
              {#if user.activated}
                <button class=" text-green-600 dark:text-green-500">
                  <CheckCircleIcon />
                </button>
              {:else}
                <button class="text-red-600 dark:text-red-500">
                  <XCircleIcon />
                </button>
              {/if}
            </form>
          </TableBodyCell>
          <TableBodyCell>
            <form method="POST" action="?/lock&state={user.nonLocked}" use:enhance>
              {#if user.nonLocked}
                <button class="text-blue-600 dark:text-blue-500">
                  <LockOpenIcon />
                </button>
              {:else}
                <button class="text-red-600 dark:text-red-500">
                  <LockClosedIcon />
                </button>
              {/if}
            </form>
          </TableBodyCell>
          <TableBodyCell>
            <div class="flex gap-2">
              {#each user.roles as role}
                {`${role.name} `}
              {/each}
              <button
                on:click={() => (changeRoleModal = true)}
                class="text-blue-600 dark:text-blue-500"
              >
                <PencilIcon />
              </button>
            </div>
          </TableBodyCell>
          <TableBodyCell class="w-10">
            <form method="POST" action="?/delete" use:enhance>
              <button class="text-red-600 dark:text-red-500">
                <TrashIcon />
              </button>
            </form>
          </TableBodyCell>
        </TableBodyRow>
      {/each}
    </TableBody>
  </TableSearch>
</div>

<Modal bind:open={changeRoleModal} size="xs" autoclose={false} class="w-full">
  <form class="flex flex-col space-y-6" action="?/change-role">
    <h3 class="p-0 text-xl font-medium text-gray-900 dark:text-white">Change user roles</h3>
    <ul
      class="divide-y divide-gray-200 rounded-lg border border-gray-200 bg-white dark:divide-gray-600 dark:border-gray-600 dark:bg-gray-800"
    >
      <li><Checkbox class="p-3">ADMIN</Checkbox></li>
      <li><Checkbox class="p-3">USER</Checkbox></li>
    </ul>
    <Button type="submit" class="w-full1">Change</Button>
  </form>
</Modal>

<style>
</style>
