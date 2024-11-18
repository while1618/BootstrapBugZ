<script lang="ts">
  import * as Card from '$lib/components/ui/card/index.js';
  import * as Form from '$lib/components/ui/form';
  import { Input } from '$lib/components/ui/input/index.js';
  import { Label } from '$lib/components/ui/label/index.js';
  import * as m from '$lib/paraglide/messages.js';
  import { toast } from 'svelte-sonner';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from '../$types';
  import { updateProfileSchema } from '../schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();

  const superform = superForm(data.updateProfileForm, {
    validators: zodClient(updateProfileSchema),
    resetForm: false,
  });
  const { form, message, errors, enhance } = superform;

  $effect(() => {
    if ($message) toast.success($message);
  });
</script>

<Card.Root class="w-[350px]">
  <Card.Content>
    <form class="flex flex-col gap-2" method="POST" action="?/updateProfile" use:enhance novalidate>
      <Form.Field form={superform} name="username">
        <Form.Control>
          {#snippet children({ props })}
            <Label>{m.auth_username()}</Label>
            <Input {...props} bind:value={$form.username} />
          {/snippet}
        </Form.Control>
        <Form.FieldErrors />
      </Form.Field>

      <Form.Field form={superform} name="email">
        <Form.Control>
          {#snippet children({ props })}
            <Label>{m.auth_email()}</Label>
            <Input type="email" {...props} bind:value={$form.email} />
          {/snippet}
        </Form.Control>
        <Form.FieldErrors />
      </Form.Field>

      <Label class="text-[0.8rem] text-destructive">{$errors?._errors}</Label>
      <Form.Button>{m.general_save()}</Form.Button>
    </form>
  </Card.Content>
</Card.Root>
