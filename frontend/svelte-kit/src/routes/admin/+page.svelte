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
  let activateDialog: HTMLDialogElement;
  let lockDialog: HTMLDialogElement;
  let deleteDialog: HTMLDialogElement;
  let rolesDialog: HTMLDialogElement;
  let selectedUser: UserDTO;
</script>

<div class="flex h-screen flex-col items-center justify-center">
  <div class="card bg-base-100 mx-auto w-auto p-8 shadow-xl">
    <div class="flex flex-col gap-8">
      <h1 class="text-center text-3xl font-bold">Users</h1>
      <table class="table-zebra table">
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
                {#if user.active}
                  <button
                    class=" text-green-600 dark:text-green-500"
                    on:click|stopPropagation={() => {
                      activateDialog.showModal();
                      selectedUser = user;
                    }}
                  >
                    <CheckCircleIcon />
                  </button>
                {:else}
                  <button
                    class="text-red-600 dark:text-red-500"
                    on:click|stopPropagation={() => {
                      activateDialog.showModal();
                      selectedUser = user;
                    }}
                  >
                    <CloseCircleIcon />
                  </button>
                {/if}
              </th>
              <th>
                {#if user.lock}
                  <button
                    class="text-red-600 dark:text-red-500"
                    on:click|stopPropagation={() => {
                      lockDialog.showModal();
                      selectedUser = user;
                    }}
                  >
                    <LockCloseIcon />
                  </button>
                {:else}
                  <button
                    class="text-blue-600 dark:text-blue-500"
                    on:click|stopPropagation={() => {
                      lockDialog.showModal();
                      selectedUser = user;
                    }}
                  >
                    <LockOpenIcon />
                  </button>
                {/if}
              </th>
              <th>{new Date(user.createdAt).toLocaleString()}</th>
              <th>
                {#if user.roles}
                  <div class="flex gap-2">
                    {#each user.roles as role}
                      {`${role.name} `}
                    {/each}
                    <button
                      on:click|stopPropagation={() => {
                        rolesDialog.showModal();
                        selectedUser = user;
                      }}
                      class="text-blue-600 dark:text-blue-500"
                    >
                      <PencilIcon />
                    </button>
                  </div>
                {/if}
              </th>
              <th>
                <button
                  class="text-red-600 dark:text-red-500"
                  on:click|stopPropagation={() => {
                    deleteDialog.showModal();
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
      <div class="join justify-end">
        <button class="join-item btn btn-active">1</button>
        <button class="join-item btn">2</button>
        <button class="join-item btn">3</button>
        <button class="join-item btn">4</button>
      </div>
    </div>
  </div>
</div>

<Modal
  bind:dialog={activateDialog}
  title={selectedUser?.active ? 'Deactivate user' : 'Activate user'}
>
  <svelte:fragment slot="body">
    <p class="py-4">
      Are you sure you want to {selectedUser?.active ? 'deactivate' : 'activate'}
      {selectedUser?.username}?
    </p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form
      method="POST"
      action="?/{selectedUser?.active ? 'deactivate' : 'activate'}&id={selectedUser?.id}"
      use:enhance
    >
      <div class="flex gap-2">
        <button type="submit" class="btn btn-neutral" on:click={() => activateDialog.close()}>
          {selectedUser?.active ? 'Deactivate' : 'Activate'}
        </button>
        <button type="button" class="btn" on:click={() => activateDialog.close()}>Cancel</button>
      </div>
    </form>
  </svelte:fragment>
</Modal>

<Modal bind:dialog={lockDialog} title={selectedUser?.lock ? 'Unlock user' : 'Lock user'}>
  <svelte:fragment slot="body">
    <p class="py-4">
      Are you sure you want to {selectedUser?.lock ? 'unlock' : 'lock'}
      {selectedUser?.username}?
    </p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form
      method="POST"
      action="?/{selectedUser?.lock ? 'unlock' : 'lock'}&id={selectedUser?.id}"
      use:enhance
    >
      <div class="flex gap-2">
        <button type="submit" class="btn btn-neutral" on:click={() => lockDialog.close()}>
          {selectedUser?.lock ? 'Unlock' : 'Lock'}
        </button>
        <button type="button" class="btn" on:click={() => lockDialog.close()}>Cancel</button>
      </div>
    </form>
  </svelte:fragment>
</Modal>

<Modal bind:dialog={deleteDialog} title="Delete user">
  <svelte:fragment slot="body">
    <p class="py-4">Are you sure you want to delete {selectedUser?.username}?</p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form method="POST" action="?/delete&id={selectedUser?.id}" use:enhance>
      <div class="flex gap-2">
        <button type="submit" class="btn btn-error" on:click={() => deleteDialog.close()}>
          Delete
        </button>
        <button type="button" class="btn" on:click={() => deleteDialog.close()}>Cancel</button>
      </div>
    </form>
  </svelte:fragment>
</Modal>

<Modal bind:dialog={rolesDialog} title="Change roles">
  <svelte:fragment slot="body">
    <p>logic here</p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form method="POST" action="?/roles&id={selectedUser?.id}" use:enhance>
      <div class="flex gap-2">
        <button type="submit" class="btn btn-neutral" on:click={() => rolesDialog.close()}>
          Save
        </button>
        <button type="button" class="btn" on:click={() => rolesDialog.close()}>Cancel</button>
      </div>
    </form>
  </svelte:fragment>
</Modal>
