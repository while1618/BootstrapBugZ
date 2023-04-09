<script lang="ts">
  import { enhance } from '$app/forms';
  import { Button, Card, FloatingLabelInput, TabItem, Tabs } from 'flowbite-svelte';
  import type { ActionData } from './$types';

  type UpdateErrors = {
    firstName?: string[] | undefined;
    lastName?: string[] | undefined;
    username?: string[] | undefined;
    email?: string[] | undefined;
  };

  type ChangePasswordErrors = {
    oldPassword?: string[] | undefined;
    newPassword?: string[] | undefined;
    confirmNewPassword?: string[] | undefined;
  };

  export let form: ActionData;

  $: updateErrors = form?.errors as UpdateErrors | undefined;
  $: changePasswordErrors = form?.errors as ChangePasswordErrors | undefined;
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
              {#if updateErrors && updateErrors.firstName}
                <p class="mt-2 text-sm text-red-600">{updateErrors.firstName[0]}</p>
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
              {#if updateErrors && updateErrors.lastName}
                <p class="mt-2 text-sm text-red-600">{updateErrors.lastName[0]}</p>
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
              {#if updateErrors && updateErrors.username}
                <p class="mt-2 text-sm text-red-600">{updateErrors.username[0]}</p>
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
              {#if updateErrors && updateErrors.email}
                <p class="mt-2 text-sm text-red-600">{updateErrors.email[0]}</p>
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
              {#if changePasswordErrors && changePasswordErrors.oldPassword}
                <p class="mt-2 text-sm text-red-600">{changePasswordErrors.oldPassword[0]}</p>
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
              {#if changePasswordErrors && changePasswordErrors.newPassword}
                <p class="mt-2 text-sm text-red-600">{changePasswordErrors.newPassword[0]}</p>
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
              {#if changePasswordErrors && changePasswordErrors.confirmNewPassword}
                <p class="mt-2 text-sm text-red-600">
                  {changePasswordErrors.confirmNewPassword[0]}
                </p>
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
