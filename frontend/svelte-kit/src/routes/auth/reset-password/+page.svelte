<script lang="ts">
  import { page } from '$app/stores';
  import * as Form from '$lib/components/ui/form/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import type { PageServerData } from './$types';

  interface Props {
    data: PageServerData;
    token: string | null;
  }

  let { data, token = $page.url.searchParams.get('token') }: Props = $props();
  const form = superForm(data.form, {
    validators: false,
  });

  const { form: formData, enhance } = form;
</script>

<form method="POST" action="?/resetPassword&token={token}" use:enhance>
  <Form.Field {form} name="password">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.auth_password()}</Form.Label>
        <Input {...props} bind:value={$formData.password} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Field {form} name="confirmPassword">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.auth_confirmPassword()}</Form.Label>
        <Input {...props} bind:value={$formData.confirmPassword} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Field {form} name="token">
    <Form.FieldErrors />
  </Form.Field>
  <Form.Button>{m.auth_resetPassword()}</Form.Button>
</form>
