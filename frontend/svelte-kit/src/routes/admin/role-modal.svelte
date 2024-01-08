<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/modal.svelte';
  import type { UserDTO } from '$lib/models/user/user';
  import type { PageServerData } from './$types';

  export let rolesDialog: HTMLDialogElement;
  export let selectedUser: UserDTO;
  export let data: PageServerData;
</script>

<Modal bind:dialog={rolesDialog} title="Change roles">
  <svelte:fragment slot="body">
    <p class="py-4">Select roles for <strong>{selectedUser?.username}</strong>:</p>
    <form id="roleForm" method="POST" action="?/roles&id={selectedUser?.id}" use:enhance>
      <div class="flex flex-col gap-2">
        {#each data.roles as role}
          <div class="form-control">
            <label class="label cursor-pointer">
              <span class="label-text">{role.name}</span>
              <input type="checkbox" id={role.name} name={role.name} class="checkbox" />
            </label>
          </div>
        {/each}
      </div>
    </form>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <button
      type="submit"
      form="roleForm"
      class="btn btn-neutral"
      on:click={() => rolesDialog.close()}
    >
      Save
    </button>
    <button type="button" class="btn" on:click={() => rolesDialog.close()}>Cancel</button>
  </svelte:fragment>
</Modal>
