<script lang="ts">
  import { enhance } from '$app/forms';
  import CheckCircleIcon from '$lib/icons/check-circle.svelte';
  import CloseCircleIcon from '$lib/icons/close-circle.svelte';
  import LockCloseIcon from '$lib/icons/lock-close.svelte';
  import LockOpenIcon from '$lib/icons/lock-open.svelte';
  import PencilIcon from '$lib/icons/pencil.svelte';
  import TrashIcon from '$lib/icons/trash.svelte';
  import type { PageServerData } from './$types';

  export let data: PageServerData;
</script>

<div class="overflow-x-auto p-10">
  <table class="table-zebra table w-full">
    <thead>
      <tr>
        <th>ID</th>
        <th>First name</th>
        <th>Last name</th>
        <th>Username</th>
        <th>Email</th>
        <th>Activated</th>
        <th>Locked</th>
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
              action="?/{user.activated ? 'deactivate' : 'activate'}&usernames={user.username}"
              use:enhance
            >
              {#if user.activated}
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
            <form
              method="POST"
              action="?/{user.nonLocked ? 'lock' : 'unlock'}&usernames={user.username}"
              use:enhance
            >
              {#if user.nonLocked}
                <button class="text-blue-600 dark:text-blue-500">
                  <LockOpenIcon />
                </button>
              {:else}
                <button class="text-red-600 dark:text-red-500">
                  <LockCloseIcon />
                </button>
              {/if}
            </form>
          </th>
          <th>
            <div class="flex gap-2">
              {#each user.roles as role}
                {`${role.name} `}
              {/each}
              <button on:click={() => alert('modal')} class="text-blue-600 dark:text-blue-500">
                <PencilIcon />
              </button>
            </div>
          </th>
          <th>
            <form method="POST" action="?/delete&usernames={user.username}" use:enhance>
              <button class="text-red-600 dark:text-red-500">
                <TrashIcon />
              </button>
            </form>
          </th>
        </tr>
      {/each}
    </tbody>
  </table>
</div>