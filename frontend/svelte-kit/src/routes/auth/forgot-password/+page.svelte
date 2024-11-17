<script lang="ts">
  import * as Card from '$lib/components/ui/card';
  import * as Form from '$lib/components/ui/form';
  import { Input } from '$lib/components/ui/input';
  import { Label } from '$lib/components/ui/label';
  import * as m from '$lib/paraglide/messages.js';
  import { toast } from 'svelte-sonner';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from './$types';
  import { forgotPasswordSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
  const superform = superForm(data.form, {
    validators: zodClient(forgotPasswordSchema),
  });
  const { form, message, errors, enhance } = superform;

  $effect(() => {
    if ($message) toast.success($message);
  });
</script>

<section>
  <div class="container">
    <div class="m-20 flex items-center justify-center">
      <div class="flex max-w-lg flex-col items-center gap-5">
        <Card.Root class="w-[350px]">
          <Card.Header>
            <Card.Title class="text-2xl">{m.auth_forgotPassword()}</Card.Title>
          </Card.Header>
          <Card.Content>
            <form
              class="flex flex-col gap-2"
              method="POST"
              action="?/forgotPassword"
              use:enhance
              novalidate
            >
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
              <Form.Button>{m.general_send()}</Form.Button>
            </form>
          </Card.Content>
        </Card.Root>
      </div>
    </div>
  </div>
</section>
