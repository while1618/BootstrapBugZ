<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';

  export let deleteDialog: HTMLDialogElement;
  export let selectedUser: User;
</script>

<Modal bind:dialog={deleteDialog} title={m.admin_deleteUser()}>
  <svelte:fragment slot="body">
    <p class="py-4">
      {m.admin_deleteUserConfirmation({ username: selectedUser?.username })}
    </p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form method="POST" action="?/delete&id={selectedUser?.id}" use:enhance>
      <div class="flex gap-2">
        <button type="submit" class="btn btn-error" on:click={() => deleteDialog.close()}>
          {m.general_delete()}
        </button>
        <button type="button" class="btn" on:click={() => deleteDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  </svelte:fragment>
</Modal>
