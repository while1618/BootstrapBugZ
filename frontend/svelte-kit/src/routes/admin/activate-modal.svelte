<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/modal.svelte';
  import type { UserDTO } from '$lib/models/user/user';

  export let activateDialog: HTMLDialogElement;
  export let selectedUser: UserDTO;
</script>

<Modal
  bind:dialog={activateDialog}
  title={selectedUser?.active ? 'Deactivate user' : 'Activate user'}
>
  <svelte:fragment slot="body">
    <p class="py-4">
      Are you sure you want to {selectedUser?.active ? 'deactivate' : 'activate'}
      <strong>{selectedUser?.username}</strong>?
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
