<script lang="ts">
  import * as Form from '$lib/components/ui/form/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from './$types';
  import { formSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  let { data }: Props = $props();
  const form = superForm(data.form, {
    validators: zodClient(formSchema),
  });

  const { form: formData, enhance } = form;
</script>

<form method="POST" action="?/changePassword" use:enhance>
  <Form.Field {form} name="currentPassword">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.profile_currentPassword()}</Form.Label>
        <Input {...props} bind:value={$formData.currentPassword} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Field {form} name="newPassword">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.profile_newPassword()}</Form.Label>
        <Input {...props} bind:value={$formData.newPassword} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Field {form} name="confirmNewPassword">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.profile_confirmNewPassword()}</Form.Label>
        <Input {...props} bind:value={$formData.confirmNewPassword} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Button>{m.profile_changePassword()}</Form.Button>
</form>

<form method="POST" action="?/delete">
  <Form.Button>{m.profile_delete()}</Form.Button>
</form>
