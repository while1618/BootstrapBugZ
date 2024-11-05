<script lang="ts">
  import * as Form from '$lib/components/ui/form/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import { type PageServerData } from './$types.js';

  interface Props {
    data: PageServerData;
  }

  let { data }: Props = $props();
  const form = superForm(data.form, {
    validators: false,
  });

  const { form: formData, enhance } = form;
</script>

<form method="POST" use:enhance>
  <Form.Field {form} name="username">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.auth_username()}</Form.Label>
        <Input {...props} bind:value={$formData.username} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
  <Form.Field {form} name="email">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.auth_email()}</Form.Label>
        <Input {...props} bind:value={$formData.email} />
      {/snippet}
    </Form.Control>
    <Form.FieldErrors />
  </Form.Field>
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
  <Form.Button>{m.auth_signUp()}</Form.Button>
</form>
