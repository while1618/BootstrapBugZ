<script lang="ts">
  import { enhance } from '$app/forms';
  import { Button, Card, FloatingLabelInput, TabItem, Tabs } from 'flowbite-svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
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
              {#if form?.errors?.firstName}
                <p class="mt-2 text-sm text-red-600">{form.errors.firstName}</p>
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
              {#if form?.errors?.lastName}
                <p class="mt-2 text-sm text-red-600">{form.errors.lastName}</p>
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
              {#if form?.errors?.username}
                <p class="mt-2 text-sm text-red-600">{form.errors.username}</p>
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
              {#if form?.errors?.email}
                <p class="mt-2 text-sm text-red-600">{form.errors.email}</p>
              {/if}
              {#if form?.errorMessage}
                {#each form.errorMessage.details as error}
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
              {#if form?.errors?.oldPassword}
                <p class="mt-2 text-sm text-red-600">{form.errors.oldPassword}</p>
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
              {#if form?.errors?.newPassword}
                <p class="mt-2 text-sm text-red-600">{form.errors.newPassword}</p>
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
              {#if form?.errors?.confirmNewPassword}
                <p class="mt-2 text-sm text-red-600">{form.errors.confirmNewPassword}</p>
              {/if}
              {#if form?.errorMessage}
                {#each form.errorMessage.details as error}
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
          <form method="POST" action="/auth/sign-out-from-all-devices">
            <Button type="submit" class="w-2/3">Sign out from all devices</Button>
          </form>
        </div>
      </TabItem>
    </Tabs>
  </Card>
</div>

<style>
</style>
