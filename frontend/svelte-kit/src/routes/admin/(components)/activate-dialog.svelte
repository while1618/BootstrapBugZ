<script lang="ts">
  import * as AlertDialog from '$lib/components/ui/alert-dialog';
  import { Button } from '$lib/components/ui/button';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';
  import { CheckIcon, XIcon } from 'lucide-svelte';
  import { toast } from 'svelte-sonner';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from '../$types';
  import { adminSchema } from '../schema';

  interface Props {
    data: PageServerData;
    user: User;
  }

  const { data, user }: Props = $props();

  const superform = superForm(data.activateForm, {
    validators: zodClient(adminSchema),
  });
  const { message, errors, enhance } = superform;
  let dialogOpen = $state(false);

  $effect(() => {
    if ($message) toast.success($message);
    if ($errors._errors) {
      for (const error of $errors._errors) {
        toast.error(error);
      }
    }
  });
</script>

<AlertDialog.Root bind:open={dialogOpen}>
  <AlertDialog.Trigger>
    <Button
      variant="ghost"
      class={user.active
        ? 'text-green-500 hover:text-green-500/90'
        : 'text-red-500 hover:text-red-500/90'}
    >
      {#if user.active}
        <CheckIcon />
      {:else}
        <XIcon />
      {/if}
    </Button>
  </AlertDialog.Trigger>
  <AlertDialog.Content>
    <AlertDialog.Header>
      <AlertDialog.Title>
        {user.active ? m.admin_deactivateUser() : m.admin_activateUser()}
      </AlertDialog.Title>
      <AlertDialog.Description>
        {user.active
          ? m.admin_deactivateUserConfirmation({ username: user.username })
          : m.admin_activateUserConfirmation({ username: user.username })}
      </AlertDialog.Description>
    </AlertDialog.Header>
    <AlertDialog.Footer>
      <AlertDialog.Cancel>{m.general_cancel()}</AlertDialog.Cancel>
      <form
        method="POST"
        action="?/{user.active ? 'deactivate' : 'activate'}&id={user.id}"
        use:enhance
      >
        <AlertDialog.Action onclick={() => (dialogOpen = false)}>
          {user.active ? m.admin_deactivate() : m.admin_activate()}
        </AlertDialog.Action>
      </form>
    </AlertDialog.Footer>
  </AlertDialog.Content>
</AlertDialog.Root>
