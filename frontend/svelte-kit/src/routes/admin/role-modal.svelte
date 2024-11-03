<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';
  import type { PageServerData } from './$types';

  interface Props {
    rolesDialog: HTMLDialogElement;
    selectedUser: User;
    data: PageServerData;
  }

  let { rolesDialog = $bindable(), selectedUser, data }: Props = $props();
</script>

<Modal bind:dialog={rolesDialog} title="Change roles">
  {#snippet body()}
    <p class="py-4">{m.admin_selectUserRoles({ username: selectedUser?.username })}</p>
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
  {/snippet}
  {#snippet actions()}
    <button
      type="submit"
      form="roleForm"
      class="btn btn-neutral"
      onclick={() => rolesDialog.close()}
    >
      {m.general_save()}
    </button>
    <button type="button" class="btn" onclick={() => rolesDialog.close()}>
      {m.general_cancel()}
    </button>
  {/snippet}
</Modal>
