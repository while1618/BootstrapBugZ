<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';

  export let lockDialog: HTMLDialogElement;
  export let selectedUser: User;
</script>

<Modal
  bind:dialog={lockDialog}
  title={selectedUser?.lock ? m.admin_unlockUser() : m.admin_lockUser()}
>
  <svelte:fragment slot="body">
    <p class="py-4">
      {selectedUser?.lock ? m.admin_unlockUserConfirmation() : m.admin_lockUserConfirmation()}
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
          {selectedUser?.lock ? m.admin_unlock() : m.admin_lock()}
        </button>
        <button type="button" class="btn" on:click={() => lockDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  </svelte:fragment>
</Modal>
