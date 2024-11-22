<script lang="ts">
  import * as AlertDialog from '$lib/components/ui/alert-dialog';
  import { Button } from '$lib/components/ui/button';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';
  import { Trash2Icon } from 'lucide-svelte';
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

  const superform = superForm(data.deleteForm, {
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
    {#snippet child({ props })}
      <Button {...props} variant="ghost" class="text-red-500 hover:text-red-500/90">
        <Trash2Icon />
      </Button>
    {/snippet}
  </AlertDialog.Trigger>
  <AlertDialog.Content>
    <AlertDialog.Header>
      <AlertDialog.Title>{m.admin_deleteUser()}</AlertDialog.Title>
      <AlertDialog.Description>
        {m.admin_deleteUserConfirmation({ username: user.username })}
      </AlertDialog.Description>
    </AlertDialog.Header>
    <AlertDialog.Footer>
      <AlertDialog.Cancel>{m.general_cancel()}</AlertDialog.Cancel>
      <form method="POST" action="?/delete&id={user.id}" use:enhance>
        <AlertDialog.Action onclick={() => (dialogOpen = false)}>
          {m.general_delete()}
        </AlertDialog.Action>
      </form>
    </AlertDialog.Footer>
  </AlertDialog.Content>
</AlertDialog.Root>
