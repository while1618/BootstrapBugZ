<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';

  interface Props {
    deleteDialog: HTMLDialogElement;
    selectedUser: User;
  }

  let { deleteDialog = $bindable(), selectedUser }: Props = $props();
</script>

<Modal bind:dialog={deleteDialog} title={m.admin_deleteUser()}>
  {#snippet body()}
    <p class="py-4">
      {m.admin_deleteUserConfirmation({ username: selectedUser?.username })}
    </p>
  {/snippet}
  {#snippet actions()}
    <form method="POST" action="?/delete&id={selectedUser?.id}" use:enhance>
      <div class="flex gap-2">
        <button type="submit" class="btn btn-error" onclick={() => deleteDialog.close()}>
          {m.general_delete()}
        </button>
        <button type="button" class="btn" onclick={() => deleteDialog.close()}>
          {m.general_cancel()}
        </button>
      </div>
    </form>
  {/snippet}
</Modal>
