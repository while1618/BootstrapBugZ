<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';

  export let activateDialog: HTMLDialogElement;
  export let selectedUser: User;
</script>

<Modal
  bind:dialog={activateDialog}
  title={selectedUser?.active ? m.deactivateUser() : m.activateUser()}
>
  <svelte:fragment slot="body">
    <p class="py-4">
      {m.areYouSureYouWantTo()} {selectedUser?.active ? m.deactivate() : m.activate()}
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
          {selectedUser?.active ? m.deactivate() : m.activate()}
        </button>
        <button type="button" class="btn" on:click={() => activateDialog.close()}>
          {m.cancel()}
        </button>
      </div>
    </form>
  </svelte:fragment>
</Modal>
