<script lang="ts">
  import CheckCircleIcon from '$lib/icons/check-circle.svelte';
  import CloseCircleIcon from '$lib/icons/close-circle.svelte';
  import LockCloseIcon from '$lib/icons/lock-close.svelte';
  import LockOpenIcon from '$lib/icons/lock-open.svelte';
  import PencilIcon from '$lib/icons/pencil.svelte';
  import TrashIcon from '$lib/icons/trash.svelte';
  import type { User } from '$lib/models/user/user';
  import type { PageServerData } from './$types';
  import ActivateModal from './activate-modal.svelte';
  import DeleteModal from './delete-modal.svelte';
  import LockModal from './lock-modal.svelte';
  import RoleModal from './role-modal.svelte';

  export let data: PageServerData;
  let activateDialog: HTMLDialogElement;
  let lockDialog: HTMLDialogElement;
  let deleteDialog: HTMLDialogElement;
  let rolesDialog: HTMLDialogElement;
  let selectedUser: User;

  const tableFieldsLabels = [
    'ID',
    'First name',
    'Last name',
    'Username',
    'Email',
    'Created at',
    'Active',
    'Lock',
    'Roles',
    '',
  ];

  const showModal = (dialog: HTMLDialogElement, user: User) => {
    dialog.showModal();
    selectedUser = user;
  };
</script>

<section class="py-10 md:py-16">
  <div class="flex items-center justify-center">
    <div class="card mx-auto w-auto bg-base-200 p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">Users</h1>
        <table class="table table-zebra">
          <thead>
            <tr>
              {#each tableFieldsLabels as label}
                <th>{label}</th>
              {/each}
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
                <th>{new Date(user.createdAt).toLocaleString()}</th>
                <th>
                  <button
                    class={user.active
                      ? 'text-green-600 dark:text-green-500'
                      : 'text-red-600 dark:text-red-500'}
                    on:click|stopPropagation={() => showModal(activateDialog, user)}
                  >
                    {#if user.active}
                      <CheckCircleIcon />
                    {:else}
                      <CloseCircleIcon />
                    {/if}
                  </button>
                </th>
                <th>
                  <button
                    class={user.lock
                      ? 'text-red-600 dark:text-red-500'
                      : 'text-blue-600 dark:text-blue-500'}
                    on:click|stopPropagation={() => showModal(lockDialog, user)}
                  >
                    {#if user.lock}
                      <LockCloseIcon />
                    {:else}
                      <LockOpenIcon />
                    {/if}
                  </button>
                </th>
                <th>
                  {#if user.roles}
                    <div class="flex gap-2">
                      {#each user.roles as role}
                        <span class="badge badge-neutral">{role.name}</span>
                      {/each}
                      <button
                        class="text-blue-600 dark:text-blue-500"
                        on:click|stopPropagation={() => showModal(rolesDialog, user)}
                      >
                        <PencilIcon />
                      </button>
                    </div>
                  {/if}
                </th>
                <th>
                  <button
                    class="text-red-600 dark:text-red-500"
                    on:click|stopPropagation={() => showModal(deleteDialog, user)}
                  >
                    <TrashIcon />
                  </button>
                </th>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    </div>
  </div>
</section>

<ActivateModal bind:activateDialog {selectedUser} />
<LockModal bind:lockDialog {selectedUser} />
<DeleteModal bind:deleteDialog {selectedUser} />
<RoleModal bind:rolesDialog {selectedUser} {data} />
