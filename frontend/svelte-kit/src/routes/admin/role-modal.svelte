<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { User } from '$lib/models/user/user';
  import type { PageServerData } from './$types';
  import * as m from '$lib/paraglide/messages.js';

  export let rolesDialog: HTMLDialogElement;
  export let selectedUser: User;
  export let data: PageServerData;
</script>

<Modal bind:dialog={rolesDialog} title="Change roles">
  <svelte:fragment slot="body">
    <p class="py-4">{m.selectRolesFor()} <strong>{selectedUser?.username}</strong>:</p>
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
      {m.save()}
    </button>
    <button type="button" class="btn" on:click={() => rolesDialog.close()}>{m.cancel()}</button>
  </svelte:fragment>
</Modal>
