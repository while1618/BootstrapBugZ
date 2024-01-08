<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/modal.svelte';
  import type { UserDTO } from '$lib/models/user/user';

  export let deleteDialog: HTMLDialogElement;
  export let selectedUser: UserDTO;
</script>

<Modal bind:dialog={deleteDialog} title="Delete user">
  <svelte:fragment slot="body">
    <p class="py-4">Are you sure you want to delete <strong>{selectedUser?.username}</strong>?</p>
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
