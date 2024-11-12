<script lang="ts">
  import Modal from '$lib/components/shared/modal.svelte';
  import Pagination from '$lib/components/shared/pagination.svelte';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';
  import {
    CheckIcon,
    LockKeyholeIcon,
    LockKeyholeOpenIcon,
    PencilIcon,
    Trash2Icon,
    XIcon,
  } from 'lucide-svelte';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from './$types';
  import { adminSchema, roleSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();

  const activateSuperform = superForm(data.activateForm, {
    validators: zodClient(adminSchema),
  });
  const { errors: activateFormErrors, enhance: activateFormEnhance } = activateSuperform;

  const lockSuperform = superForm(data.lockForm, {
    validators: zodClient(adminSchema),
  });
  const { errors: lockFormErrors, enhance: lockFormEnhance } = lockSuperform;

  const deleteSuperform = superForm(data.deleteForm, {
    validators: zodClient(adminSchema),
  });
  const { errors: deleteFormErrors, enhance: deleteFormEnhance } = deleteSuperform;

  const roleSuperform = superForm(data.roleForm, {
    validators: zodClient(roleSchema),
  });
  const { form: roleForm, errors: roleFormErrors, enhance: roleFormEnhance } = roleSuperform;

  let activateDialog: HTMLDialogElement = $state() as HTMLDialogElement;
  let lockDialog: HTMLDialogElement = $state() as HTMLDialogElement;
  let deleteDialog: HTMLDialogElement = $state() as HTMLDialogElement;
  let rolesDialog: HTMLDialogElement = $state() as HTMLDialogElement;
  let selectedUser: User = $state() as User;

  const tableFieldsLabels = [
    m.admin_userId(),
    m.admin_userUsername(),
    m.admin_userEmail(),
    m.admin_userCreatedAt(),
    m.admin_userActive(),
    m.admin_userLock(),
    m.admin_userRoles(),
    '',
  ];

  const showModal = (dialog: HTMLDialogElement, user: User) => {
    dialog.showModal();
    selectedUser = user;
  };

  const totalPages = $derived(Math.ceil(data.users.total / data.pageable.size));
  const currentPage = $derived(data.pageable.page);
</script>

<section class="py-10 md:py-16">
  <div class="flex items-center justify-center">
    <div class="card mx-auto w-auto bg-base-200 p-8 shadow-xl 2xl:w-2/3">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.admin_users()}</h1>
        <table class="table table-zebra">
          <thead>
            <tr>
              {#each tableFieldsLabels as label}
                <th>{label}</th>
              {/each}
            </tr>
          </thead>
          <tbody>
            {#each data.users.data as user}
              <tr>
                <th>{user.id}</th>
                <th>{user.username}</th>
                <th>{user.email}</th>
                <th>{new Date(user.createdAt).toLocaleString()}</th>
                <th>
                  <button
                    class={user.active
                      ? 'text-green-600 dark:text-green-500'
                      : 'text-red-600 dark:text-red-500'}
                    onclick={(event: Event) => {
                      event.stopPropagation();
                      showModal(activateDialog, user);
                    }}
                  >
                    {#if user.active}
                      <CheckIcon />
                    {:else}
                      <XIcon />
                    {/if}
                  </button>
                </th>
                <th>
                  <button
                    class={user.lock
                      ? 'text-red-600 dark:text-red-500'
                      : 'text-blue-600 dark:text-blue-500'}
                    onclick={(event: Event) => {
                      event.stopPropagation();
                      showModal(lockDialog, user);
                    }}
                  >
                    {#if user.lock}
                      <LockKeyholeIcon />
                    {:else}
                      <LockKeyholeOpenIcon />
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
                        onclick={(event: Event) => {
                          event.stopPropagation();
                          showModal(rolesDialog, user);
                        }}
                      >
                        <PencilIcon />
                      </button>
                    </div>
                  {/if}
                </th>
                <th>
                  <button
                    class="text-red-600 dark:text-red-500"
                    onclick={(event: Event) => {
                      event.stopPropagation();
                      showModal(deleteDialog, user);
                    }}
                  >
                    <Trash2Icon />
                  </button>
                </th>
              </tr>
            {/each}
          </tbody>
        </table>
        <div class="flex items-end justify-end">
          <Pagination {currentPage} {totalPages} size={data.pageable.size} />
        </div>
        <p class="label-text text-center text-xl text-error">
          {$activateFormErrors._errors}
          {$lockFormErrors._errors}
          {$roleFormErrors._errors}
          {$deleteFormErrors._errors}
        </p>
      </div>
    </div>
  </div>
</section>

<Modal
  bind:dialog={activateDialog}
  title={selectedUser?.active ? m.admin_deactivateUser() : m.admin_activateUser()}
>
  {#snippet body()}
    <p class="py-4">
      {selectedUser?.active
        ? m.admin_deactivateUserConfirmation({ username: selectedUser?.username })
        : m.admin_activateUserConfirmation({ username: selectedUser?.username })}
    </p>
  {/snippet}
  {#snippet actions()}
    <form
      method="POST"
      action="?/{selectedUser?.active ? 'deactivate' : 'activate'}&id={selectedUser?.id}"
      use:activateFormEnhance
    >
      <div class="flex gap-2">
        <button type="submit" class="btn btn-neutral" onclick={() => activateDialog.close()}>
          {selectedUser?.active ? m.admin_deactivate() : m.admin_activate()}
        </button>
        <button type="button" class="btn" onclick={() => activateDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  {/snippet}
</Modal>

<Modal
  bind:dialog={lockDialog}
  title={selectedUser?.lock ? m.admin_unlockUser() : m.admin_lockUser()}
>
  {#snippet body()}
    <p class="py-4">
      {selectedUser?.lock
        ? m.admin_unlockUserConfirmation({ username: selectedUser?.username })
        : m.admin_lockUserConfirmation({ username: selectedUser?.username })}
    </p>
  {/snippet}
  {#snippet actions()}
    <form
      method="POST"
      action="?/{selectedUser?.lock ? 'unlock' : 'lock'}&id={selectedUser?.id}"
      use:lockFormEnhance
    >
      <div class="flex gap-2">
        <button type="submit" class="btn btn-neutral" onclick={() => lockDialog.close()}>
          {selectedUser?.lock ? m.admin_unlock() : m.admin_lock()}
        </button>
        <button type="button" class="btn" onclick={() => lockDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  {/snippet}
</Modal>

<Modal bind:dialog={deleteDialog} title={m.admin_deleteUser()}>
  {#snippet body()}
    <p class="py-4">
      {m.admin_deleteUserConfirmation({ username: selectedUser?.username })}
    </p>
  {/snippet}
  {#snippet actions()}
    <form method="POST" action="?/delete&id={selectedUser?.id}" use:deleteFormEnhance>
      <div class="flex gap-2">
        <button type="submit" class="btn btn-error" onclick={() => deleteDialog.close()}>
          {m.general_delete()}
        </button>
        <button type="button" class="btn" onclick={() => deleteDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  {/snippet}
</Modal>

<Modal bind:dialog={rolesDialog} title="Change roles">
  {#snippet body()}
    <p class="py-4">{m.admin_selectUserRoles({ username: selectedUser?.username })}</p>
    <form id="roleForm" method="POST" action="?/roles&id={selectedUser?.id}" use:roleFormEnhance>
      <div class="flex flex-col gap-2">
        {#each data.roleNames as roleName}
          <div class="form-control w-full">
            <label class="label cursor-pointer">
              <span class="label-text">{roleName}</span>
              <input
                type="checkbox"
                name="names"
                value={roleName}
                bind:group={$roleForm.names}
                class="checkbox"
              />
            </label>
          </div>
        {/each}
      </div>
    </form>
  {/snippet}
  {#snippet actions()}
    <button
      type="submit"
      form="roleForm"
      class="btn btn-neutral"
      onclick={() => rolesDialog.close()}
    >
      {m.general_save()}
    </button>
    <button type="button" class="btn" onclick={() => rolesDialog.close()}>
      {m.general_cancel()}
    </button>
  {/snippet}
</Modal>
