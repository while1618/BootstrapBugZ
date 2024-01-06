<script lang="ts">
  import { enhance } from '$app/forms';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<div class="flex h-screen items-center justify-center">
  <div class="card mx-auto w-full max-w-xl bg-base-100 p-8 shadow-xl">
    <div class="flex flex-col gap-10">
      <h1 class="text-center text-3xl font-bold">Sign up</h1>
      <form class="flex flex-col gap-4" method="POST" action="?/signUp" use:enhance>
        <div class="form-control w-full">
          <label for="firstName" class="label">
            <span class="label-text">First name</span>
          </label>
          <input type="text" id="firstName" name="firstName" class="input input-bordered w-full" />
          {#if form?.errors?.firstName}
            <label for="firstName" class="label">
              <span class="label-text text-error">{form.errors.firstName[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="lastName" class="label">
            <span class="label-text">Last name</span>
          </label>
          <input type="text" id="lastName" name="lastName" class="input input-bordered w-full" />
          {#if form?.errors?.lastName}
            <label for="lastName" class="label">
              <span class="label-text text-error">{form.errors.lastName[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="username" class="label">
            <span class="label-text">Username</span>
          </label>
          <input type="text" id="username" name="username" class="input input-bordered w-full" />
          {#if form?.errors?.username}
            <label for="username" class="label">
              <span class="label-text text-error">{form.errors.username[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="email" class="label">
            <span class="label-text">Email</span>
          </label>
          <input type="email" id="email" name="email" class="input input-bordered w-full" />
          {#if form?.errors?.email}
            <label for="email" class="label">
              <span class="label-text text-error">{form.errors.email[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="password" class="label">
            <span class="label-text">Password</span>
          </label>
          <input
            type="password"
            id="password"
            name="password"
            class="input input-bordered w-full"
          />
          {#if form?.errors?.password}
            <label for="password" class="label">
              <span class="label-text text-error">{form.errors.password[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="confirmPassword" class="label">
            <span class="label-text">Confirm password</span>
          </label>
          <input
            type="password"
            id="confirmPassword"
            name="confirmPassword"
            class="input input-bordered w-full"
          />
          {#if form?.errors?.confirmPassword}
            <label for="confirmPassword" class="label">
              <span class="label-text text-error">{form.errors.confirmPassword[0]}</span>
            </label>
          {/if}
        </div>

        {#if form?.errorMessage}
          {#each form.errorMessage.details as error}
            <div class="flex">
              <p class="label-text text-error">{error.message}</p>
            </div>
          {/each}
        {/if}

        <button class="btn btn-primary">Sign up</button>
      </form>
    </div>
  </div>
</div>
