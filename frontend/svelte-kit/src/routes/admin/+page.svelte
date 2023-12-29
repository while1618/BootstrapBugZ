<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/modal.svelte';
  import CheckCircleIcon from '$lib/icons/check-circle.svelte';
  import CloseCircleIcon from '$lib/icons/close-circle.svelte';
  import LockCloseIcon from '$lib/icons/lock-close.svelte';
  import LockOpenIcon from '$lib/icons/lock-open.svelte';
  import PencilIcon from '$lib/icons/pencil.svelte';
  import TrashIcon from '$lib/icons/trash.svelte';
  import type { UserDTO } from '$lib/models/user/user';
  import type { PageServerData } from './$types';

  export let data: PageServerData;
  let dialog: HTMLDialogElement;
  let selectedUser: UserDTO;
</script>

<div class="overflow-x-auto p-10">
  <table class="table table-zebra w-full">
    <thead>
      <tr>
        <th>ID</th>
        <th>First name</th>
        <th>Last name</th>
        <th>Username</th>
        <th>Email</th>
        <th>Activated</th>
        <th>Locked</th>
        <th>Created at</th>
        <th>Roles</th>
        <th />
      </tr>
    </thead>
    <tbody>
      {#each data.users as user}
        <tr>
          <th>{user.id}</th>
          <th>{user.firstName}</th>
          <th>{user.lastName}</th>
          <th>{user.username}</th>
          <th>{user.email}</th>
          <th>
            <form
              method="POST"
              action="?/{user.active ? 'deactivate' : 'activate'}&id={user.id}"
              use:enhance
            >
              {#if user.active}
                <button class=" text-green-600 dark:text-green-500">
                  <CheckCircleIcon />
                </button>
              {:else}
                <button class="text-red-600 dark:text-red-500">
                  <CloseCircleIcon />
                </button>
              {/if}
            </form>
          </th>
          <th>
            <form method="POST" action="?/{user.lock ? 'unlock' : 'lock'}&id={user.id}" use:enhance>
              {#if user.lock}
                <button class="text-red-600 dark:text-red-500">
                  <LockCloseIcon />
                </button>
              {:else}
                <button class="text-blue-600 dark:text-blue-500">
                  <LockOpenIcon />
                </button>
              {/if}
            </form>
          </th>
          <th>{user.createdAt}</th>
          <th>
            {#if user.roles}
              <div class="flex gap-2">
                {#each user.roles as role}
                  {`${role.name} `}
                {/each}
                <button on:click={() => alert('modal')} class="text-blue-600 dark:text-blue-500">
                  <PencilIcon />
                </button>
              </div>
            {/if}
          </th>
          <th>
            <button
              class="text-red-600 dark:text-red-500"
              on:click|stopPropagation={() => {
                dialog.showModal();
                selectedUser = user;
              }}
            >
              <TrashIcon />
            </button>
          </th>
        </tr>
      {/each}
    </tbody>
  </table>
</div>

<Modal bind:dialog title="Delete user">
  <svelte:fragment slot="body">
    <p class="py-4">Are you sure you want to delete {selectedUser?.username}?</p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form method="POST" action="?/delete&id={selectedUser?.id}" use:enhance>
      <button type="submit" class="btn text-error" on:click={() => dialog.close()}>Delete</button>
      <button type="button" class="btn" on:click={() => dialog.close()}>Cancel</button>
    </form>
  </svelte:fragment>
</Modal>
