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

<form method="POST" use:enhance>
  <Form.Field {form} name="usernameOrEmail">
    <Form.Control>
      {#snippet children({ props })}
        <Form.Label>{m.auth_usernameOrEmail()}</Form.Label>
        <Input {...props} bind:value={$formData.usernameOrEmail} />
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
  <div class="flex gap-4">
    <span class="inline-block text-sm">
      {m.auth_doNotHaveAnAccount()}
      <a href="/auth/sign-up" class="ml-auto underline">
        {m.auth_signUp()}
      </a>
    </span>
    <a href="/auth/forgot-password" class="ml-auto inline-block text-sm underline">
      {m.auth_forgotPassword()}
    </a>
  </div>
  <Form.Button>{m.auth_signIn()}</Form.Button>
</form>
