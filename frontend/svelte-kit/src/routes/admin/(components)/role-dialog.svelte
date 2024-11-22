<script lang="ts">
  import * as AlertDialog from '$lib/components/ui/alert-dialog';
  import { Button } from '$lib/components/ui/button';
  import { Checkbox } from '$lib/components/ui/checkbox';
  import * as Form from '$lib/components/ui/form';
  import { Label } from '$lib/components/ui/label';
  import type { User } from '$lib/models/user/user';
  import * as m from '$lib/paraglide/messages.js';
  import { PencilIcon } from 'lucide-svelte';
  import { toast } from 'svelte-sonner';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from '../$types';
  import { roleSchema } from '../schema';

  interface Props {
    data: PageServerData;
    user: User;
  }

  const { data, user }: Props = $props();

  const superform = superForm(data.roleForm, {
    validators: zodClient(roleSchema),
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
      <Button {...props} variant="ghost" class="text-blue-500 hover:text-blue-500/90">
        <PencilIcon />
      </Button>
    {/snippet}
  </AlertDialog.Trigger>
  <AlertDialog.Content>
    <AlertDialog.Header>
      <AlertDialog.Title>
        {m.admin_selectUserRoles({ username: user.username })}
      </AlertDialog.Title>
      <AlertDialog.Description>
        <form id="roleForm" method="POST" action="?/roles&id={user.id}" use:enhance>
          <div class="flex flex-col gap-3">
            {#each data.roleNames as roleName}
              {@const checked = user.roles?.some((role) => role.name === roleName)}
              <Form.Field form={superform} name="names">
                <Form.Control>
                  {#snippet children({ props })}
                    <div class="flex items-center space-x-2">
                      <Checkbox {...props} id={roleName} value={roleName} {checked} />
                      <Label class="cursor-pointer" for={roleName}>{roleName}</Label>
                    </div>
                  {/snippet}
                </Form.Control>
              </Form.Field>
            {/each}
          </div>
        </form>
      </AlertDialog.Description>
    </AlertDialog.Header>
    <AlertDialog.Footer>
      <AlertDialog.Cancel>{m.general_cancel()}</AlertDialog.Cancel>
      <Button form="roleForm" type="submit" onclick={() => (dialogOpen = false)}>
        {m.general_save()}
      </Button>
    </AlertDialog.Footer>
  </AlertDialog.Content>
</AlertDialog.Root>
