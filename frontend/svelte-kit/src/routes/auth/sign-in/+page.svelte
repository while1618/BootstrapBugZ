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
  import { signInSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
  const superform = superForm(data.form, {
    validators: zodClient(signInSchema),
  });
  const { form, errors, enhance } = superform;

  $effect(() => {
    if ($errors._errors) {
      for (const error of $errors._errors) {
        toast.error(error);
      }
    }
  });
</script>

<section>
  <div class="container">
    <div class="m-20 flex items-center justify-center">
      <div class="flex max-w-lg flex-col items-center gap-5">
        <Card.Root class="w-[350px]">
          <Card.Header>
            <Card.Title class="text-2xl">{m.auth_signIn()}</Card.Title>
          </Card.Header>
          <Card.Content>
            <form
              class="flex flex-col gap-2"
              method="POST"
              action="?/signIn"
              use:enhance
              novalidate
            >
              <Form.Field form={superform} name="usernameOrEmail">
                <Form.Control>
                  {#snippet children({ props })}
                    <Label>{m.auth_usernameOrEmail()}</Label>
                    <Input {...props} bind:value={$form.usernameOrEmail} />
                  {/snippet}
                </Form.Control>
                <Form.FieldErrors />
              </Form.Field>

              <Form.Field form={superform} name="password">
                <Form.Control>
                  {#snippet children({ props })}
                    <div class="flex justify-between">
                      <Label>{m.auth_password()}</Label>
                      <a href="/auth/forgot-password" class="text-sm font-medium underline">
                        {m.auth_forgotPasswordQuestion()}
                      </a>
                    </div>
                    <Input type="password" {...props} bind:value={$form.password} />
                  {/snippet}
                </Form.Control>
                <Form.FieldErrors />
              </Form.Field>

              <Form.Button>{m.auth_signIn()}</Form.Button>

              <div class="text-center">
                <Label>{m.auth_doNotHaveAnAccount()}</Label>
                <a href="/auth/sign-up" class="text-sm font-medium underline">
                  {m.auth_signUp()}
                </a>
              </div>
            </form>
          </Card.Content>
        </Card.Root>
      </div>
    </div>
  </div>
</section>
