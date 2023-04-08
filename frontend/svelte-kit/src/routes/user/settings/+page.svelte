<!-- TODO: fix typings -->
<script lang="ts">
  import { enhance } from '$app/forms';
  import { Button, Card, FloatingLabelInput, TabItem, Tabs } from 'flowbite-svelte';
  import type { ActionData } from './$types';

  type NameErrors = {
    firstName?: string[] | undefined;
    lastName?: string[] | undefined;
    username?: string[] | undefined;
    email?: string[] | undefined;
  };

  type PasswordErrors = {
    oldPassword?: string[] | undefined;
    newPassword?: string[] | undefined;
    confirmNewPassword?: string[] | undefined;
  };

  export let form: ActionData;

  $: nameErrors = form?.errors as NameErrors | undefined;
  $: passwordErrors = form?.errors as PasswordErrors | undefined;
</script>

<div class="flex justify-center pt-10">
  <Card
    size="md"
    color="none"
    class="border-gray-100 bg-white dark:border-gray-700 dark:bg-gray-800"
  >
    <Tabs style="underline" class="justify-center">
      <TabItem open title="Update profile">
        <div class="flex justify-center">
          <form class="flex w-2/3 flex-col space-y-6" method="POST" action="?/update" use:enhance>
            <div>
              <FloatingLabelInput
                class="space-y-2"
                style="standard"
                id="firstName"
                name="firstName"
                type="text"
                label="First name"
              />
              {#if nameErrors}
                <p class="mt-2 text-sm text-red-600">{nameErrors.firstName?.[0]}</p>
              {/if}
            </div>
            <div>
              <FloatingLabelInput
                class="space-y-2"
                style="standard"
                id="lastName"
                name="lastName"
                type="text"
                label="Last name"
              />
              {#if nameErrors}
                <p class="mt-2 text-sm text-red-600">{nameErrors.lastName?.[0]}</p>
              {/if}
            </div>
            <div>
              <FloatingLabelInput
                class="space-y-2"
                style="standard"
                id="username"
                name="username"
                type="text"
                label="Username"
              />
              {#if nameErrors}
                <p class="mt-2 text-sm text-red-600">{nameErrors.username?.[0]}</p>
              {/if}
            </div>
            <div>
              <FloatingLabelInput
                class="space-y-2"
                style="standard"
                id="email"
                name="email"
                type="email"
                label="Email"
              />
              {#if nameErrors}
                <p class="mt-2 text-sm text-red-600">{nameErrors.email?.[0]}</p>
              {/if}
              {#if form?.updateErrorMessage}
                {#each form.updateErrorMessage.details as error}
                  <p class="mt-2 text-sm text-red-600">{error.message}</p>
                {/each}
              {/if}
            </div>
            <Button type="submit">Update</Button>
          </form>
        </div>
      </TabItem>
      <TabItem title="Change password">
        <div class="flex justify-center">
          <form
            class="flex w-2/3 flex-col space-y-6"
            method="POST"
            action="?/changePassword"
            use:enhance
          >
            <div>
              <FloatingLabelInput
                class="space-y-2"
                style="standard"
                id="oldPassword"
                name="oldPassword"
                type="password"
                label="Old password"
              />
              {#if passwordErrors}
                <p class="mt-2 text-sm text-red-600">{passwordErrors.oldPassword?.[0]}</p>
              {/if}
            </div>
            <div>
              <FloatingLabelInput
                class="space-y-2"
                style="standard"
                id="newPassword"
                name="newPassword"
                type="password"
                label="New password"
              />
              {#if passwordErrors}
                <p class="mt-2 text-sm text-red-600">{passwordErrors.newPassword?.[0]}</p>
              {/if}
            </div>
            <div>
              <FloatingLabelInput
                class="space-y-2"
                style="standard"
                id="confirmNewPassword"
                name="confirmNewPassword"
                type="password"
                label="Confirm password"
              />
              {#if passwordErrors}
                <p class="mt-2 text-sm text-red-600">{passwordErrors.confirmNewPassword?.[0]}</p>
              {/if}
              {#if form?.changePasswordErrorMessage}
                {#each form.changePasswordErrorMessage.details as error}
                  <p class="mt-2 text-sm text-red-600">{error.message}</p>
                {/each}
              {/if}
            </div>
            <Button type="submit">Change password</Button>
          </form>
        </div>
      </TabItem>
      <TabItem title="Logged in devices">
        <div class="flex justify-center">
          <Button href="/auth/sign-out-from-all-devices">Sign out from all devices</Button>
        </div>
      </TabItem>
    </Tabs>
  </Card>
</div>

<style>
</style>
