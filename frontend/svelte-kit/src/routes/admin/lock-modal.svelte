<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/modal.svelte';
  import type { UserDTO } from '$lib/models/user/user';

  export let lockDialog: HTMLDialogElement;
  export let selectedUser: UserDTO;
</script>

<Modal bind:dialog={lockDialog} title={selectedUser?.lock ? 'Unlock user' : 'Lock user'}>
  <svelte:fragment slot="body">
    <p class="py-4">
      Are you sure you want to {selectedUser?.lock ? 'unlock' : 'lock'}
      <strong>{selectedUser?.username}</strong>?
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
