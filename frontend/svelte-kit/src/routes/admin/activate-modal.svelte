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
  title={selectedUser?.active ? m.admin_deactivateUser() : m.admin_activateUser()}
>
  <svelte:fragment slot="body">
    <p class="py-4">
      {selectedUser?.active
        ? m.admin_deactivateUserConfirmation({ username: selectedUser?.username })
        : m.admin_activateUserConfirmation({ username: selectedUser?.username })}
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
          {selectedUser?.active ? m.admin_deactivate() : m.admin_activate()}
        </button>
        <button type="button" class="btn" on:click={() => activateDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  </svelte:fragment>
</Modal>
