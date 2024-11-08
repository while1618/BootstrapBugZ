<script lang="ts">
  import * as Form from '$lib/components/ui/form/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import * as m from '$lib/paraglide/messages.js';
  import { userStore } from '$lib/stores/user';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from '../personal/$types';
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

<form method="POST" use:enhance>
  <Form.Field {form} name="username">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.profile_username()}</Form.Label>
        <Input {...props} bind:value={$formData.username} placeholder={$userStore?.username} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Field {form} name="email">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.profile_email()}</Form.Label>
        <Input {...props} bind:value={$formData.email} placeholder={$userStore?.email} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Button>{m.general_update()}</Form.Button>
</form>
