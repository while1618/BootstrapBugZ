<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';

  interface Props {
    lockDialog: HTMLDialogElement;
    selectedUser: User;
  }

  let { lockDialog = $bindable(), selectedUser }: Props = $props();
</script>

<Modal
  bind:dialog={lockDialog}
  title={selectedUser?.lock ? m.admin_unlockUser() : m.admin_lockUser()}
>
  {#snippet body()}
    <p class="py-4">
      {selectedUser?.lock
        ? m.admin_unlockUserConfirmation({ username: selectedUser?.username })
        : m.admin_lockUserConfirmation({ username: selectedUser?.username })}
    </p>
  {/snippet}
  {#snippet actions()}
    <form
      method="POST"
      action="?/{selectedUser?.lock ? 'unlock' : 'lock'}&id={selectedUser?.id}"
      use:enhance
    >
      <div class="flex gap-2">
        <button type="submit" class="btn btn-neutral" onclick={() => lockDialog.close()}>
          {selectedUser?.lock ? m.admin_unlock() : m.admin_lock()}
        </button>
        <button type="button" class="btn" onclick={() => lockDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  {/snippet}
</Modal>
